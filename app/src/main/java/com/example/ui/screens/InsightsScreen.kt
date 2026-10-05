package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.util.MedicalReportExporter
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun InsightsScreen(
    user: UserEntity,
    cycles: List<CycleEntity>,
    logs: List<LogEntity>,
    onDeleteLog: (String) -> Unit,
    onResetData: () -> Unit,
    initialTab: String = "trends"
) {
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }
    var resetConfirm by remember { mutableStateOf(false) }

    val completedCycles = remember(cycles) {
        cycles.filter { it.length > 0 }
    }

    val avgCycleLength = remember(completedCycles, user) {
        if (completedCycles.isNotEmpty()) {
            (completedCycles.map { it.length }.average()).roundToInt()
        } else {
            user.cycleLength
        }
    }

    val cycleVariability = remember(completedCycles) {
        if (completedCycles.size > 1) {
            val lengths = completedCycles.map { it.length }
            lengths.maxOrNull()!! - lengths.minOrNull()!!
        } else {
            0
        }
    }

    // Symptom counts
    val symptomCounts = remember(logs) {
        val map = mutableMapOf<String, Int>()
        logs.forEach { log ->
            log.symptoms.forEach { s ->
                map[s] = (map[s] ?: 0) + 1
            }
        }
        map.toList().sortedByDescending { it.second }.take(5)
    }

    // Symptom timeline observations
    val observations = remember(completedCycles, cycleVariability, logs) {
        val list = mutableListOf<Triple<String, String, Color>>()
        if (completedCycles.size >= 3) {
            if (cycleVariability <= 3) {
                list.add(
                    Triple(
                        "Consistency King",
                        "Your cycle is very regular with low variability.",
                        Color(0xFF22C55E)
                    )
                )
            } else {
                list.add(
                    Triple(
                        "Irregular Patterns",
                        "Your cycle length varies. This is common and tracked for better accuracy.",
                        Color(0xFFF97316)
                    )
                )
            }
        }

        if (logs.any { it.symptoms.contains("Bloating") }) {
            list.add(
                Triple(
                    "PMS Alert: Bloating",
                    "You often track bloating in your Luteal phase. Magnesium and potassium may help.",
                    LunaViolet
                )
            )
        } else if (logs.any { it.symptoms.contains("Cramps") }) {
            list.add(
                Triple(
                    "Cramp Tracking",
                    "Cramping detected near period start. Heat therapy and hydration provide relief.",
                    PhaseMenstrual
                )
            )
        }

        if (list.isEmpty()) {
            list.add(
                Triple(
                    "Awaiting Data",
                    "Log more cycles and daily symptoms to unlock personalized hormone observations.",
                    Color.Gray
                )
            )
        }
        list.take(3)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val context = LocalContext.current

        // Header & Tab switch
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Insights",
                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )

                var showExportDialog by remember { mutableStateOf(false) }

                // Quick Export & Download button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(LunaRoseDeep, LunaViolet)))
                        .clickable { showExportDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "DOCUMENTS",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                if (showExportDialog) {
                    Dialog(
                        onDismissRequest = { showExportDialog = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.72f))
                                .clickable { showExportDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .widthIn(max = 420.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(
                                        if (user.darkMode) Color(0xF6181428) else Color(0xFDFFFFFF)
                                    )
                                    .border(
                                        1.5.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                LunaRose.copy(alpha = 0.4f),
                                                LunaViolet.copy(alpha = 0.25f)
                                            )
                                        ),
                                        RoundedCornerShape(28.dp)
                                    )
                                    .clickable(enabled = false) {}
                                    .padding(22.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Medical Documents",
                                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "Download or share your clinical reports",
                                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                                fontSize = 11.sp
                                            )
                                        }

                                        IconButton(onClick = { showExportDialog = false }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = if (user.darkMode) Color.White else Color.Black
                                            )
                                        }
                                    }

                                    // Download PDF
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.horizontalGradient(listOf(LunaRoseDeep, LunaViolet))
                                            )
                                            .clickable {
                                                MedicalReportExporter.downloadPdf(context, user, cycles, logs)
                                                showExportDialog = false
                                            }
                                            .padding(vertical = 13.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Download, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Text("DOWNLOAD DOCTOR PDF", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }

                                    // Share PDF
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                            .border(1.dp, LunaRose.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                            .clickable {
                                                MedicalReportExporter.generateAndSharePdf(context, user, cycles, logs)
                                                showExportDialog = false
                                            }
                                            .padding(vertical = 13.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Share, null, tint = LunaRoseDeep, modifier = Modifier.size(16.dp))
                                            Text("SHARE PDF WITH DOCTOR", color = LunaRoseDeep, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }

                                    // Download CSV
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                            .border(1.dp, LunaViolet.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                            .clickable {
                                                MedicalReportExporter.downloadCsv(context, user, cycles, logs)
                                                showExportDialog = false
                                            }
                                            .padding(vertical = 13.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Download, null, tint = LunaViolet, modifier = Modifier.size(16.dp))
                                            Text("DOWNLOAD RAW CSV SPREADSHEET", color = LunaViolet, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }

                                    // Share CSV
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFEFEAF5))
                                            .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else Color(0x22000000), RoundedCornerShape(16.dp))
                                            .clickable {
                                                MedicalReportExporter.generateAndShareCsv(context, user, cycles, logs)
                                                showExportDialog = false
                                            }
                                            .padding(vertical = 13.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Share, null, tint = if (user.darkMode) Color.White else Color(0xFF1E1B29), modifier = Modifier.size(16.dp))
                                            Text("SHARE CSV SPREADSHEET", color = if (user.darkMode) Color.White else Color(0xFF1E1B29), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4-Tab Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                    .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "trends" to "TRENDS",
                    "syncing" to "SYNCING",
                    "bbt" to "BBT",
                    "history" to "HISTORY"
                ).forEach { (tabId, label) ->
                    val isSelected = activeTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (user.darkMode) Color(0x50FFFFFF) else Color.White
                                } else Color.Transparent
                            )
                            .clickable { activeTab = tabId }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) LunaRoseDeep else if (user.darkMode) Color(0xFF9E98B0) else Color(0xFF7A738A),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }

        when (activeTab) {
            "trends" -> {
                // Metrics row: Avg Cycle & Variability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LiquidGlassCard(
                    modifier = Modifier.weight(1f),
                    darkMode = user.darkMode
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "AVG CYCLE",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$avgCycleLength",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DAYS",
                                color = LunaRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }

                LiquidGlassCard(
                    modifier = Modifier.weight(1f),
                    darkMode = user.darkMode
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "VARIABILITY",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "±$cycleVariability",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DAYS",
                                color = LunaViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
            }

            // Observations Card
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkMode = user.darkMode
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = LunaViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Luna's Observations",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        observations.forEach { (title, desc, color) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .offset(y = 6.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Column {
                                    Text(
                                        text = title.uppercase(),
                                        color = color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        color = if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF5A5368),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Cycle Length Bar Chart (last 6 cycles)
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkMode = user.darkMode
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LunaRose.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = LunaRose,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Cycle Length",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "LAST COMPLETED CYCLES",
                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val chartCycles = completedCycles.takeLast(6)
                    if (chartCycles.isNotEmpty()) {
                        CycleLengthBarChart(
                            cycles = chartCycles,
                            darkMode = user.darkMode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Awaiting completed cycles data...",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Symptoms Breakdown
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkMode = user.darkMode
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "MOST FREQUENT SYMPTOMS",
                        color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (symptomCounts.isNotEmpty()) {
                        val maxCount = symptomCounts.first().second.toFloat().coerceAtLeast(1f)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            symptomCounts.forEach { (name, count) ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = name,
                                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$count times",
                                            color = LunaViolet,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape)
                                            .background(if (user.darkMode) Color(0x33FFFFFF) else Color(0x10000000))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(fraction = (count / maxCount).coerceIn(0.1f, 1f))
                                                .height(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(LunaRose, LunaViolet)
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No symptoms recorded yet",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        "syncing" -> {
            val prediction = remember(user, cycles, logs) {
                CyclePredictionEngine.calculateAdvancedPrediction(user, cycles, logs)
            }
            val futureCycles = remember(user, cycles, logs) {
                CyclePredictionEngine.getFuturePredictions(user, cycles, logs)
            }
            val today = remember { LocalDate.now() }
            val currentCycle = futureCycles.find { fc ->
                !today.isBefore(fc.start) && today.isBefore(fc.nextStart)
            }
            val dayInCycle = if (currentCycle != null) {
                ChronoUnit.DAYS.between(currentCycle.start, today).toInt() + 1
            } else {
                1
            }
            CycleSyncingView(
                user = user,
                activePhase = prediction.phase,
                currentCycleDay = dayInCycle,
                cycleLength = if (user.cycleLength > 0) user.cycleLength else 28
            )
        }
        "bbt" -> {
            BbtChartView(user = user, logs = logs)
        }
        "history" -> {
            // --- HISTORY TAB ---
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
                        Column {
                            Text(
                                text = "Log History",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Manage your entries",
                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (resetConfirm) Color(0xFFEF4444) else if (user.darkMode) Color(0x22FFFFFF) else Color(0x15FF3B70))
                                .clickable {
                                    if (resetConfirm) {
                                        onResetData()
                                        resetConfirm = false
                                    } else {
                                        resetConfirm = true
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (resetConfirm) "CONFIRM RESET?" else "CLEAR ALL",
                                color = if (resetConfirm) Color.White else LunaRoseDeep,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (logs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No entries found",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            logs.sortedByDescending { it.date }.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (user.darkMode) Color(0x24FFFFFF) else Color(0xFFFBF8FC))
                                        .border(
                                            1.dp,
                                            if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    when {
                                                        log.isPeriod -> PhaseMenstrual
                                                        log.isPeriodEnd -> Color(0xFF880E4F)
                                                        log.intensity != "None" -> PhaseMenstrual.copy(alpha = 0.5f)
                                                        else -> if (user.darkMode) Color(0x33FFFFFF) else Color.White
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WaterDrop,
                                                contentDescription = null,
                                                tint = if (log.isPeriod || log.isPeriodEnd || log.intensity != "None") Color.White else Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = try {
                                                    LocalDate.parse(log.date).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                                                } catch (_: Exception) {
                                                    log.date
                                                },
                                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "${log.mood} • ${log.symptoms.size} Symptoms ${if (log.intensity != "None") "• Flow" else ""}",
                                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x15EF4444))
                                            .clickable { onDeleteLog(log.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun CycleSyncingView(
    user: UserEntity,
    activePhase: String = "Menstrual",
    currentCycleDay: Int = 1,
    cycleLength: Int = 28
) {
    var selectedPhase by remember(activePhase) {
        mutableStateOf(
            if (activePhase in listOf("Menstrual", "Follicular", "Ovulatory", "Luteal")) activePhase else "Menstrual"
        )
    }

    val phases = listOf("Menstrual", "Follicular", "Ovulatory", "Luteal")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Interactive Phase Selector with Active Phase Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                .border(
                    1.dp,
                    if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f),
                    RoundedCornerShape(18.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            phases.forEach { phase ->
                val isSelected = selectedPhase == phase
                val isCurrent = activePhase == phase
                val phaseColor = when (phase) {
                    "Menstrual" -> PhaseMenstrual
                    "Follicular" -> PhaseFollicular
                    "Ovulatory" -> PhaseOvulation
                    else -> PhaseLuteal
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    listOf(phaseColor, phaseColor.copy(alpha = 0.85f))
                                )
                            } else {
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .clickable { selectedPhase = phase }
                        .padding(vertical = 10.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = phase,
                            color = if (isSelected) Color.White else if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF554F65),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Distinct status dot / indicator
                        if (isCurrent) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else phaseColor)
                                )
                                Text(
                                    text = "TODAY",
                                    color = if (isSelected) Color.White else phaseColor,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        } else {
                            Text(
                                text = when (phase) {
                                    "Menstrual" -> "D 1-5"
                                    "Follicular" -> "D 6-13"
                                    "Ovulatory" -> "D 14-16"
                                    else -> "D 17-28"
                                },
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else if (user.darkMode) Color(0xFF8E889F) else Color(0xFF8B849A),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // 2. Cycle Syncing 4-Phase Timeline Indicator Bar
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = LunaRoseDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CYCLE PHASE TIMELINE",
                            color = LunaRoseDeep,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    // Active alignment indicator badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedPhase == activePhase) Color(0x2210B981) else LunaViolet.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (selectedPhase == activePhase) "● ALIGNED TODAY" else "○ PREVIEWING",
                            color = if (selectedPhase == activePhase) Color(0xFF10B981) else LunaViolet,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Visual proportional 4-segment cycle bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Menstrual: ~18% (5 days)
                    Box(
                        modifier = Modifier
                            .weight(0.18f)
                            .fillMaxSize()
                            .background(PhaseMenstrual.copy(alpha = if (selectedPhase == "Menstrual") 1f else 0.45f))
                    )
                    // Follicular: ~29% (8 days)
                    Box(
                        modifier = Modifier
                            .weight(0.29f)
                            .fillMaxSize()
                            .background(PhaseFollicular.copy(alpha = if (selectedPhase == "Follicular") 1f else 0.45f))
                    )
                    // Ovulatory: ~11% (3 days)
                    Box(
                        modifier = Modifier
                            .weight(0.11f)
                            .fillMaxSize()
                            .background(PhaseOvulation.copy(alpha = if (selectedPhase == "Ovulatory") 1f else 0.45f))
                    )
                    // Luteal: ~42% (12 days)
                    Box(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxSize()
                            .background(PhaseLuteal.copy(alpha = if (selectedPhase == "Luteal") 1f else 0.45f))
                    )
                }

                // Phase range labels under timeline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Menstrual (1-5)", color = PhaseMenstrual, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text("Follicular (6-13)", color = PhaseFollicular, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text("Ovulation (14-16)", color = PhaseOvulation, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text("Luteal (17-28)", color = PhaseLuteal, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }

                // Active status message with quick sync button if previewing another phase
                if (selectedPhase != activePhase) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LunaViolet.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Viewing $selectedPhase Phase Blueprint",
                            color = if (user.darkMode) Color(0xFFD6CEEE) else Color(0xFF4A4458),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LunaViolet)
                                .clickable { selectedPhase = activePhase }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "BACK TO TODAY",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x1810B981))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Text(
                            text = "Day $currentCycleDay of $cycleLength • You are in your $activePhase Phase today.",
                            color = if (user.darkMode) Color(0xFFA7F3D0) else Color(0xFF065F46),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Details for selected phase with complete, pristine indicators
        when (selectedPhase) {
            "Menstrual" -> {
                CycleSyncCard(
                    phaseName = "Menstrual Phase",
                    daysRange = "Days 1 to 5",
                    badge = "REST & INTUITION",
                    badgeColor = PhaseMenstrual,
                    superpower = "Heightened intuition, inward reflection, boundary setting, and foundational renewal.",
                    nutrition = "Iron-rich bone broths, spinach, beets, lentils, dark berries, and calming chamomile tea.",
                    seedCycling = "Phase 1: 1 tbsp ground Flax Seeds + 1 tbsp raw Pumpkin Seeds daily (Supports healthy estrogen production).",
                    workouts = "Gentle restorative yin yoga, slow walks, and light pelvic stretching. Honor slow pacing and deep rest.",
                    energy = "Lowest physical stamina, highest intuitive clarity. Perfect for journaling, vision setting, and delegating.",
                    isCurrentPhase = (activePhase == "Menstrual"),
                    darkMode = user.darkMode,
                    estrogenLevel = 0.20f,
                    estrogenStatus = "Low • Baseline",
                    progesteroneLevel = 0.10f,
                    progesteroneStatus = "Low • Baseline",
                    testosteroneLevel = 0.15f,
                    testosteroneStatus = "Low",
                    energyScore = 30,
                    energyDescriptor = "Rest & Conserve",
                    intensityLevel = 1,
                    intensityDescriptor = "Gentle Restorative (1/4)",
                    metabolismInfo = "Baseline metabolic burn. Lower caloric demands; prioritize micronutrient density and hydration."
                )
            }
            "Follicular" -> {
                CycleSyncCard(
                    phaseName = "Follicular Phase",
                    daysRange = "Days 6 to 13",
                    badge = "CREATIVITY & MOMENTUM",
                    badgeColor = PhaseFollicular,
                    superpower = "Brain neuroplasticity, curiosity, high drive, and rapid skill acquisition.",
                    nutrition = "Fermented kimchi/kefir, lean clean protein, avocados, citrus fruits, and vibrant crunchy salads.",
                    seedCycling = "Phase 1: 1 tbsp ground Flax Seeds + 1 tbsp raw Pumpkin Seeds daily (Supports follicle maturation).",
                    workouts = "HIIT intervals, progressive weight training, running, and dance workouts. Stamina is surging!",
                    energy = "High motivation & enthusiasm. Ideal for starting projects, brainstorming, and learning new skills.",
                    isCurrentPhase = (activePhase == "Follicular"),
                    darkMode = user.darkMode,
                    estrogenLevel = 0.75f,
                    estrogenStatus = "Rising Steadily",
                    progesteroneLevel = 0.10f,
                    progesteroneStatus = "Low • Resting",
                    testosteroneLevel = 0.45f,
                    testosteroneStatus = "Steady Rise",
                    energyScore = 85,
                    energyDescriptor = "High Vitality",
                    intensityLevel = 3,
                    intensityDescriptor = "Dynamic HIIT & Cardio (3/4)",
                    metabolismInfo = "High insulin sensitivity. Body processes complex carbohydrates efficiently for sustained energy."
                )
            }
            "Ovulatory" -> {
                CycleSyncCard(
                    phaseName = "Ovulatory Phase",
                    daysRange = "Days 14 to 16",
                    badge = "CONFIDENCE & RADIANCE",
                    badgeColor = PhaseOvulation,
                    superpower = "Magnetic communication, peak fertility, highest verbal eloquence, and social confidence.",
                    nutrition = "Anti-inflammatory wild salmon, asparagus, magnesium-rich almonds, pumpkin seeds, and leafy greens.",
                    seedCycling = "Transition: Transitioning toward raw Sunflower and Sesame seeds as ovulation completes.",
                    workouts = "Peak personal records (PRs), group fitness, power lifting, and high-intensity sweat sessions.",
                    energy = "Peak verbal eloquence and magnetism. Best time for public presentations, negotiations, and social dates.",
                    isCurrentPhase = (activePhase == "Ovulatory"),
                    darkMode = user.darkMode,
                    estrogenLevel = 1.0f,
                    estrogenStatus = "Peak Surge",
                    progesteroneLevel = 0.35f,
                    progesteroneStatus = "Awakening",
                    testosteroneLevel = 0.90f,
                    testosteroneStatus = "Peak Surge",
                    energyScore = 100,
                    energyDescriptor = "Maximum Vitality",
                    intensityLevel = 4,
                    intensityDescriptor = "Peak PRs & Power (4/4)",
                    metabolismInfo = "Elevated metabolic rate. Energy and stamina peak alongside resting body temperature shift."
                )
            }
            else -> {
                CycleSyncCard(
                    phaseName = "Luteal Phase",
                    daysRange = "Days 17 to 28",
                    badge = "FOCUS & NOURISHMENT",
                    badgeColor = PhaseLuteal,
                    superpower = "Deep focus, organization, detail editing, nesting, and wrapping up tasks.",
                    nutrition = "Complex carbs (roasted sweet potatoes, oats, quinoa), magnesium, dark chocolate, and vitamin B6.",
                    seedCycling = "Phase 2: 1 tbsp raw Sesame Seeds + 1 tbsp raw Sunflower Seeds daily (Boosts natural progesterone).",
                    workouts = "Strength training in early luteal, transitioning to pilates, barre, and calm hikes in late luteal.",
                    energy = "Metabolism burns 150–300 more calories daily! High attention to detail. Great for organizing and decluttering.",
                    isCurrentPhase = (activePhase == "Luteal"),
                    darkMode = user.darkMode,
                    estrogenLevel = 0.50f,
                    estrogenStatus = "Secondary Wave",
                    progesteroneLevel = 0.95f,
                    progesteroneStatus = "Peak Dominant",
                    testosteroneLevel = 0.20f,
                    testosteroneStatus = "Low",
                    energyScore = 60,
                    energyDescriptor = "Grounded Focus",
                    intensityLevel = 2,
                    intensityDescriptor = "Sculpt & Pilates (2/4)",
                    metabolismInfo = "Burns +150 to 300 kcal/day extra resting calories! Nourish with complex carbs to stave off PMS."
                )
            }
        }
    }
}

@Composable
fun CycleSyncCard(
    phaseName: String,
    daysRange: String,
    badge: String,
    badgeColor: Color,
    superpower: String,
    nutrition: String,
    seedCycling: String,
    workouts: String,
    energy: String,
    isCurrentPhase: Boolean = false,
    darkMode: Boolean,
    estrogenLevel: Float = 0.5f,
    estrogenStatus: String = "Moderate",
    progesteroneLevel: Float = 0.5f,
    progesteroneStatus: String = "Moderate",
    testosteroneLevel: Float = 0.5f,
    testosteroneStatus: String = "Moderate",
    energyScore: Int = 50,
    energyDescriptor: String = "Moderate",
    intensityLevel: Int = 2,
    intensityDescriptor: String = "Moderate",
    metabolismInfo: String = "Standard metabolic rate"
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        darkMode = darkMode
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Phase Name on the left (with weight), Days Range indicator on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                    Text(
                        text = phaseName,
                        color = if (darkMode) Color.White else Color(0xFF1E1B29),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Days range indicator pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (darkMode) Color(0x28FFFFFF) else Color(0xFFF3EDF7))
                        .border(
                            1.dp,
                            if (darkMode) Color.White.copy(alpha = 0.15f) else Color(0x18000000),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF4A4458),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = daysRange,
                            color = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF4A4458),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }

            // Phase Theme / Focus Banner Indicator: Wide, prominent banner with matching icon & non-clipping active tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeColor.copy(alpha = 0.16f))
                    .border(1.2.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = when {
                                badge.contains("INTUITION") -> Icons.Default.Spa
                                badge.contains("CREATIVITY") -> Icons.Default.Bolt
                                badge.contains("CONFIDENCE") -> Icons.Default.Favorite
                                else -> Icons.Default.Psychology
                            },
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }

                    if (isCurrentPhase) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.White))
                                Text(
                                    text = "ACTIVE NOW",
                                    color = Color.White,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                    }
                }
            }

            // Superpower
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SUPERPOWER",
                    color = badgeColor,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = superpower,
                    color = if (darkMode) Color(0xFFEDE8F7) else Color(0xFF2B2538),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // HORMONAL RHYTHM INDICATORS PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (darkMode) Color(0x20FFFFFF) else Color(0xFFF7F3FB))
                    .border(
                        1.dp,
                        if (darkMode) Color.White.copy(alpha = 0.1f) else Color(0x15000000),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "HORMONAL RHYTHM INDICATORS",
                            color = badgeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "BIOMARKERS",
                        color = if (darkMode) Color(0xFF9E98B0) else Color(0xFF7A738A),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Estrogen Indicator Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estrogen",
                            color = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF3B3549),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = estrogenStatus,
                            color = PhaseMenstrual,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (darkMode) Color(0x30FFFFFF) else Color(0x20000000))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(estrogenLevel.coerceIn(0.05f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(PhaseMenstrual)
                        )
                    }
                }

                // Progesterone Indicator Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progesterone",
                            color = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF3B3549),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = progesteroneStatus,
                            color = PhaseLuteal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (darkMode) Color(0x30FFFFFF) else Color(0x20000000))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progesteroneLevel.coerceIn(0.05f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(PhaseLuteal)
                        )
                    }
                }

                // Testosterone Indicator Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Testosterone & Stamina",
                            color = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF3B3549),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = testosteroneStatus,
                            color = Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (darkMode) Color(0x30FFFFFF) else Color(0x20000000))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(testosteroneLevel.coerceIn(0.05f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFF59E0B))
                        )
                    }
                }
            }

            // ENERGY & STAMINA INDICATOR
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Bolt, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                        Text(
                            text = "ENERGY & STAMINA GAUGE",
                            color = Color(0xFFD97706),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "$energyScore% • $energyDescriptor",
                        color = Color(0xFFD97706),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (darkMode) Color(0x30FFFFFF) else Color(0x20000000))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((energyScore / 100f).coerceIn(0.05f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                                )
                            )
                    )
                }
            }

            // WORKOUT INTENSITY GAUGE
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                        Text(
                            text = "WORKOUT INTENSITY GAUGE",
                            color = Color(0xFF0284C7),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = intensityDescriptor,
                        color = Color(0xFF0284C7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                // 4-Bar Segmented Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..4) {
                        val isFilled = i <= intensityLevel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isFilled) Color(0xFF0284C7) else (if (darkMode) Color(0x30FFFFFF) else Color(0x20000000))
                                )
                        )
                    }
                }
                Text(
                    text = workouts,
                    color = if (darkMode) Color(0xFFBBB5CC) else Color(0xFF554F65),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp
                )
            }

            // METABOLISM & CALORIC BURN INDICATOR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF97316).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFFF97316).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "METABOLISM & CALORIC SHIFT",
                            color = Color(0xFFEA580C),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = metabolismInfo,
                            color = if (darkMode) Color(0xFFFFEDD5) else Color(0xFF431407),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // NUTRITION & SEED CYCLING
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Restaurant, null, tint = LunaRose, modifier = Modifier.size(14.dp))
                    Text(
                        text = "FOOD & NUTRITION",
                        color = LunaRose,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = nutrition,
                    color = if (darkMode) Color(0xFFBBB5CC) else Color(0xFF554F65),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Seed Cycling Indicator Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(LunaViolet.copy(alpha = 0.14f))
                        .border(1.dp, LunaViolet.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = LunaViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Seed Cycling Protocol: $seedCycling",
                            color = if (darkMode) Color(0xFFE2DCF5) else LunaViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Work & Social Energy
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Psychology, null, tint = Color(0xFFA855F7), modifier = Modifier.size(14.dp))
                    Text(
                        text = "WORK & SOCIAL LIFE",
                        color = Color(0xFFA855F7),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = energy,
                    color = if (darkMode) Color(0xFFBBB5CC) else Color(0xFF554F65),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun BbtChartView(user: UserEntity, logs: List<LogEntity>) {
    val bbtLogs = remember(logs) {
        logs.filter { it.bbt != null }.sortedBy { it.date }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    Column {
                        Text(
                            text = "BASAL BODY TEMPERATURE",
                            color = LunaRoseDeep,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Biphasic Ovulation Curve",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LunaRose.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = LunaRoseDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "A sustained post-ovulation temperature rise of 0.2°C – 0.4°C confirms that ovulation has successfully occurred.",
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BBT Canvas Chart
                if (bbtLogs.size >= 2) {
                    val minTemp = (bbtLogs.minOfOrNull { it.bbt!! } ?: 36.2f) - 0.2f
                    val maxTemp = (bbtLogs.maxOfOrNull { it.bbt!! } ?: 37.2f) + 0.2f

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        // Horizontal coverline at 36.60°C
                        val coverlineY = h - (((36.60f - minTemp) / (maxTemp - minTemp)) * h)
                        drawLine(
                            color = Color(0x66FF85A1),
                            start = Offset(0f, coverlineY),
                            end = Offset(w, coverlineY),
                            strokeWidth = 2.dp.toPx()
                        )

                        // Path connecting temperatures
                        val path = Path()
                        val stepX = w / (bbtLogs.size - 1)

                        bbtLogs.forEachIndexed { i, log ->
                            val temp = log.bbt!!
                            val x = i * stepX
                            val y = h - (((temp - minTemp) / (maxTemp - minTemp)) * h)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                            // Point
                            drawCircle(
                                color = LunaRoseDeep,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(listOf(LunaRose, LunaViolet)),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFF3EDF7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log at least 2 morning temperatures in Fast Log to plot your BBT curve",
                            color = if (user.darkMode) Color(0xFFA8A3B5) else Color(0xFF757082),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Recent BBT and Cervical Mucus table
        if (bbtLogs.isNotEmpty()) {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkMode = user.darkMode
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "RECORDED TEMPERATURES",
                        color = LunaViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    bbtLogs.takeLast(5).reversed().forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = log.date,
                                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Fluid: ${log.cervicalMucus ?: "Not logged"}",
                                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = String.format("%.2f°C", log.bbt!!),
                                color = LunaRoseDeep,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CycleLengthBarChart(
    cycles: List<CycleEntity>,
    darkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val maxVal = (cycles.maxOfOrNull { it.length } ?: 35).toFloat().coerceAtLeast(30f)
    val animFraction by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700),
        label = "bar_anim"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barCount = cycles.size
        val barWidth = (w / (barCount * 2)).coerceAtLeast(20.dp.toPx())
        val spacing = (w - (barWidth * barCount)) / (barCount + 1)

        cycles.forEachIndexed { i, c ->
            val barHeight = ((c.length / maxVal) * (h - 40f)) * animFraction
            val x = spacing + i * (barWidth + spacing)
            val y = h - barHeight - 20f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(LunaRose, LunaRoseDeep)
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
    }
}

