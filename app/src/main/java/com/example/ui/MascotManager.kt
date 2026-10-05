package com.example.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

enum class MascotEmotion(
    val id: String,
    @DrawableRes val drawableRes: Int,
    val title: String,
    val emoji: String,
    val glowColor: Color,
    val badgeLabel: String
) {
    NORMAL(
        id = "normal",
        drawableRes = R.drawable.mascot_normal,
        title = "Balanced & Calm",
        emoji = "🌱",
        glowColor = Color(0xFF6C5CE7),
        badgeLabel = "BALANCED"
    ),
    HAPPY(
        id = "happy",
        drawableRes = R.drawable.mascot_happy,
        title = "Radiant & Glowing",
        emoji = "✨",
        glowColor = Color(0xFFFF9FF3),
        badgeLabel = "RADIANT"
    ),
    EXCITED(
        id = "excited",
        drawableRes = R.drawable.mascot_excited,
        title = "High Energy & Drive",
        emoji = "⚡",
        glowColor = Color(0xFFFECA57),
        badgeLabel = "ENERGIZED"
    ),
    IN_LOVE(
        id = "in_love",
        drawableRes = R.drawable.mascot_in_love,
        title = "Full of Love & Affection",
        emoji = "💖",
        glowColor = Color(0xFFFF6B6B),
        badgeLabel = "IN LOVE"
    ),
    RELIEF(
        id = "relief",
        drawableRes = R.drawable.mascot_relief,
        title = "Peaceful & Refreshed",
        emoji = "🌿",
        glowColor = Color(0xFF1DD1A1),
        badgeLabel = "REFRESHED"
    ),
    CRAMPY(
        id = "crampy",
        drawableRes = R.drawable.mascot_crampy,
        title = "Cozy In A Blanket",
        emoji = "☕",
        glowColor = Color(0xFFFF7675),
        badgeLabel = "COZY REST"
    ),
    BLOATED(
        id = "bloated",
        drawableRes = R.drawable.mascot_bloated,
        title = "Gentle Fluid Flush",
        emoji = "🍵",
        glowColor = Color(0xFF48DBFB),
        badgeLabel = "PUFFY CARE"
    ),
    FATIGUED(
        id = "fatigued",
        drawableRes = R.drawable.mascot_fatigued,
        title = "Deep Rest Mode",
        emoji = "🌙",
        glowColor = Color(0xFFA29BFE),
        badgeLabel = "RESTING"
    ),
    SLEEPING(
        id = "sleeping",
        drawableRes = R.drawable.mascot_sleeping,
        title = "Dreamy Sleep",
        emoji = "💤",
        glowColor = Color(0xFF576574),
        badgeLabel = "DREAMING"
    ),
    CRAVINGS(
        id = "cravings",
        drawableRes = R.drawable.mascot_cravings,
        title = "Sweet Tooth Cravings",
        emoji = "🍫",
        glowColor = Color(0xFFE17055),
        badgeLabel = "CRAVINGS"
    ),
    HUNGRY(
        id = "hungry",
        drawableRes = R.drawable.mascot_hungry,
        title = "Metabolic Nourish",
        emoji = "🥑",
        glowColor = Color(0xFF00B894),
        badgeLabel = "NOURISH"
    ),
    PILL_DAY(
        id = "pill_day",
        drawableRes = R.drawable.mascot_pill_day,
        title = "Pill Routine Complete",
        emoji = "💊",
        glowColor = Color(0xFF0984E3),
        badgeLabel = "MEDS & PILL"
    ),
    EMOTIONAL(
        id = "emotional",
        drawableRes = R.drawable.mascot_emotional,
        title = "Tender Feelings",
        emoji = "🥺",
        glowColor = Color(0xFFD6A2E8),
        badgeLabel = "TENDER"
    ),
    SAD(
        id = "sad",
        drawableRes = R.drawable.mascot_sad,
        title = "Soft Rain & Hugs",
        emoji = "🌧️",
        glowColor = Color(0xFF74B9FF),
        badgeLabel = "GENTLE HUGS"
    ),
    ANGRY(
        id = "angry",
        drawableRes = R.drawable.mascot_angry,
        title = "Spicy Energy",
        emoji = "🔥",
        glowColor = Color(0xFFEE5253),
        badgeLabel = "FIERY"
    ),
    IRRITABLE(
        id = "irritable",
        drawableRes = R.drawable.mascot_irritable,
        title = "Need Space & Quiet",
        emoji = "⚡",
        glowColor = Color(0xFFFF9F43),
        badgeLabel = "SOLO TIME"
    ),
    SCARED(
        id = "scared",
        drawableRes = R.drawable.mascot_scared,
        title = "Calm Deep Breath",
        emoji = "🫂",
        glowColor = Color(0xFF8395A7),
        badgeLabel = "GROUNDING"
    ),
    CONFUSED(
        id = "confused",
        drawableRes = R.drawable.mascot_confused,
        title = "Curious & Exploring",
        emoji = "🔍",
        glowColor = Color(0xFF54A0FF),
        badgeLabel = "CURIOUS"
    );

    companion object {
        fun fromMoodString(mood: String?): MascotEmotion {
            return when (mood?.trim()?.lowercase()) {
                "happy" -> HAPPY
                "sad" -> SAD
                "angry" -> ANGRY
                "anxious" -> SCARED
                "calm" -> RELIEF
                "bloated" -> BLOATED
                "energetic" -> EXCITED
                "low" -> FATIGUED
                "in love", "in_love" -> IN_LOVE
                "cravings" -> CRAVINGS
                "hungry" -> HUNGRY
                "emotional" -> EMOTIONAL
                "irritable" -> IRRITABLE
                "sleepy" -> SLEEPING
                "pill", "pill_day" -> PILL_DAY
                "confused" -> CONFUSED
                else -> NORMAL
            }
        }

        fun fromSymptom(symptom: String?): MascotEmotion? {
            return when (symptom?.trim()?.lowercase()) {
                "cramps" -> CRAMPY
                "bloating" -> BLOATED
                "fatigue" -> FATIGUED
                "headache" -> IRRITABLE
                "acne" -> EMOTIONAL
                "backache" -> CRAMPY
                "nausea" -> RELIEF
                "cravings" -> CRAVINGS
                else -> null
            }
        }

        fun resolveEmotion(
            symptoms: List<String>,
            mood: String?,
            phase: String,
            waterMl: Int,
            waterGoal: Int,
            isPeriod: Boolean
        ): MascotEmotion {
            // Priority 1: Acute Symptoms
            if (symptoms.any { it.contains("Cramp", ignoreCase = true) }) return CRAMPY
            if (symptoms.any { it.contains("Bloat", ignoreCase = true) }) return BLOATED
            if (symptoms.any { it.contains("Fatigue", ignoreCase = true) }) return FATIGUED
            if (symptoms.any { it.contains("Headache", ignoreCase = true) }) return IRRITABLE

            // Priority 2: User Reported Mood
            if (!mood.isNullOrBlank() && !mood.equals("Neutral", ignoreCase = true)) {
                val mapped = fromMoodString(mood)
                if (mapped != NORMAL) return mapped
            }

            // Priority 3: Water goal completion
            if (waterGoal > 0 && waterMl >= waterGoal) return RELIEF

            // Priority 4: Cycle Phase
            return when (phase) {
                "Menstrual" -> if (isPeriod) CRAMPY else NORMAL
                "Follicular" -> HAPPY
                "Ovulatory" -> EXCITED
                "Luteal" -> if (symptoms.isNotEmpty()) CRAVINGS else NORMAL
                else -> NORMAL
            }
        }

        fun getSpeechBubbleMessage(emotion: MascotEmotion, phase: String, userName: String): String {
            val name = userName.ifBlank { "dear" }
            return when (emotion) {
                NORMAL -> "Listening to your body today, $name! Navigating your $phase phase with balanced calm."
                HAPPY -> "Radiant smile! Estrogen and endorphins are singing together in harmony today ✨"
                EXCITED -> "High voltage energy! Your focus and physical strength are at their absolute peak ⚡"
                IN_LOVE -> "Heart flutter moments! Oxytocin and dopamine are filling your space with warmth 💖"
                RELIEF -> "Deep breath of calm. Fluid balance and hydration are beautifully restored 🌿"
                CRAMPY -> "I feel that squeeze too! Cozy heating pad, dark chocolate, and rest for you ☕"
                BLOATED -> "Sip warm dandelion or peppermint tea! Gentle fluid movement will ease the pressure 🍵"
                FATIGUED -> "Permission granted to rest, $name. Your body is doing heavy cellular calibration 🌙"
                SLEEPING -> "Drifting into sweet slow-wave sleep. Recharging your hormones tonight 💤"
                CRAVINGS -> "Your luteal metabolism is burning higher! Satisfy it with magnesium-rich dark cacao 🍫"
                HUNGRY -> "Fuel that body! Healthy fats, complex carbs, and protein will stabilize your blood sugar 🥑"
                PILL_DAY -> "Pill routine tracked! Staying consistent with your reproductive wellness 💊"
                EMOTIONAL -> "It's okay to feel deeply today. All your feelings are sacred and valid 🥺"
                SAD -> "Sending you the warmest axolotl hugs. You are safe, cherished, and not alone 🌧️"
                ANGRY -> "Spicy mood detected! Channel that fire into setting strong, healthy boundaries 🔥"
                IRRITABLE -> "Sensing sensory overload! Put on soft headphones and carve out quiet solo space ⚡"
                SCARED -> "Two quick breaths in through the nose, long sigh out. You are grounded and safe 🫂"
                CONFUSED -> "Checking the biomarkers! Let's explore your cycle clues together step by step 🔍"
            }
        }
    }
}

/**
 * AnimatedAxolotlView:
 * Renders the transparent Axolotl mascot with organic floating, tilt hover, breathing scale,
 * aura glow, and dynamic tap reactions.
 */
@Composable
fun AnimatedAxolotlView(
    emotion: MascotEmotion,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showGlow: Boolean = true,
    showBadge: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    var bounceTrigger by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "axolotl_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )
    val tiltAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val bounceScale by animateFloatAsState(
        targetValue = if (bounceTrigger) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f),
        finishedListener = { bounceTrigger = false },
        label = "bounce"
    )

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                bounceTrigger = true
                onTap?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        // Glowing Background Aura
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size * 0.9f)
                    .scale(glowPulse)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                emotion.glowColor.copy(alpha = 0.45f),
                                emotion.glowColor.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Mascot PNG with Bobbing, Breathing, and Tilt
        Image(
            painter = painterResource(id = emotion.drawableRes),
            contentDescription = emotion.title,
            modifier = Modifier
                .size(size * 0.88f)
                .offset(y = floatOffset.dp)
                .rotate(tiltAngle)
                .scale(bounceScale),
            contentScale = ContentScale.Fit
        )

        // Emoji Mood Badge on bottom corner
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, emotion.glowColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emotion.emoji,
                    fontSize = 14.sp
                )
            }
        }
    }
}
