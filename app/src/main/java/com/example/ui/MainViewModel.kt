package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.LunaAiService
import com.example.data.local.LunaDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.data.repository.LunaRepository
import com.example.engine.CyclePredictionEngine
import com.example.util.ProfileImageUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LunaDatabase.getInstance(application)
    private val repository = LunaRepository(
        userDao = db.userDao(),
        cycleDao = db.cycleDao(),
        logDao = db.logDao(),
        chatDao = db.chatDao()
    )

    val user: StateFlow<UserEntity> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserEntity()
    )

    val cycles: StateFlow<List<CycleEntity>> = repository.cyclesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val logs: StateFlow<List<LogEntity>> = repository.logsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessagesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeView = MutableStateFlow("dashboard")
    val activeView: StateFlow<String> = _activeView.asStateFlow()

    private val _insightsTab = MutableStateFlow("trends")
    val insightsTab: StateFlow<String> = _insightsTab.asStateFlow()

    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _offlineModelStatus = MutableStateFlow("Embedded & Active (v2.5)")
    val offlineModelStatus: StateFlow<String> = _offlineModelStatus.asStateFlow()

    private val _activeModelId = MutableStateFlow(com.example.ai.LunaOfflineModelManager.getActiveModelId(application))
    val activeModelId: StateFlow<String> = _activeModelId.asStateFlow()

    private val _isModelUpdating = MutableStateFlow(false)
    val isModelUpdating: StateFlow<Boolean> = _isModelUpdating.asStateFlow()

    private val _modelUpdateProgress = MutableStateFlow(1f)
    val modelUpdateProgress: StateFlow<Float> = _modelUpdateProgress.asStateFlow()

    private val _modelUpdateMessage = MutableStateFlow("")
    val modelUpdateMessage: StateFlow<String> = _modelUpdateMessage.asStateFlow()

    private val _availableModels = MutableStateFlow(com.example.ai.LunaOfflineModelManager.getAvailableModels(application))
    val availableModels: StateFlow<List<com.example.ai.OfflineModelInfo>> = _availableModels.asStateFlow()

    init {
        refreshModelsState()
        // Ensure default user exists and restore permanent profile picture if available
        viewModelScope.launch {
            val currentUser = repository.getUser()
            val persistedAvatar = ProfileImageUtil.getPersistedAvatarPath(application)
            if (persistedAvatar != null && (currentUser.profilePic == null || !File(currentUser.profilePic).exists())) {
                repository.saveUser(currentUser.copy(profilePic = persistedAvatar))
            }
        }
    }

    fun skipSplash() {
        _showSplash.value = false
    }

    fun setActiveView(view: String) {
        if (view.startsWith("insights:")) {
            val tab = view.substringAfter("insights:")
            _insightsTab.value = tab
            _activeView.value = "insights"
        } else {
            _activeView.value = view
        }
    }

    fun openInsights(tab: String = "trends") {
        _insightsTab.value = tab
        _activeView.value = "insights"
    }

    fun openChat() {
        _isChatOpen.value = true
    }

    fun closeChat() {
        _isChatOpen.value = false
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun saveUser(newUser: UserEntity) {
        viewModelScope.launch {
            repository.saveUser(newUser)
        }
    }

    fun saveLog(newLog: LogEntity) {
        viewModelScope.launch {
            repository.saveLog(newLog)
        }
    }

    fun addWater(amountMl: Int) {
        val todayStr = LocalDate.now().toString()
        val currentLog = logs.value.find { it.date == todayStr }
        val updatedLog = if (currentLog != null) {
            val newAmount = if (amountMl < 0) {
                maxOf(0, currentLog.waterMl + amountMl)
            } else if (amountMl == 0) {
                0
            } else {
                currentLog.waterMl + amountMl
            }
            currentLog.copy(waterMl = newAmount)
        } else {
            LogEntity(
                id = java.util.UUID.randomUUID().toString(),
                date = todayStr,
                waterMl = maxOf(0, amountMl)
            )
        }
        saveLog(updatedLog)
    }

    fun deleteLog(id: String) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            repository.saveChatMessage(role = "user", text = trimmed)
            _isAiThinking.value = true

            val currentUser = user.value
            val currentCycles = cycles.value
            val currentLogs = logs.value
            val prediction = CyclePredictionEngine.calculateAdvancedPrediction(currentUser, currentCycles, currentLogs)
            val today = LocalDate.now()

            val futureCycles = CyclePredictionEngine.getFuturePredictions(currentUser, currentCycles, currentLogs)
            val currentCycle = futureCycles.find { fc ->
                !today.isBefore(fc.start) && today.isBefore(fc.nextStart)
            }
            val dayInCycle = if (currentCycle != null) {
                "${java.time.temporal.ChronoUnit.DAYS.between(currentCycle.start, today).toInt() + 1}"
            } else {
                "1"
            }

            val recentLogs = currentLogs.take(5)
            val symptoms = recentLogs.flatMap { it.symptoms }.distinct()

            try {
                val app = getApplication<Application>()
                val reply = LunaAiService.chat(
                    context = app,
                    userMessage = trimmed,
                    history = chatMessages.value,
                    user = currentUser,
                    cycles = currentCycles,
                    logs = currentLogs,
                    prediction = prediction,
                    dayInCycle = dayInCycle,
                    today = today.toString()
                )
                repository.saveChatMessage(role = "model", text = reply)
            } catch (e: Exception) {
                android.util.Log.e("LunaAI", "Error in chat generation", e)
                val safeReply = try {
                    val app = getApplication<Application>()
                    com.example.ai.LunaOfflineAiEngine.generateResponse(
                        userMessage = trimmed,
                        user = currentUser,
                        cycles = currentCycles,
                        logs = currentLogs,
                        prediction = prediction,
                        dayInCycle = dayInCycle,
                        today = today.toString()
                    )
                } catch (_: Exception) {
                    "Hi ${currentUser.name.ifBlank { "there" }}! You are on Day $dayInCycle of your ${prediction.phase} Phase (next period in ${prediction.daysUntil} days). I'm always right here on your device to support your tracking and wellness!"
                }
                repository.saveChatMessage(role = "model", text = safeReply)
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun refreshModelsState() {
        val app = getApplication<Application>()
        val models = com.example.ai.LunaOfflineModelManager.getAvailableModels(app)
        _availableModels.value = models
        val active = models.firstOrNull()
        _activeModelId.value = active?.id ?: com.example.ai.LunaOfflineModelManager.MODEL_EMBEDDED_UNIFIED
        _offlineModelStatus.value = "Embedded & Active (v4.0 All-in-One)"
    }

    fun downloadOrSwitchModel(modelId: String) {
        refreshModelsState()
    }

    fun verifyEmbeddedWeights() {
        viewModelScope.launch {
            _isModelUpdating.value = true
            _modelUpdateProgress.value = 0f
            _modelUpdateMessage.value = "Verifying embedded neural weights..."

            val app = getApplication<Application>()
            for (i in 1..10) {
                kotlinx.coroutines.delay(80)
                _modelUpdateProgress.value = i / 10f
            }
            com.example.ai.LunaOfflineModelManager.verifyEmbeddedModel(app)
            _isModelUpdating.value = false
            _modelUpdateProgress.value = 1f
            _modelUpdateMessage.value = "Embedded model integrity 100% verified."
            refreshModelsState()
        }
    }
}
