package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.CyclePredictionEngine
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassPill
import com.example.ui.theme.LunaPeach
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet
import com.example.ui.theme.PhaseFollicular
import com.example.ui.theme.PhaseLuteal
import com.example.ui.theme.PhaseMenstrual
import com.example.ui.theme.PhaseOvulation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DashboardScreen(
    user: UserEntity,
    cycles: List<CycleEntity>,
    logs: List<LogEntity>,
    onNavigate: (String) -> Unit,
    onAddWater: (Int) -> Unit = {}
) {
    val today = remember { LocalDate.now() }
    val prediction = remember(user, cycles, logs) {
        CyclePredictionEngine.calculateAdvancedPrediction(user, cycles, logs)
    }
    val futureCycles = remember(user, cycles, logs) {
        CyclePredictionEngine.getFuturePredictions(user, cycles, logs)
    }

    // Determine current day in cycle & days until next period
    val currentCycle = futureCycles.find { fc ->
        !today.isBefore(fc.start) && today.isBefore(fc.nextStart)
    }
    val dayInCycle = if (currentCycle != null) {
        ChronoUnit.DAYS.between(currentCycle.start, today).toInt() + 1
    } else {
        1
    }
    val daysUntilNextPeriod = if (currentCycle != null) {
        ChronoUnit.DAYS.between(today, currentCycle.nextStart).toInt().coerceAtLeast(0)
    } else {
        prediction.daysUntil
    }

    val todayStatus = remember(today, user, cycles, logs, futureCycles) {
        CyclePredictionEngine.getDayStatus(today, user, cycles, logs, futureCycles)
    }

    // 7-day strip centered around today
    val weekDays = remember(today) {
        (-3..3).map { today.plusDays(it.toLong()) }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val livePulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- HERO LIVE TRACKING CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(44.dp),
                    ambientColor = if (user.darkMode) Color.Black else LunaRose.copy(alpha = 0.35f),
                    spotColor = LunaRoseDeep.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(44.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF8FA8),
                            Color(0xFFFF7597),
                            Color(0xFFFEA5B8)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(44.dp)
                )
                .clickable { onNavigate("calendar") }
                .padding(26.dp)
        ) {
            // Watermark mascot background
            Image(
                painter = painterResource(id = R.drawable.mascot),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-24).dp)
                    .alpha(0.18f)
                    .scale(1.2f)
            )

            Image(
                painter = painterResource(id = R.drawable.mascot),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 40.dp)
                    .alpha(0.12f)
                    .rotate(15f)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")).uppercase(),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = livePulseAlpha))
                        )
                        Text(
                            text = "LIVE TRACKING",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day X Big Typography
                Text(
                    text = "Day $dayInCycle",
                    color = Color.White,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    lineHeight = 60.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = todayStatus.phaseName.uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "FERTILITY: ${prediction.fertilityStatus}".uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 7-Day Liquid Glass Calendar Strip ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    weekDays.forEach { d ->
                        val isToday = (d == today)
                        val dStatus = CyclePredictionEngine.getDayStatus(d, user, cycles, logs, futureCycles)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = d.format(DateTimeFormatter.ofPattern("EEE")).take(3).uppercase(),
                                color = if (isToday) Color.White else Color.White.copy(alpha = 0.65f),
                                fontSize = 8.sp,
                                fontWeight = if (isToday) FontWeight.Black else FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday -> Color.White
                                            dStatus.status?.contains("period") == true || dStatus.status?.contains("menstrual") == true -> PhaseMenstrual.copy(alpha = 0.45f)
                                            dStatus.status?.contains("ovulation") == true -> PhaseOvulation.copy(alpha = 0.45f)
                                            dStatus.status?.contains("follicular") == true -> PhaseFollicular.copy(alpha = 0.35f)
                                            dStatus.status?.contains("luteal") == true -> PhaseLuteal.copy(alpha = 0.35f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) Color.White else Color.White.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${d.dayOfMonth}",
                                    color = if (isToday) LunaRoseDeep else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )

                                if (dStatus.isSafe) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = if (isToday) LunaRoseDeep else Color.White,
                                        modifier = Modifier
                                            .size(6.dp)
                                            .align(Alignment.BottomCenter)
                                            .offset(y = (-2).dp)
                                    )
                                }

                                if (dStatus.hasSymptoms) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .align(Alignment.TopEnd)
                                            .offset(x = (-2).dp, y = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Next period forecast text
                val periodForecastText = when {
                    daysUntilNextPeriod > 1 -> "Next period in $daysUntilNextPeriod days"
                    daysUntilNextPeriod == 1 -> "Next period expected tomorrow"
                    daysUntilNextPeriod == 0 -> "Period expected today"
                    else -> "Period is ${-daysUntilNextPeriod} days late"
                }

                val estimatedDateText = if (daysUntilNextPeriod >= 0) {
                    "ESTIMATED: ${today.plusDays(daysUntilNextPeriod.toLong()).format(DateTimeFormatter.ofPattern("MMM d, yyyy")).uppercase()}"
                } else {
                    "WAS ESTIMATED: ${today.plusDays(daysUntilNextPeriod.toLong()).format(DateTimeFormatter.ofPattern("MMM d, yyyy")).uppercase()}"
                }

                Text(
                    text = periodForecastText,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = estimatedDateText,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Quick buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .clickable { onNavigate("logs") }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOG SYMPTOM",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .clickable { onNavigate("logs") }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START CYCLE",
                            color = LunaRoseDeep,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // --- PHASE INSIGHT CARD ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode,
            onClick = { onNavigate("insights") }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CYCLE INSIGHT",
                            color = LunaViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todayStatus.phaseName,
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LunaViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = LunaViolet,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val animatedProgress by animateFloatAsState(
                    targetValue = prediction.phaseProgress.coerceIn(0.08f, 1f),
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                    label = "phase_progress"
                )

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(if (user.darkMode) Color(0x28FFFFFF) else LunaRose.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = animatedProgress)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LunaRose, LunaViolet, LunaRose)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val phaseSummary = when {
                    todayStatus.phaseName.contains("Menstrual") -> "Healing phase. Your body is renewing itself with deep rest."
                    todayStatus.phaseName.contains("Follicular") -> "Creative phase. High energy and stamina for new beginnings."
                    todayStatus.phaseName.contains("Ovulation") -> "Peak energy! The ideal time for social connections and action."
                    else -> "Reflective phase. Good time for introspection, nourishment, and calm self-care."
                }

                Text(
                    text = phaseSummary,
                    color = if (user.darkMode) Color(0xFFBBB5CC) else Color(0xFF6B657B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
        }

        // --- INTERACTIVE LUNA AXOLOTL MASCOT REACTIONS ---
        val todayLog = logs.find { it.date == today.toString() }
        val currentWater = todayLog?.waterMl ?: 0
        val dynamicWaterGoal = if (prediction.phase == "Luteal" || prediction.phase == "Menstrual") 2400 else user.waterGoalMl

        var mascotBounceTrigger by remember { mutableStateOf(false) }
        var affirmationIndex by remember { mutableStateOf(0) }
        var petHeartVisible by remember { mutableStateOf(false) }
        var waterCelebrationMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(petHeartVisible) {
            if (petHeartVisible) {
                delay(900)
                petHeartVisible = false
            }
        }

        LaunchedEffect(waterCelebrationMessage) {
            if (waterCelebrationMessage != null) {
                delay(2200)
                waterCelebrationMessage = null
            }
        }

        val petHeartFloat by animateFloatAsState(
            targetValue = if (petHeartVisible) -28f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "pet_heart_float"
        )
        val petHeartAlpha by animateFloatAsState(
            targetValue = if (petHeartVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "pet_heart_alpha"
        )

        // Continuous Ambient Hover & Breathing animations
        val mascotHoverTransition = rememberInfiniteTransition(label = "mascot_hover_anim")
        val mascotFloatOffset by mascotHoverTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hover_float"
        )
        val mascotTiltAngle by mascotHoverTransition.animateFloat(
            initialValue = -2.5f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hover_tilt"
        )
        val mascotGlowPulse by mascotHoverTransition.animateFloat(
            initialValue = 0.90f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_pulse"
        )

        val mascotScale by animateFloatAsState(
            targetValue = if (mascotBounceTrigger) 1.25f else 1f,
            animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f),
            finishedListener = { mascotBounceTrigger = false },
            label = "mascot_bounce"
        )

        val affirmations = listOf(
            "✦ Your cycle is your superpower, not an inconvenience.",
            "✦ Deep rest during flow is essential, productive healing.",
            "✦ High follicular estrogen gives you razor-sharp focus!",
            "✦ Radiant ovulation energy: speak your truth and shine.",
            "✦ Luteal cravings are valid: your metabolism burns higher now!"
        )

        data class MascotMood(
            val badgeEmoji: String,
            val statusLabel: String,
            val glowColor: Color,
            val reactionMessage: String,
            val drawableRes: Int
        )

        val resolvedEmotion = remember(prediction.phase, todayLog, currentWater, dynamicWaterGoal) {
            val symptoms = todayLog?.symptoms ?: emptyList()
            val mood = todayLog?.mood ?: "Neutral"
            val isPeriod = todayLog?.isPeriod == true || (todayLog?.intensity != null && !todayLog.intensity.equals("None", ignoreCase = true))

            com.example.ui.MascotEmotion.resolveEmotion(
                symptoms = symptoms,
                mood = mood,
                phase = prediction.phase,
                waterMl = currentWater,
                waterGoal = dynamicWaterGoal,
                isPeriod = isPeriod
            )
        }

        val mascotMood = remember(resolvedEmotion, prediction.phase, user.name) {
            MascotMood(
                badgeEmoji = resolvedEmotion.emoji,
                statusLabel = resolvedEmotion.badgeLabel,
                glowColor = resolvedEmotion.glowColor,
                reactionMessage = com.example.ui.MascotEmotion.getSpeechBubbleMessage(resolvedEmotion, prediction.phase, user.name),
                drawableRes = resolvedEmotion.drawableRes
            )
        }

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(mascotMood.glowColor.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mascotMood.badgeEmoji,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            Text(
                                text = "LUNA AXOLOTL COMPANION",
                                color = mascotMood.glowColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = mascotMood.statusLabel,
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                            .clickable {
                                mascotBounceTrigger = true
                                petHeartVisible = true
                                affirmationIndex = (affirmationIndex + 1) % affirmations.size
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "PET LUNA ✨",
                            color = LunaViolet,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Reactive Axolotl Mascot with Bounce, Glow, Hover & Mood Badge
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .offset(y = mascotFloatOffset.dp)
                            .scale(mascotScale)
                            .rotate(mascotTiltAngle)
                            .clickable {
                                mascotBounceTrigger = true
                                petHeartVisible = true
                                affirmationIndex = (affirmationIndex + 1) % affirmations.size
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Floating hearts / sparkle reaction on tap
                        if (petHeartAlpha > 0.05f) {
                            Text(
                                text = "💖✨",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = petHeartFloat.dp)
                                    .alpha(petHeartAlpha)
                            )
                        }
                        // Pulsing ambient glow aura
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(mascotGlowPulse)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            mascotMood.glowColor.copy(alpha = 0.45f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Mascot image
                        Image(
                            painter = painterResource(id = mascotMood.drawableRes),
                            contentDescription = "Luna Mascot",
                            modifier = Modifier.size(68.dp),
                            contentScale = ContentScale.Fit
                        )

                        // Floating dynamic mood icon badge on bottom-right of mascot
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(if (user.darkMode) Color(0xFF1E1B29) else Color.White)
                                .border(1.5.dp, mascotMood.glowColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mascotMood.badgeEmoji,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Dynamic Speech Bubble
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomEnd = 18.dp,
                                    bottomStart = 4.dp
                                )
                            )
                            .background(
                                if (user.darkMode) Color(0xFF221C38) else Color(0xFFF9F5FB)
                            )
                            .border(
                                1.dp,
                                if (user.darkMode) Color(0x30FFFFFF) else LunaRose.copy(alpha = 0.25f),
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomEnd = 18.dp,
                                    bottomStart = 4.dp
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = mascotMood.reactionMessage,
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = affirmations[affirmationIndex],
                                color = LunaViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // --- CYCLE SYNCING DAILY PHASE GUIDANCE CARD ---
        val syncPhaseGuidance = remember(prediction.phase) {
            when (prediction.phase) {
                "Menstrual" -> Triple(
                    "Iron-rich broths, beets, lentils, dark berries & raw pumpkin/flax seeds",
                    "Gentle yin yoga, pelvic stretching & slow restorative walks",
                    "Inward intuition, boundary setting, and peaceful reflection"
                )
                "Follicular" -> Triple(
                    "Fermented kimchi, lean protein, citrus & pumpkin/flax seeds",
                    "HIIT intervals, progressive weight training, running & dance",
                    "Creative brainstorming, fast learning & launching ambitious projects"
                )
                "Ovulatory" -> Triple(
                    "Anti-inflammatory wild salmon, asparagus, magnesium & seeds",
                    "Peak PRs, group power fitness, spin & high-intensity sweat",
                    "Magnetic social eloquence, negotiations, public speaking & dates"
                )
                else -> Triple(
                    "Complex carbs (sweet potatoes, oats), magnesium & sunflower/sesame seeds",
                    "Pilates, barre, bodyweight sculpting & calming nature walks",
                    "Organization, detail editing, nesting & finishing lingering tasks"
                )
            }
        }

        val syncPhaseColor = when (prediction.phase) {
            "Menstrual" -> PhaseMenstrual
            "Follicular" -> PhaseFollicular
            "Ovulatory" -> PhaseOvulation
            else -> PhaseLuteal
        }

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(syncPhaseColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = syncPhaseColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = "CYCLE SYNCING GUIDE",
                                color = syncPhaseColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "${prediction.phase.uppercase()} BLUEPRINT",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Phase indicator pill with distinct dot
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(syncPhaseColor.copy(alpha = 0.16f))
                            .border(1.dp, syncPhaseColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(syncPhaseColor)
                            )
                            Text(
                                text = prediction.phase.uppercase(),
                                color = syncPhaseColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Biomarker Indicators Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (user.darkMode) Color(0x20FFFFFF) else Color(0xFFF7F3FB))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Energy Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Bolt, null, tint = Color(0xFFD97706), modifier = Modifier.size(13.dp))
                        Text(
                            text = when (prediction.phase) {
                                "Menstrual" -> "Energy: 30%"
                                "Follicular" -> "Energy: 85%"
                                "Ovulatory" -> "Energy: 100%"
                                else -> "Energy: 60%"
                            },
                            color = Color(0xFFD97706),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Workout Intensity Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, null, tint = Color(0xFF0284C7), modifier = Modifier.size(13.dp))
                        Text(
                            text = when (prediction.phase) {
                                "Menstrual" -> "Workout: 1/4"
                                "Follicular" -> "Workout: 3/4"
                                "Ovulatory" -> "Workout: 4/4"
                                else -> "Workout: 2/4"
                            },
                            color = Color(0xFF0284C7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Seed cycling indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Spa, null, tint = LunaViolet, modifier = Modifier.size(13.dp))
                        Text(
                            text = if (prediction.phase in listOf("Menstrual", "Follicular")) "Seeds: Phase 1" else "Seeds: Phase 2",
                            color = LunaViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nutrition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = LunaRoseDeep,
                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                    )
                    Column {
                        Text(
                            text = "FOOD & NUTRITION",
                            color = LunaRoseDeep,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = syncPhaseGuidance.first,
                            color = if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF4A4458),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Movement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                    )
                    Column {
                        Text(
                            text = "MOVEMENT & WORKOUT",
                            color = Color(0xFF0284C7),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = syncPhaseGuidance.second,
                            color = if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF4A4458),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Energy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                    )
                    Column {
                        Text(
                            text = "ENERGY & SOCIAL LIFE",
                            color = Color(0xFFD97706),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = syncPhaseGuidance.third,
                            color = if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF4A4458),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Properly placed, prominent, beautifully styled DETAILS action button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(LunaRoseDeep, LunaViolet)
                            )
                        )
                        .clickable { onNavigate("insights:syncing") }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "VIEW FULL CYCLE SYNCING DETAILS ➔",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }

        // --- WATER & HYDRATION TRACKER CARD ---
        val waterPercentage = (currentWater.toFloat() / dynamicWaterGoal.toFloat()).coerceIn(0f, 1f)
        val animatedWaterFraction by animateFloatAsState(
            targetValue = waterPercentage,
            animationSpec = tween(700, easing = FastOutSlowInEasing),
            label = "water_fill"
        )

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LunaViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water",
                                tint = LunaViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "DAILY HYDRATION",
                                color = LunaViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "$currentWater ml / $dynamicWaterGoal ml",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = "${(waterPercentage * 100).toInt()}% REACHED",
                        color = if (waterPercentage >= 1f) Color(0xFF10B981) else LunaViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Liquid Fill Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFEFEAF5))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = animatedWaterFraction)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LunaViolet, Color(0xFF60A5FA), LunaRoseDeep)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phase-aware hydration tip
                Text(
                    text = if (prediction.phase == "Luteal" || prediction.phase == "Menstrual") {
                        "Target: 2400 ml • Increased intake reduces progesterone bloating and cramp intensity."
                    } else {
                        "Target: 2000 ml • Sustains energetic metabolism and luminous skin."
                    },
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Water celebration feedback badge
                androidx.compose.animation.AnimatedVisibility(
                    visible = waterCelebrationMessage != null,
                    enter = androidx.compose.animation.fadeIn(tween(250)) + androidx.compose.animation.scaleIn(initialScale = 0.9f),
                    exit = androidx.compose.animation.fadeOut(tween(200)) + androidx.compose.animation.scaleOut(targetScale = 0.9f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF38BDF8).copy(alpha = 0.18f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(vertical = 7.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = waterCelebrationMessage ?: "",
                            color = if (user.darkMode) Color(0xFF7DD3FC) else Color(0xFF0284C7),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick Add Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7)
                            )
                            .clickable {
                                onAddWater(250)
                                waterCelebrationMessage = "💧 +250 ml logged! Pure cell hydration"
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+250 ml (Cup)",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7)
                            )
                            .clickable {
                                onAddWater(500)
                                waterCelebrationMessage = "✨ +500 ml logged! Hormonal balance boosted"
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+500 ml (Bottle)",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFEAE5EF))
                            .clickable {
                                onAddWater(-250)
                                waterCelebrationMessage = "Hydration adjusted"
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "-250",
                            color = if (user.darkMode) Color(0xFFA8A3B5) else Color(0xFF6B657B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- LUNA'S DAILY TIP CARD ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode,
            onClick = { onNavigate("insights") }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LUNA'S DAILY TIP",
                            color = PhaseLuteal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Holistic Tip",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PhaseLuteal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PhaseLuteal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = CyclePredictionEngine.getDailyTip(todayStatus.phaseName),
                    color = if (user.darkMode) Color(0xFFEDE8F7) else Color(0xFF332D42),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }
        }

        // --- UPCOMING FORECAST CARD ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LunaRose.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = LunaRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Upcoming Forecast",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Predicted for the next few days",
                            color = if (user.darkMode) Color(0xFF8E88A1) else Color(0xFF888299),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                (1..3).forEach { offset ->
                    val forecastDay = today.plusDays(offset.toLong())
                    val fStatus = CyclePredictionEngine.getDayStatus(forecastDay, user, cycles, logs, futureCycles)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = forecastDay.format(DateTimeFormatter.ofPattern("EEEE")),
                            color = if (user.darkMode) Color.White else Color(0xFF2B2538),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = forecastDay.format(DateTimeFormatter.ofPattern("MMM d")).uppercase(),
                            color = LunaViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Footer Brand Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0x33FF85A1))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "HOLISTIC WELLNESS ASSISTANT",
                    color = if (user.darkMode) Color(0xFFA29BFE) else LunaRoseDeep,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
