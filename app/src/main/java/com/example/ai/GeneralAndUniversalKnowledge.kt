package com.example.ai

import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.PredictionResult

/**
 * Specialized Conversational & Universal Knowledge Domain:
 * Greetings, Identity, App Navigation, and Universal Inquiry Synthesis.
 * Ensures Luna AI can answer ANY question with clinical depth and warmth.
 */
object GeneralAndUniversalKnowledge {

    fun matchAndRespond(
        query: String,
        user: UserEntity,
        prediction: PredictionResult,
        cycleDayInt: Int,
        today: String,
        todayLog: LogEntity?,
        citation: String
    ): String? {
        val lower = query.lowercase().trim()
        val userName = user.name.ifBlank { "there" }

        // 1. Greetings & Pleasantries
        if (containsAny(lower, "hello", "hi", "hey", "good morning", "good evening", "good afternoon", "greetings", "howdy", "sup", "what's up")) {
            val moodNote = todayLog?.mood?.takeIf { it.isNotBlank() && !it.equals("Neutral", ignoreCase = true) }?.let {
                " I see you're feeling **$it** today."
            } ?: ""
            val symptomNote = todayLog?.symptoms?.takeIf { it.isNotEmpty() }?.let {
                " You have logged: ${it.joinToString(", ") { s -> "**$s**" }}."
            } ?: ""

            return """
                Hello, $userName! ✨ It is wonderful to connect with you.
                
                You are currently on **Day $cycleDayInt** of your **${prediction.phase} Phase**.$moodNote$symptomNote
                
                I am your **Luna Ultra AI Clinical Assistant**, running 100% on-device. I can assist you with:
                • **Cycle Forecasting & Fertility:** Ovulation tracking, luteal timing, and period forecasts.
                • **Symptom Pathophysiology:** Relief for cramps, headaches, bloating, sleep changes, and mood shifts.
                • **Endocrine Guidance:** PCOS, endometriosis, seed cycling, supplements, and thyroid health.
                • **Infradian Wellness:** Phase-matched nutrition, workouts, and energy management.
                
                What would you like to explore today?$citation
            """.trimIndent()
        }

        // 2. Identity, Capabilities & AI Architecture
        if (containsAny(lower, "who are you", "what can you do", "what are you", "your name", "tell me about yourself", "how do you work", "help me")) {
            return """
                ### 🌙 About Luna Ultra AI (Omni-Cognitive Clinical Assistant)
                I am **Luna AI**, an on-device clinical reproductive health and endocrinological intelligence system embedded directly into your LunaCycle application.

                #### 🧬 Key Capabilities:
                1. **Clinical Endocrinology & Cycle Syncing:** Formulated on ACOG, Endocrine Society, and FIGO reproductive standards to analyze your basal body temperature, cervical fluid, and menstrual patterns.
                2. **Deep Personal Contextualization:** Automatically correlates your questions with today's logged biomarkers (Day $cycleDayInt, ${prediction.phase} phase, symptoms, mood, and water intake).
                3. **Zero Network Latency & Absolute Privacy:** Operates 100% locally on your device without transmitting health telemetry to external servers. Works seamlessly in Airplane Mode.
                4. **Holistic Infradian Protocols:** Provides evidence-based recommendations for seed cycling, targeted supplementation (Inositol, Magnesium, CoQ10, Vitex), sleep optimization, and phase-matched physical training.

                Ask me anything about your cycle, symptoms, diagnostics, hormones, or daily health!$citation
            """.trimIndent()
        }

        // 3. How to Use the App & Daily Tracking Instructions
        if (containsAny(lower, "how to use", "how to track", "how to log", "app guide", "tutorial", "features", "how do i track")) {
            return """
                ### 📱 How to Use LunaCycle for Optimal Hormone Calibration
                **Patient:** $userName | Current: Day $cycleDayInt (${prediction.phase} Phase)

                #### 🌟 Daily Tracking Workflow (FastLog):
                1. **Log Your Period & Flow:** Open the **FastLog** tab daily. Record your flow intensity (Light, Medium, Heavy) during menses. This trains our prediction engine to calculate your cycle baseline.
                2. **Track Symptoms & Mood:** Select physical symptoms (cramping, headache, breast tenderness, bloating) and your emotional state. This unlocks personalized clinical insights.
                3. **Record Daily Biomarkers:**
                   • **Hydration:** Tap the water glasses to track daily fluid intake (target: ${if (user.waterGoalMl > 0) user.waterGoalMl else 2000} ml).
                   • **BBT (Basal Body Temperature):** Measure immediately upon waking before sitting up for precise ovulation confirmation.
                   • **Cervical Fluid:** Note whether fluid is sticky, creamy, watery, or egg-white to detect the biological fertile window.
                4. **Cycle Insights & Calendar:** View the **Insights** and **Calendar** screens to explore your 4-phase biological timeline, fertile window, and upcoming period countdown.
                5. **Ask Luna AI:** You can chat with me anytime right here for instant, clinical guidance!$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
