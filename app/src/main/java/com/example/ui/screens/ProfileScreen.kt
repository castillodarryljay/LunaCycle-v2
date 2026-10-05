package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LunaPeach
import com.example.ui.theme.LunaPurple
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet
import com.example.util.LunaNotificationHelper
import com.example.util.MedicalReportExporter
import com.example.util.ProfileImageUtil
import java.io.File

@Composable
fun ProfileScreen(
    user: UserEntity,
    cycles: List<CycleEntity> = emptyList(),
    logs: List<LogEntity> = emptyList(),
    onSaveUser: (UserEntity) -> Unit,
    onOpenSettings: () -> Unit,
    onResetData: () -> Unit
) {
    val context = LocalContext.current

    // Dual photo pickers: Modern PickVisualMedia + GetContent fallback with permanent storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val localPath = ProfileImageUtil.saveProfileImageLocally(context, uri)
            if (localPath != null) {
                onSaveUser(user.copy(profilePic = localPath))
                Toast.makeText(context, "Profile picture saved permanently!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val localPath = ProfileImageUtil.saveProfileImageLocally(context, uri)
            if (localPath != null) {
                onSaveUser(user.copy(profilePic = localPath))
                Toast.makeText(context, "Profile picture saved permanently!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var resetConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your profile",
                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (user.darkMode) Color(0x28FFFFFF) else Color(0xFFF9F5FB))
                    .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else LunaRose.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = LunaViolet,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Profile Main Card with Avatar
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkMode = user.darkMode
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with Camera icon
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(16.dp, CircleShape, spotColor = LunaRose.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(LunaRose.copy(alpha = 0.3f), LunaViolet.copy(alpha = 0.3f))
                                )
                            )
                            .border(
                                3.dp,
                                Brush.linearGradient(listOf(Color.White, LunaRose)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val currentAvatarPath = user.profilePic ?: ProfileImageUtil.getPersistedAvatarPath(context)
                        val avatarFile = remember(currentAvatarPath) {
                            currentAvatarPath?.let { File(it) }
                        }
                        val hasValidImage = (avatarFile != null && avatarFile.exists() && avatarFile.length() > 0) ||
                                (!currentAvatarPath.isNullOrEmpty() && (currentAvatarPath.startsWith("http") || currentAvatarPath.startsWith("content://")))

                        if (hasValidImage) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(if (avatarFile?.exists() == true) avatarFile else currentAvatarPath)
                                    .memoryCacheKey(avatarFile?.lastModified()?.toString() ?: currentAvatarPath)
                                    .diskCacheKey(avatarFile?.lastModified()?.toString() ?: currentAvatarPath)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.mascot),
                                contentDescription = "Default Mascot",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Camera button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .align(Alignment.BottomEnd)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(LunaRoseDeep, LunaViolet))
                            )
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    getContentLauncher.launch("image/*")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.name.ifBlank { "Luna User" },
                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Age: ${user.age} • ${if (user.isIrregular) "Irregular Cycle" else "Regular Cycle"}",
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (resetConfirm) "CONFIRM CLEAR ALL DATA?" else "RESET APP DATA",
                    color = if (resetConfirm) Color(0xFFEF4444) else LunaViolet,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.clickable {
                        if (resetConfirm) {
                            onResetData()
                            resetConfirm = false
                            Toast.makeText(context, "Data reset complete", Toast.LENGTH_SHORT).show()
                        } else {
                            resetConfirm = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (user.darkMode) Color(0x24FFFFFF) else Color(0xFFFBF8FC))
                            .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AVERAGE CYCLE",
                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${user.cycleLength}d",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (user.darkMode) Color(0x24FFFFFF) else Color(0xFFFBF8FC))
                            .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.12f) else LunaRose.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AVERAGE PERIOD",
                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${user.periodLength}d",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mascot Emotion Gallery Showcase (18 Axolotl Emotions)
                var previewEmotion by remember { mutableStateOf(com.example.ui.MascotEmotion.HAPPY) }

                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    darkMode = user.darkMode
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "AXOLOTL MASCOT MOODS",
                                    color = previewEmotion.glowColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(previewEmotion.glowColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "18 EMOTIONS",
                                        color = previewEmotion.glowColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // Preview Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (user.darkMode) Color(0x30FFFFFF) else Color(0xFFF9F5FB))
                                .border(1.dp, previewEmotion.glowColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.example.ui.AnimatedAxolotlView(
                                emotion = previewEmotion,
                                size = 64.dp,
                                showGlow = true,
                                showBadge = true
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = previewEmotion.title,
                                    color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = com.example.ui.MascotEmotion.getSpeechBubbleMessage(previewEmotion, "your", user.name),
                                    color = if (user.darkMode) Color(0xFFD1CCE0) else Color(0xFF4A4458),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        // Horizontal Emotion Selector Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(com.example.ui.MascotEmotion.entries) { em ->
                                val isSelected = (previewEmotion == em)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) em.glowColor.copy(alpha = 0.25f) else if (user.darkMode) Color(0x20FFFFFF) else Color(0xFFF5F0F8))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) em.glowColor else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { previewEmotion = em }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = em.drawableRes),
                                            contentDescription = em.title,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = em.emoji,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Edit Cycle Profile Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = LunaRoseDeep.copy(alpha = 0.35f))
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(LunaRoseDeep, LunaViolet)
                            )
                        )
                        .clickable(onClick = onOpenSettings)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EDIT CYCLE PROFILE",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        // --- MEDICAL REPORT EXPORT CARD (For OB-GYN / Doctor Visits) ---
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
                            .background(LunaViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Medical",
                            tint = LunaViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "CLINICAL & DOCTOR EXPORT",
                            color = LunaViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "OB-GYN Visit Summary",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Text(
                    text = "Generate a clean, clinical-grade summary of your past cycle lengths, regularity, symptom frequency, and BBT shift for your physician visit.",
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp)
                )

                // Export & Download Buttons: Responsive, clear, and downloadable
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary: Download PDF
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(LunaRoseDeep, LunaViolet))
                            )
                            .clickable {
                                MedicalReportExporter.downloadPdf(context, user, cycles, logs)
                            }
                            .padding(vertical = 13.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Download, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text("DOWNLOAD DOCTOR PDF REPORT", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        }
                    }

                    // Primary: Download CSV
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                            .border(1.2.dp, LunaViolet.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                            .clickable {
                                MedicalReportExporter.downloadCsv(context, user, cycles, logs)
                            }
                            .padding(vertical = 13.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Download, null, tint = LunaViolet, modifier = Modifier.size(18.dp))
                            Text("DOWNLOAD RAW CSV SPREADSHEET", color = LunaViolet, fontSize = 11.5.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        }
                    }

                    // Secondary: Share Options Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFEFEAF5))
                                .border(1.dp, LunaRose.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .clickable {
                                    MedicalReportExporter.generateAndSharePdf(context, user, cycles, logs)
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Share, null, tint = LunaRoseDeep, modifier = Modifier.size(15.dp))
                                Text("SHARE PDF", color = LunaRoseDeep, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFEFEAF5))
                                .border(1.dp, if (user.darkMode) Color.White.copy(alpha = 0.15f) else Color(0x22000000), RoundedCornerShape(14.dp))
                                .clickable {
                                    MedicalReportExporter.generateAndShareCsv(context, user, cycles, logs)
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Share, null, tint = if (user.darkMode) Color.White else Color(0xFF1E1B29), modifier = Modifier.size(15.dp))
                                Text("SHARE CSV", color = if (user.darkMode) Color.White else Color(0xFF1E1B29), fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // --- SMART NOTIFICATIONS & PREDICTIVE ALERTS CARD ---
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
                            .background(LunaRose.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerts",
                            tint = LunaRoseDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "SMART PREDICTIONS",
                            color = LunaRoseDeep,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Cycle Alerts & Reminders",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Period Approaching Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Period Approaching Alert",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Remind me ${user.notificationPeriodReminderDays} days before predicted flow",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = user.notificationPeriodEnabled,
                        onCheckedChange = { onSaveUser(user.copy(notificationPeriodEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LunaRoseDeep
                        )
                    )
                }

                // Days selector
                if (user.notificationPeriodEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3).forEach { days ->
                            val isSelected = user.notificationPeriodReminderDays == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) LunaRoseDeep else if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7)
                                    )
                                    .clickable {
                                        onSaveUser(user.copy(notificationPeriodReminderDays = days))
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$days ${if (days == 1) "day" else "days"} before",
                                    color = if (isSelected) Color.White else if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fertile Window Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fertile Window & Ovulation",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Alerts for estimated peak conception days",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = user.notificationFertileEnabled,
                        onCheckedChange = { onSaveUser(user.copy(notificationFertileEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LunaViolet
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Logging Reminder Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Evening Log Prompt",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gentle 8:00 PM check-in to log flow, mood, water & symptoms",
                            color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = user.notificationDailyLogEnabled,
                        onCheckedChange = { onSaveUser(user.copy(notificationDailyLogEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LunaPeach
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Late Period Check-in Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (user.darkMode) Color(0x18FFFFFF) else Color(0xFFF9F6FC))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💜 EMPATHETIC LATE PERIOD CHECK-IN",
                        color = LunaViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "If your period is overdue by 3+ days, Luna sends gentle support offering logging adjustments or discreet pregnancy test reminders.",
                        color = if (user.darkMode) Color(0xFFCCC5E0) else Color(0xFF6B657B),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PREVIEW / TEST NOTIFICATIONS",
                    color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test Action Buttons Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .border(1.dp, LunaRose.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .clickable {
                                    LunaNotificationHelper.sendPeriodApproachingNotification(context, user.notificationPeriodReminderDays)
                                    Toast.makeText(context, "Period approaching alert sent!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔔 Test Period Alert", color = LunaRoseDeep, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .border(1.dp, LunaViolet.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .clickable {
                                    LunaNotificationHelper.sendFertileWindowNotification(context, true)
                                    Toast.makeText(context, "Ovulation alert sent!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌸 Test Ovulation Alert", color = LunaViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .border(1.dp, LunaPeach.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .clickable {
                                    LunaNotificationHelper.sendDailyLogReminder(context)
                                    Toast.makeText(context, "Daily log reminder sent!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌙 Test Daily Prompt", color = Color(0xFFE65100), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF3EDF7))
                                .border(1.dp, LunaViolet.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .clickable {
                                    LunaNotificationHelper.sendLatePeriodCheckin(context, 4)
                                    Toast.makeText(context, "Late period check-in sent!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💜 Test Late Check-in", color = LunaViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    user: UserEntity,
    onSave: (UserEntity) -> Unit,
    onDismiss: () -> Unit,
    onResetData: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var age by remember { mutableStateOf(user.age.toString()) }
    var cycleLength by remember { mutableStateOf(user.cycleLength.toString()) }
    var periodLength by remember { mutableStateOf(user.periodLength.toString()) }
    var isIrregular by remember { mutableStateOf(user.isIrregular) }
    var darkMode by remember { mutableStateOf(user.darkMode) }
    var waterGoal by remember { mutableStateOf(user.waterGoalMl.toString()) }
    var resetConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66080512))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            // Ambient soft glowing aura behind dialog to simulate optical frosted glass diffusion
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                LunaRoseDeep.copy(alpha = 0.30f),
                                LunaViolet.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // High-contrast frosted glass card container
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .widthIn(max = 440.dp)
                    .fillMaxWidth()
                    .imePadding()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        if (darkMode) Color(0xF6181428) else Color(0xFDFFFFFF)
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                if (darkMode) Color.White.copy(alpha = 0.25f) else LunaRose.copy(alpha = 0.5f),
                                if (darkMode) Color.White.copy(alpha = 0.08f) else LunaViolet.copy(alpha = 0.25f)
                            )
                        ),
                        RoundedCornerShape(32.dp)
                    )
                    .clickable(enabled = false) {} // Prevent backdrop dismiss when tapping inside dialog
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Profile & Health",
                        color = if (darkMode) Color.White else Color(0xFF1E1B29),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (darkMode) Color.White else Color.Black
                        )
                    }
                }

                // Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LunaRoseDeep,
                        unfocusedBorderColor = if (darkMode) Color.White.copy(alpha = 0.2f) else Color(0x33000000)
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaRoseDeep,
                            unfocusedBorderColor = if (darkMode) Color.White.copy(alpha = 0.2f) else Color(0x33000000)
                        )
                    )

                    OutlinedTextField(
                        value = waterGoal,
                        onValueChange = { waterGoal = it.filter { c -> c.isDigit() } },
                        label = { Text("Water (ml)") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaRoseDeep,
                            unfocusedBorderColor = if (darkMode) Color.White.copy(alpha = 0.2f) else Color(0x33000000)
                        )
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = cycleLength,
                        onValueChange = { cycleLength = it.filter { c -> c.isDigit() } },
                        label = { Text("Cycle Length") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaRoseDeep,
                            unfocusedBorderColor = if (darkMode) Color.White.copy(alpha = 0.2f) else Color(0x33000000)
                        )
                    )

                    OutlinedTextField(
                        value = periodLength,
                        onValueChange = { periodLength = it.filter { c -> c.isDigit() } },
                        label = { Text("Period Length") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LunaRoseDeep,
                            unfocusedBorderColor = if (darkMode) Color.White.copy(alpha = 0.2f) else Color(0x33000000)
                        )
                    )
                }

                // Irregular cycle toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Irregular Cycle?",
                        color = if (darkMode) Color.White else Color(0xFF1E1B29),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isIrregular,
                        onCheckedChange = { isIrregular = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LunaRoseDeep
                        )
                    )
                }

                // Dark mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Liquid Dark Mode",
                        color = if (darkMode) Color.White else Color(0xFF1E1B29),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LunaViolet
                        )
                    )
                }

                // Reset data button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (resetConfirm) Color(0xFFEF4444) else if (darkMode) Color(0x22FFFFFF) else Color(0x10000000))
                        .clickable {
                            if (resetConfirm) {
                                onResetData()
                                onDismiss()
                            } else {
                                resetConfirm = true
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (resetConfirm) "CONFIRM: ERASE ALL HISTORY" else "RESET ALL APPLICATION DATA",
                        color = if (resetConfirm) Color.White else Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Action buttons: Cancel & Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (darkMode) Color(0x22FFFFFF) else Color(0x10000000))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CANCEL",
                            color = if (darkMode) Color.White else Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = LunaRoseDeep.copy(alpha = 0.35f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LunaRoseDeep, LunaViolet)
                                )
                            )
                            .clickable {
                                val updated = user.copy(
                                    name = name.ifBlank { "User" },
                                    age = age.toIntOrNull() ?: 25,
                                    cycleLength = cycleLength.toIntOrNull() ?: 28,
                                    periodLength = periodLength.toIntOrNull() ?: 5,
                                    isIrregular = isIrregular,
                                    darkMode = darkMode,
                                    waterGoalMl = waterGoal.toIntOrNull() ?: 2000
                                )
                                onSave(updated)
                                onDismiss()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "APPLY CHANGES",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
}
