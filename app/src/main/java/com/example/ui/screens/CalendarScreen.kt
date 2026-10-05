package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.CyclePredictionEngine
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LunaPeach
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet
import com.example.ui.theme.PhaseFollicular
import com.example.ui.theme.PhaseLuteal
import com.example.ui.theme.PhaseMenstrual
import com.example.ui.theme.PhaseOvulation
import com.example.ui.theme.SafeDayPink
import com.example.ui.theme.SymptomPurple
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun CalendarScreen(
    user: UserEntity,
    cycles: List<CycleEntity>,
    logs: List<LogEntity>,
    onSaveLog: (LogEntity) -> Unit,
    onDeleteLog: (String) -> Unit
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val futureCycles = remember(user, cycles, logs) {
        CyclePredictionEngine.getFuturePredictions(user, cycles, logs)
    }

    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- MONTH NAVIGATION & CALENDAR CARD ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Month Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM")),
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${currentYearMonth.year}",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                                .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                                .clickable { currentYearMonth = currentYearMonth.minusMonths(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month",
                                tint = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                                .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                                .clickable { currentYearMonth = currentYearMonth.plusMonths(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month",
                                tint = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Day of Week Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            color = if (user.darkMode) Color(0xFF756F86) else Color(0xFFA09BAC),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Calendar Grid
                val totalSlots = dayOfWeekOffset + daysInMonth
                val totalRows = (totalSlots + 6) / 7

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until totalRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0..6) {
                                val slotIndex = row * 7 + col
                                val dayNum = slotIndex - dayOfWeekOffset + 1

                                if (dayNum in 1..daysInMonth) {
                                    val date = currentYearMonth.atDay(dayNum)
                                    val dayStatus = CyclePredictionEngine.getDayStatus(
                                        date, user, cycles, logs, futureCycles
                                    )
                                    val isToday = (date == today)
                                    val isSelected = (selectedDate == date)

                                    CalendarDayCell(
                                        date = date,
                                        dayStatus = dayStatus,
                                        isToday = isToday,
                                        isSelected = isSelected,
                                        darkMode = user.darkMode,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedDate = date
                                            showDetailDialog = true
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SELECTED DATE QUICK PREVIEW ---
        if (selectedDate != null) {
            val previewStatus = remember(selectedDate, user, cycles, logs, futureCycles) {
                selectedDate?.let { CyclePredictionEngine.getDayStatus(it, user, cycles, logs, futureCycles) }
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedDate!!.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = previewStatus?.phaseName ?: "Cycle Phase",
                                color = LunaRose,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(LunaRoseDeep.copy(alpha = 0.25f), LunaViolet.copy(alpha = 0.25f))
                                    )
                                )
                                .border(1.dp, LunaRose.copy(alpha = 0.35f), RoundedCornerShape(13.dp))
                                .clickable { showDetailDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Day Details",
                                tint = if (user.darkMode) Color.White else LunaRoseDeep,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (previewStatus?.log != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LunaViolet.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Mood: ${previewStatus.log.mood}",
                                    color = LunaViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (previewStatus.log.intensity != "None") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PhaseMenstrual.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Flow: ${previewStatus.log.intensity}",
                                        color = PhaseMenstrual,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (previewStatus.log.symptoms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                previewStatus.log.symptoms.forEach { s ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0x10000000))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = s,
                                            color = if (user.darkMode) Color(0xFFDDD7EC) else Color(0xFF554F65),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominently placed, beautiful full-width details & log action button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LunaRoseDeep, LunaViolet)
                                )
                            )
                            .clickable { showDetailDialog = true }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (previewStatus?.log != null) "VIEW & EDIT DAY DETAILS ➔" else "LOG DETAILS FOR THIS DAY ➔",
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }
        }

        // --- PHASE LEGEND ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "PHASE LEGEND",
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendItem(color = PhaseMenstrual, label = "Menstruation", darkMode = user.darkMode)
                        LegendItem(color = PhaseFollicular, label = "Proliferative", darkMode = user.darkMode)
                        LegendItem(color = PhaseOvulation, label = "Ovulation", darkMode = user.darkMode)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendItem(color = PhaseLuteal, label = "Secretory", darkMode = user.darkMode)
                        LegendItem(color = SafeDayPink, label = "Safe Day", isHeart = true, darkMode = user.darkMode)
                        LegendItem(color = SymptomPurple, label = "Symptom Dot", darkMode = user.darkMode)
                    }
                }
            }
        }
    }

    // --- DATE DETAIL DIALOG ---
    if (showDetailDialog && selectedDate != null) {
        val existingLog = logs.find { it.date == selectedDate.toString() }
        DateDetailDialog(
            date = selectedDate!!,
            existingLog = existingLog,
            user = user,
            onDismiss = { showDetailDialog = false },
            onSave = { updatedLog ->
                onSaveLog(updatedLog)
                showDetailDialog = false
            },
            onDelete = { id ->
                onDeleteLog(id)
                showDetailDialog = false
            }
        )
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    dayStatus: com.example.engine.DayStatusInfo,
    isToday: Boolean,
    isSelected: Boolean,
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val status = dayStatus.status

    val isPeriod = status?.contains("period") == true || status?.contains("menstrual") == true || status == "predicted-start" || status == "predicted-body"
    val isOvulation = status?.contains("ovulation") == true
    val isFollicular = status?.contains("follicular") == true
    val isLuteal = status?.contains("luteal") == true

    val cellBackground = when {
        status == "period-start" -> PhaseMenstrual
        status == "period-end" -> Color(0xFF880E4F)
        status == "period-body" -> PhaseMenstrual.copy(alpha = 0.8f)
        status == "predicted-start" -> PhaseMenstrual.copy(alpha = 0.25f)
        status == "predicted-body" -> PhaseMenstrual.copy(alpha = 0.15f)
        isOvulation -> PhaseOvulation.copy(alpha = 0.25f)
        isFollicular -> PhaseFollicular.copy(alpha = 0.18f)
        isLuteal -> PhaseLuteal.copy(alpha = 0.18f)
        else -> Color.Transparent
    }

    val cellBorder = when {
        isSelected -> LunaRose
        isToday -> LunaRoseDeep
        status == "predicted-start" -> PhaseMenstrual
        isOvulation -> PhaseOvulation
        isFollicular -> PhaseFollicular.copy(alpha = 0.5f)
        isLuteal -> PhaseLuteal.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val textColor = when {
        status == "period-start" || status == "period-end" || status == "period-body" -> Color.White
        isOvulation -> if (darkMode) Color(0xFF4ADE80) else Color(0xFF16A34A)
        isPeriod -> if (darkMode) Color(0xFFFF8FA8) else PhaseMenstrual
        isToday -> LunaRoseDeep
        darkMode -> Color.White
        else -> Color(0xFF221D2C)
    }

    val cellScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cell_scale"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(cellScale)
            .padding(2.dp)
            .clip(CircleShape)
            .background(cellBackground)
            .border(
                width = if (isSelected || isToday || isOvulation) 2.dp else 1.dp,
                color = cellBorder,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${date.dayOfMonth}",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isToday || isSelected || isPeriod || isOvulation) FontWeight.Black else FontWeight.Medium
        )

        // Safe day indicator
        if (dayStatus.isSafe) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = SafeDayPink,
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-2).dp)
            )
        }

        // Symptom indicator
        if (dayStatus.hasSymptoms) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(SymptomPurple)
                    .align(Alignment.TopEnd)
                    .offset(x = (-3).dp, y = 3.dp)
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, isHeart: Boolean = false, darkMode: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isHeart) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(10.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Text(
            text = label.uppercase(),
            color = if (darkMode) Color(0xFFCCC5E0) else Color(0xFF6B657B),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateDetailDialog(
    date: LocalDate,
    existingLog: LogEntity?,
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (LogEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    var isPeriod by remember { mutableStateOf(existingLog?.isPeriod ?: false) }
    var isPeriodEnd by remember { mutableStateOf(existingLog?.isPeriodEnd ?: false) }
    var intensity by remember { mutableStateOf(existingLog?.intensity ?: "None") }
    var notes by remember { mutableStateOf(existingLog?.notes ?: "") }
    var isSafeDay by remember { mutableStateOf(existingLog?.mood == "Safe Day") }
    var selectedSymptoms by remember { mutableStateOf(existingLog?.symptoms ?: emptyList()) }
    var deleteConfirm by remember { mutableStateOf(false) }

    val allSymptoms = listOf("Cramps", "Headache", "Bloating", "Acne", "Fatigue", "Backache")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .imePadding()
                .shadow(24.dp, RoundedCornerShape(36.dp))
                .clip(RoundedCornerShape(36.dp))
                .background(if (user.darkMode) Color(0xFF18142D) else Color.White)
                .border(
                    1.dp,
                    if (user.darkMode) Color(0x33FFFFFF) else Color(0x22FF85A1),
                    RoundedCornerShape(36.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val dayEmotion = remember(selectedSymptoms, isPeriod) {
                    com.example.ui.MascotEmotion.resolveEmotion(
                        symptoms = selectedSymptoms,
                        mood = null,
                        phase = if (isPeriod) "Menstrual" else "Follicular",
                        waterMl = 0,
                        waterGoal = 2000,
                        isPeriod = isPeriod
                    )
                }

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = dayEmotion.drawableRes),
                            contentDescription = "Luna Axolotl",
                            modifier = Modifier.size(42.dp)
                        )
                        Column {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = dayEmotion.title.uppercase(),
                                color = dayEmotion.glowColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (user.darkMode) Color.White else Color(0xFF1E1B29)
                        )
                    }
                }

                // Period Start & End Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isPeriod) PhaseMenstrual.copy(alpha = 0.16f) else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC))
                            .border(
                                width = 1.5.dp,
                                color = if (isPeriod) PhaseMenstrual else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                isPeriod = !isPeriod
                                if (isPeriod) isPeriodEnd = false
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = if (isPeriod) PhaseMenstrual else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "PERIOD START",
                                color = if (isPeriod) PhaseMenstrual else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isPeriodEnd) Color(0xFF3B82F6).copy(alpha = 0.16f) else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC))
                            .border(
                                width = 1.5.dp,
                                color = if (isPeriodEnd) Color(0xFF3B82F6) else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                isPeriodEnd = !isPeriodEnd
                                if (isPeriodEnd) isPeriod = false
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PERIOD END",
                            color = if (isPeriodEnd) Color(0xFF3B82F6) else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Flow Intensity
                Column {
                    Text(
                        text = "FLOW INTENSITY",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("None", "Light", "Medium", "Heavy").forEach { lvl ->
                            val selected = (intensity == lvl)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) PhaseMenstrual else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC))
                                    .border(
                                        1.dp,
                                        if (selected) PhaseMenstrual else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { intensity = lvl }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (selected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
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
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allSymptoms.forEach { s ->
                            val isSelected = selectedSymptoms.contains(s)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) SymptomPurple else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC))
                                    .border(
                                        1.dp,
                                        if (isSelected) SymptomPurple else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        selectedSymptoms = if (isSelected) {
                                            selectedSymptoms - s
                                        } else {
                                            selectedSymptoms + s
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = s,
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Biomarkers (BBT & Cervical Mucus) info row if present
                if (existingLog?.bbt != null || !existingLog?.cervicalMucus.isNullOrEmpty() || (existingLog?.waterMl ?: 0) > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        existingLog?.bbt?.let { bbtVal ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LunaRoseDeep.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🌡️ ${String.format(java.util.Locale.US, "%.2f", bbtVal)}°C",
                                    color = LunaRoseDeep,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        existingLog?.cervicalMucus?.let { mucus ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LunaViolet.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "💧 $mucus",
                                    color = LunaViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if ((existingLog?.waterMl ?: 0) > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🥤 ${existingLog?.waterMl} ml",
                                    color = Color(0xFF0284C7),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Safe day toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSafeDay) SafeDayPink.copy(alpha = 0.16f) else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFFBF8FC))
                        .border(
                            1.dp,
                            if (isSafeDay) SafeDayPink else if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { isSafeDay = !isSafeDay }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (isSafeDay) SafeDayPink else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isSafeDay) "MARKED AS SAFE DAY" else "MARK AS SAFE DAY",
                            color = if (isSafeDay) SafeDayPink else if (user.darkMode) Color.White else Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Daily Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Daily notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LunaRose,
                        unfocusedBorderColor = if (user.darkMode) Color(0x33FFFFFF) else Color(0x22000000)
                    )
                )

                // Save button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = LunaRoseDeep.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(LunaRoseDeep, LunaViolet)
                            )
                        )
                        .clickable {
                            val log = LogEntity(
                                id = existingLog?.id ?: UUID.randomUUID().toString().take(9),
                                date = date.toString(),
                                symptoms = selectedSymptoms,
                                mood = if (isSafeDay) "Safe Day" else (existingLog?.mood ?: "Neutral"),
                                intensity = intensity,
                                isPeriod = isPeriod,
                                isPeriodEnd = isPeriodEnd,
                                notes = notes,
                                waterMl = existingLog?.waterMl ?: 0,
                                bbt = existingLog?.bbt,
                                cervicalMucus = existingLog?.cervicalMucus
                            )
                            onSave(log)
                        }
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SAVE DETAILS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Delete entry button if existing
                if (existingLog != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (deleteConfirm) Color(0xFFEF4444) else Color(0x15EF4444))
                            .clickable {
                                if (deleteConfirm) {
                                    onDelete(existingLog.id)
                                } else {
                                    deleteConfirm = true
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (deleteConfirm) "CONFIRM DELETE?" else "DELETE LOG ENTRY",
                            color = if (deleteConfirm) Color.White else Color(0xFFEF4444),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
