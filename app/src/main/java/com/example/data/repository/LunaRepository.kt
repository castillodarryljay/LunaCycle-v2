package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.CycleDao
import com.example.data.local.LogDao
import com.example.data.local.UserDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class LunaRepository(
    private val userDao: UserDao,
    private val cycleDao: CycleDao,
    private val logDao: LogDao,
    private val chatDao: ChatDao
) {
    val userFlow: Flow<UserEntity> = userDao.getUserFlow().map { it ?: UserEntity() }
    val cyclesFlow: Flow<List<CycleEntity>> = cycleDao.getAllCyclesFlow()
    val logsFlow: Flow<List<LogEntity>> = logDao.getAllLogsFlow()
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = chatDao.getChatMessagesFlow()

    suspend fun getUser(): UserEntity {
        return userDao.getUserSync() ?: UserEntity().also { userDao.saveUser(it) }
    }

    suspend fun saveUser(user: UserEntity) {
        userDao.saveUser(user)
    }

    suspend fun saveLog(log: LogEntity) {
        logDao.insertLog(log)
        val allLogs = logDao.getAllLogsSync()
        syncCyclesFromLogs(allLogs)
    }

    suspend fun deleteLog(id: String) {
        logDao.deleteLogById(id)
        val allLogs = logDao.getAllLogsSync()
        syncCyclesFromLogs(allLogs)
    }

    suspend fun resetAllData() {
        cycleDao.deleteAll()
        logDao.deleteAll()
        chatDao.deleteAll()
        val defaultUser = UserEntity()
        userDao.saveUser(defaultUser)
    }

    suspend fun saveChatMessage(role: String, text: String) {
        chatDao.insertMessage(ChatMessageEntity(role = role, text = text))
    }

    suspend fun clearChat() {
        chatDao.deleteAll()
    }

    private suspend fun syncCyclesFromLogs(updatedLogs: List<LogEntity>) {
        val periodStarts = updatedLogs.filter { it.isPeriod }.sortedBy { it.date }
        val periodEnds = updatedLogs.filter { it.isPeriodEnd }.sortedBy { it.date }

        val newCycles = mutableListOf<CycleEntity>()

        periodStarts.forEachIndexed { index, startLog ->
            val nextStart = periodStarts.getOrNull(index + 1)
            val matchingEnd = periodEnds.firstOrNull { endLog ->
                endLog.date >= startLog.date && (nextStart == null || endLog.date < nextStart.date)
            }

            var length = 0
            if (nextStart != null) {
                try {
                    val s = LocalDate.parse(startLog.date)
                    val n = LocalDate.parse(nextStart.date)
                    length = ChronoUnit.DAYS.between(s, n).toInt()
                } catch (_: Exception) {}
            }

            newCycles.add(
                CycleEntity(
                    id = UUID.randomUUID().toString().take(9),
                    startDate = startLog.date,
                    endDate = matchingEnd?.date,
                    length = length
                )
            )
        }

        cycleDao.deleteAll()
        cycleDao.insertCycles(newCycles)
    }
}
