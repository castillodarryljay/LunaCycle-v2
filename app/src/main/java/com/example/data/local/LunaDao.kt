package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)
}

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycles ORDER BY startDate ASC")
    fun getAllCyclesFlow(): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles ORDER BY startDate ASC")
    suspend fun getAllCyclesSync(): List<CycleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycles(cycles: List<CycleEntity>)

    @Query("DELETE FROM cycles")
    suspend fun deleteAll()
}

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY date DESC")
    fun getAllLogsFlow(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY date DESC")
    suspend fun getAllLogsSync(): List<LogEntity>

    @Query("SELECT * FROM logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: String): LogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity)

    @Query("DELETE FROM logs WHERE id = :id")
    suspend fun deleteLogById(id: String)

    @Query("DELETE FROM logs")
    suspend fun deleteAll()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()
}
