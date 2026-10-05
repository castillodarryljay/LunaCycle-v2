package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LunaPurple
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet
import com.example.ui.theme.PhaseMenstrual
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FastLogScreen(
    user: UserEntity,
    cycles: List<CycleEntity>,
    logs: List<LogEntity>,
    onSave: (LogEntity) -> Unit,
    onComplete: () -> Unit
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var symptoms by remember { mutableStateOf<List<String>>(emptyList()) }
    var mood by remember { mutableStateOf("Neutral") }
    var intensity by remember { mutableStateOf("None") }
    var isPeriod by remember { mutableStateOf(false) }
    var isPeriodEnd by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var waterMl by remember { mutableStateOf(0) }
    var bbt by remember { mutableStateOf<Float?>(null) }
    var cervicalMucus by remember { mutableStateOf<String?>(null) }
    var existingId by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    // Pre-populate if log exists for current selected date
    LaunchedEffect(date, logs) {
        val existing = logs.find { it.date == date.toString() }
        if (existing != null) {
            existingId = existing.id
            symptoms = existing.symptoms
            mood = existing.mood
            intensity = existing.intensity
            isPeriod = existing.isPeriod
            isPeriodEnd = existing.isPeriodEnd
            notes = existing.notes
            waterMl = existing.waterMl
            bbt = existing.bbt
            cervicalMucus = existing.cervicalMucus
        } else {
            existingId = null
            symptoms = emptyList()
            mood = "Neutral"
            intensity = "None"
            isPeriod = false
            isPeriodEnd = false
            notes = ""
            waterMl = 0
            bbt = null
            cervicalMucus = null
        }
    }

    val allSymptoms = listOf("Cramps", "Headache", "Bloating", "Acne", "Fatigue", "Backache", "Nausea")
    val allMoods = listOf("Happy", "Sad", "Angry", "Anxious", "Neutral", "Calm", "Bloated", "Energetic", "In Love", "Cravings", "Low", "Sleepy")

    val scrollState = rememberScrollState()

    val currentActiveEmotion = remember(symptoms, mood, isPeriod, waterMl, user.waterGoalMl) {
        com.example.ui.MascotEmotion.resolveEmotion(
            symptoms = symptoms,
            mood = mood,
            phase = "Menstrual",
            waterMl = waterMl,
            waterGoal = user.waterGoalMl,
            isPeriod = isPeriod
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fast Log",
                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onComplete)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CANCEL",
                    color = LunaRoseDeep,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Live Interactive Axolotl Companion Reaction Card
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                com.example.ui.AnimatedAxolotlView(
                    emotion = currentActiveEmotion,
                    size = 76.dp,
                    showGlow = true,
                    showBadge = true
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "LUNA REACTION",
                            color = currentActiveEmotion.glowColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(currentActiveEmotion.glowColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentActiveEmotion.badgeLabel,
                                color = currentActiveEmotion.glowColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = com.example.ui.MascotEmotion.getSpeechBubbleMessage(
                            currentActiveEmotion,
                            "today's",
                            user.name
                        ),
                        color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Select Date with Prev/Next step
                Column {
                    Text(
                        text = "SELECT DATE",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                            .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color.White)
                                .border(1.dp, if (user.darkMode) Color.Transparent else LunaRose.copy(alpha = 0.2f), CircleShape)
                                .clickable { date = date.minusDays(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Day",
                                tint = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color.White)
                                .border(1.dp, if (user.darkMode) Color.Transparent else LunaRose.copy(alpha = 0.2f), CircleShape)
                                .clickable { date = date.plusDays(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Day",
                                tint = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Period Start & End Toggles
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Start
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isPeriod) PhaseMenstrual.copy(alpha = 0.16f)
                                else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC)
                            )
                            .border(
                                1.5.dp,
                                if (isPeriod) PhaseMenstrual else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                isPeriod = !isPeriod
                                if (isPeriod) isPeriodEnd = false
                            }
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isPeriod) PhaseMenstrual else if (user.darkMode) Color(0x33FFFFFF) else Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = if (isPeriod) Color.White else PhaseMenstrual,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isPeriod) "Cycle Start" else "Cycle Tracking",
                                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (isPeriod) "Period beginning today" else "Toggle if cycle started",
                                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Switch style indicator
                        Box(
                            modifier = Modifier
                                .size(width = 46.dp, height = 28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isPeriod) PhaseMenstrual else Color.Gray.copy(alpha = 0.35f))
                                .padding(3.dp),
                            contentAlignment = if (isPeriod) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    // End
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isPeriodEnd) Color(0xFF3B82F6).copy(alpha = 0.16f)
                                else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC)
                            )
                            .border(
                                1.5.dp,
                                if (isPeriodEnd) Color(0xFF3B82F6) else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                isPeriodEnd = !isPeriodEnd
                                if (isPeriodEnd) isPeriod = false
                            }
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isPeriodEnd) Color(0xFF3B82F6) else if (user.darkMode) Color(0x33FFFFFF) else Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isPeriodEnd) Color.White else Color(0xFF3B82F6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Period End?",
                                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Mark flow finished",
                                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(width = 46.dp, height = 28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isPeriodEnd) Color(0xFF3B82F6) else Color.Gray.copy(alpha = 0.35f))
                                .padding(3.dp),
                            contentAlignment = if (isPeriodEnd) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                // Flow Intensity
                Column {
                    Text(
                        text = "INTENSITY",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("None", "Light", "Medium", "Heavy").forEach { lvl ->
                            val isSelected = (intensity == lvl)
                            val chipScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.05f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                                label = "chip_scale"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .scale(chipScale)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        if (isSelected) PhaseMenstrual
                                        else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) PhaseMenstrual else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { intensity = lvl }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Symptoms
                Column {
                    Text(
                        text = "SYMPTOMS",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allSymptoms.forEach { s ->
                            val isSelected = symptoms.contains(s)
                            val symptomScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                                label = "symptom_scale"
                            )
                            Box(
                                modifier = Modifier
                                    .scale(symptomScale)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) LunaPurple
                                        else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) LunaPurple else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        symptoms = if (isSelected) symptoms - s else symptoms + s
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = s,
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Mood
                Column {
                    Text(
                        text = "MOOD",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allMoods.chunked(3).forEach { rowMoods ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMoods.forEach { m ->
                                    val isSelected = (mood == m)
                                    val moodScale by animateFloatAsState(
                                        targetValue = if (isSelected) 1.06f else 1.0f,
                                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                                        label = "mood_scale"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .scale(moodScale)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                if (isSelected) {
                                                    if (user.darkMode) Color.White else Color(0xFF1E1B29)
                                                } else {
                                                    if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.Transparent else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .clickable { mood = m }
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val mEmotion = com.example.ui.MascotEmotion.fromMoodString(m)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = mEmotion.drawableRes),
                                                contentDescription = m,
                                                modifier = Modifier.size(26.dp),
                                                contentScale = ContentScale.Fit
                                            )
                                            Text(
                                                text = m,
                                                color = if (isSelected) {
                                                    if (user.darkMode) Color.Black else Color.White
                                                } else {
                                                    if (user.darkMode) Color.White else Color(0xFF1E1B29)
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- WATER & HYDRATION TRACKER ---
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WATER INTAKE",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "$waterMl ml / ${user.waterGoalMl} ml",
                            color = LunaViolet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .clickable { waterMl += 250 }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+250 ml (Glass)",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .clickable { waterMl += 500 }
                                .padding(vertical = 12.dp),
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
                                .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFEFE9F3))
                                .clickable { waterMl = maxOf(0, waterMl - 250) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-250",
                                color = if (user.darkMode) Color(0xFFA8A3B5) else Color(0xFF757082),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // --- BASAL BODY TEMPERATURE (BBT) ---
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BASAL BODY TEMP (BBT)",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = bbt?.let { String.format("%.2f°C", it) } ?: "Not logged",
                            color = LunaRoseDeep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(36.4f, 36.6f, 36.8f, 37.0f).forEach { preset ->
                            val isSelected = bbt != null && kotlin.math.abs(bbt!! - preset) < 0.04f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) LunaRoseDeep else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7)
                                    )
                                    .clickable { bbt = preset }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${preset}°C",
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Steppers
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .clickable { bbt = ((bbt ?: 36.6f) + 0.05f) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+0.05", color = LunaViolet, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .clickable { bbt = maxOf(35.0f, (bbt ?: 36.6f) - 0.05f) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-0.05", color = LunaViolet, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // --- CERVICAL MUCUS / FLUID ---
                Column {
                    Text(
                        text = "CERVICAL FLUID TYPE",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Dry", "Sticky", "Creamy", "Watery", "Egg-white (Peak)").forEach { fluid ->
                            val isSelected = cervicalMucus == fluid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(Brush.horizontalGradient(listOf(LunaRoseDeep, LunaViolet)))
                                        } else {
                                            Modifier.background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                        }
                                    )
                                    .clickable {
                                        cervicalMucus = if (isSelected) null else fluid
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fluid,
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Notes input
                Column {
                    Text(
                        text = "DAILY NOTES",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("How do you feel today?", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaRose,
                            unfocusedBorderColor = if (user.darkMode) Color(0x33FFFFFF) else Color(0x22000000)
                        )
                    )
                }

                // Save Log Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = LunaRoseDeep.copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(LunaRoseDeep, LunaViolet)
                            )
                        )
                        .clickable(enabled = !isSaving) {
                            isSaving = true
                            val log = LogEntity(
                                id = existingId ?: UUID.randomUUID().toString().take(9),
                                date = date.toString(),
                                symptoms = symptoms,
                                mood = mood,
                                intensity = intensity,
                                isPeriod = isPeriod,
                                isPeriodEnd = isPeriodEnd,
                                notes = notes,
                                waterMl = waterMl,
                                bbt = bbt,
                                cervicalMucus = cervicalMucus
                            )
                            onSave(log)
                            onComplete()
                        }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "SAVE LOG ENTRY",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}
