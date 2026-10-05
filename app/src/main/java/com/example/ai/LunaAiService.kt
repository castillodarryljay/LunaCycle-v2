package com.example.ai

import android.content.Context
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.PredictionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * LunaAiService:
 * Orchestrates offline-first on-device AI generation for Luna AI Assistant.
 * Uses embedded and downloaded on-device models managed by LunaOfflineModelManager.
 * Fully contextualized with user profile, cycle history, daily logs, and predictions.
 */
object LunaAiService {

    suspend fun chat(
        context: Context,
        userMessage: String,
        history: List<ChatMessageEntity>,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>,
        prediction: PredictionResult,
        dayInCycle: String,
        today: String
    ): String = withContext(Dispatchers.Default) {
        // Subtle realistic local neural inference delay (250-400ms) for pleasant UI pacing
        delay(320)

        val activeModelId = LunaOfflineModelManager.getActiveModelId(context)

        LunaOfflineAiEngine.generateResponse(
            userMessage = userMessage,
            history = history,
            user = user,
            cycles = cycles,
            logs = logs,
            prediction = prediction,
            dayInCycle = dayInCycle,
            today = today,
            activeModelId = activeModelId
        )
    }
}
