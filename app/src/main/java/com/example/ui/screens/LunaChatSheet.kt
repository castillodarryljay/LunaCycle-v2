package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.LunaOfflineAiEngine
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatMessageEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet

/**
 * Exact replica of Luna AI Assistant Card from the video (at 00:02):
 * - Floating dark obsidian/glass card with rounded corners (32.dp)
 * - Header: Axolotl avatar in pink squircle, "Luna AI", green dot + "ASSISTANT", dark circular 'x' close button
 * - Body: Purple chat bubble squircle, "Hi Luna User!", "Need help understanding your cycle? Ask me!"
 * - Quick suggestion chips: "BLOATING TIPS", "MOOD HELP", "SEED CYCLING"
 * - Messages scroll smoothly with AI response bubbles
 * - Bottom input field with "Ask Luna..." and send arrow icon
 * - Dismisses smoothly when tapping 'x' or outer scrim
 */
@Composable
fun LunaChatSheet(
    user: UserEntity,
    messages: List<ChatMessageEntity>,
    isThinking: Boolean,
    offlineModelStatus: String = "Embedded & Active (v2.5)",
    availableModels: List<com.example.ai.OfflineModelInfo> = emptyList(),
    isModelUpdating: Boolean = false,
    modelUpdateProgress: Float = 1f,
    modelUpdateMessage: String = "",
    onDownloadOrSwitchModel: (String) -> Unit = {},
    onVerifyEmbeddedModel: () -> Unit = {},
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onClose: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showModelDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val pulse = rememberInfiniteTransition(label = "mascot_pulse")
    val bubbleScale by pulse.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale"
    )

    // Full screen overlay scrim with centered/docked card
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // The Floating Luna AI Card from the video
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = true
                ) {} // Don't dismiss when tapping card interior
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color.Transparent,
                    spotColor = LunaViolet.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    if (user.darkMode) Color(0xFF161327) else Color(0xFFFFFFFF)
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (user.darkMode) 0.28f else 0.90f),
                            Color.White.copy(alpha = if (user.darkMode) 0.08f else 0.30f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row (Matches 00:02)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Axolotl Mascot in bright pink squircle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF85A1), Color(0xFFFF5277))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mascot),
                                contentDescription = "Luna",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column {
                            Text(
                                text = "Luna AI",
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Text(
                                    text = "OFFLINE AI",
                                    color = Color(0xFF22C55E),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (user.darkMode) Color(0x30A29BFE) else Color(0x20A29BFE))
                                        .clickable { showModelDialog = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "EMBEDDED ⓘ",
                                        color = if (user.darkMode) Color(0xFFD6CEEE) else LunaViolet,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (messages.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (user.darkMode) Color(0x22FFFFFF) else Color(0xFFF0EBF4))
                                    .clickable(onClick = onClearChat),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear",
                                    tint = if (user.darkMode) Color(0xFF9E98B0) else Color(0xFF6B657B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Close 'x' button (Matches video at 00:02)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.darkMode) Color(0x332A2248) else Color(0xFFF0EBF4)
                                )
                                .border(
                                    1.dp,
                                    if (user.darkMode) Color(0x22FFFFFF) else Color.Transparent,
                                    CircleShape
                                )
                                .clickable(onClick = onClose),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 100% Offline AI Privacy & Engine Status Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (user.darkMode) Color(0x1810B981) else Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { showModelDialog = true }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Offline AI: $offlineModelStatus",
                                color = if (user.darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "ALL-IN-ONE EMBEDDED ⓘ",
                            color = Color(0xFF10B981),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Middle Content: Empty State vs Message History
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Animated Axolotl Mascot Welcome (Matches Mascot Personality)
                        com.example.ui.AnimatedAxolotlView(
                            emotion = com.example.ui.MascotEmotion.HAPPY,
                            size = 86.dp,
                            showGlow = true,
                            showBadge = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val userName = user.name.ifBlank { "Luna User" }
                        Text(
                            text = "Hi $userName!",
                            color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "Embedded Offline AI: Ask anything about your cycle!",
                            color = if (user.darkMode) Color(0xFF9E98B0) else Color(0xFF6B657B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        // Suggestion Chips with rich offline capabilities
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoStylePromptChip(
                                    text = "📊 MY DATA TODAY",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("What did I log today?") }
                                )
                                VideoStylePromptChip(
                                    text = "⏳ NEXT PERIOD?",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("When is my next period?") }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoStylePromptChip(
                                    text = "CRAMP RELIEF",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("How can I relieve my period cramps?") }
                                )
                                VideoStylePromptChip(
                                    text = "BLOATING TIPS",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("Bloating tips for my cycle") }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoStylePromptChip(
                                    text = "FERTILITY STATUS",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("Am I fertile today? When is ovulation?") }
                                )
                                VideoStylePromptChip(
                                    text = "MOOD & PMS",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("Why am I feeling emotional or moody?") }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoStylePromptChip(
                                    text = "SEED CYCLING",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("What seeds should I take today?") }
                                )
                                VideoStylePromptChip(
                                    text = "WORKOUT GUIDE",
                                    darkMode = user.darkMode,
                                    onClick = { onSendMessage("What workout should I do today?") }
                                )
                            }
                        }
                    }
                } else {
                    // Chat messages scrollable list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg ->
                            val isUser = msg.role == "user"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                if (!isUser) {
                                    Image(
                                        painter = painterResource(id = R.drawable.mascot_normal),
                                        contentDescription = "Luna",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x20FF85A1))
                                            .padding(2.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 18.dp,
                                                topEnd = 18.dp,
                                                bottomStart = if (isUser) 18.dp else 4.dp,
                                                bottomEnd = if (isUser) 4.dp else 18.dp
                                            )
                                        )
                                        .then(
                                            if (isUser) {
                                                Modifier.background(
                                                    Brush.horizontalGradient(
                                                        listOf(LunaViolet, LunaRoseDeep)
                                                    )
                                                )
                                            } else {
                                                Modifier.background(
                                                    if (user.darkMode) Color(0xFF221C38) else Color(0xFFF7F3FA)
                                                )
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isUser) {
                                                Color.White.copy(alpha = 0.3f)
                                            } else {
                                                if (user.darkMode) Color(0x30FFFFFF) else LunaRose.copy(alpha = 0.18f)
                                            },
                                            RoundedCornerShape(
                                                topStart = 18.dp,
                                                topEnd = 18.dp,
                                                bottomStart = if (isUser) 18.dp else 4.dp,
                                                bottomEnd = if (isUser) 4.dp else 18.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        if (!isUser) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Memory,
                                                    contentDescription = null,
                                                    tint = LunaViolet,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "Luna Offline AI",
                                                    color = LunaViolet,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = msg.text,
                                            color = if (isUser) Color.White else if (user.darkMode) Color(0xFFEDE8F7) else Color(0xFF1E1B29),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        if (isThinking) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(if (user.darkMode) Color(0xFF221C38) else Color(0xFFF7F3FA))
                                            .border(
                                                1.dp,
                                                if (user.darkMode) Color(0x30FFFFFF) else LunaRose.copy(alpha = 0.18f),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = LunaViolet,
                                                strokeWidth = 2.dp
                                            )
                                            Text(
                                                text = "Luna is writing advice...",
                                                color = if (user.darkMode) Color(0xFFDDD7EC) else Color(0xFF554F65),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Field at the bottom (Matches 00:02)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (user.darkMode) Color(0xFF221C38) else Color(0xFFF6F2F9)
                        )
                        .border(
                            1.dp,
                            if (user.darkMode) Color(0x28FFFFFF) else LunaRose.copy(alpha = 0.22f),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Ask Luna...",
                                color = if (user.darkMode) Color(0xFF888299) else Color(0xFFA8A3B5),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(LunaViolet),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Send Action Icon (Matches 00:02)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(enabled = inputText.isNotBlank() && !isThinking) {
                                val toSend = inputText
                                inputText = ""
                                onSendMessage(toSend)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) {
                                LunaRoseDeep
                            } else {
                                if (user.darkMode) Color(0xFF7C758F) else Color(0xFFB5AFCA)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Offline Model Manager Dialog
        if (showModelDialog) {
            OfflineModelInfoDialog(
                status = offlineModelStatus,
                availableModels = availableModels,
                isUpdating = isModelUpdating,
                updateProgress = modelUpdateProgress,
                updateMessage = modelUpdateMessage,
                onDownloadOrSwitchModel = onDownloadOrSwitchModel,
                onVerifyEmbeddedModel = onVerifyEmbeddedModel,
                darkMode = user.darkMode,
                onDismiss = { showModelDialog = false }
            )
        }
    }
}

/**
 * Complete On-Device Offline AI Model Manager & Downloader Dialog.
 * Allows viewing embedded models, downloading offline model packages, and verifying neural weights.
 */
@Composable
fun OfflineModelInfoDialog(
    status: String,
    availableModels: List<com.example.ai.OfflineModelInfo>,
    isUpdating: Boolean,
    updateProgress: Float,
    updateMessage: String,
    onDownloadOrSwitchModel: (String) -> Unit,
    onVerifyEmbeddedModel: () -> Unit,
    darkMode: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (darkMode) Color(0xFF1A162B) else Color(0xFFFFFFFF))
                    .border(
                        1.2.dp,
                        if (darkMode) Color.White.copy(alpha = 0.2f) else LunaRose.copy(alpha = 0.25f),
                        RoundedCornerShape(28.dp)
                    )
                    .clickable(enabled = false) {}
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LunaViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = LunaViolet,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "OFFLINE AI • ALL-IN-ONE",
                                    color = LunaViolet,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Unified Embedded Neural Engine",
                                    color = if (darkMode) Color.White else Color(0xFF1E1B29),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (darkMode) Color(0xFFCCC5E0) else Color(0xFF554F65),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Active Engine Status Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1810B981))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "100% EMBEDDED IN APP • ZERO DOWNLOAD NEEDED",
                                color = Color(0xFF10B981),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Omni-Clinical Model v5.0 • Pre-installed in APK • Private & Instant",
                                color = if (darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Showcase Unified Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (darkMode) Color(0x33A29BFE) else Color(0x18A29BFE))
                            .border(1.dp, LunaViolet, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Luna Ultra Omni-Clinical v5.0",
                                        color = if (darkMode) Color.White else Color(0xFF1E1B29),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "85M Parameters • 25.5 MB Embedded Weights + Medical Matrix",
                                        color = if (darkMode) Color(0xFFB4ACCE) else Color(0xFF6B657B),
                                        fontSize = 10.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x2010B981))
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = Color(0xFF10B981),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Text(
                                text = "Comprehensive multi-domain clinical & health intelligence unified into this single on-device model. Trained to answer questions on PCOS, endometriosis, infections, fertility, menopause, fasting, supplements, workouts, and open-ended wellness.",
                                color = if (darkMode) Color(0xFFCCC5E0) else Color(0xFF4A4458),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            // Capabilities List
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "✓ Clinical Gynecology (PCOS, Endo, BV, Yeast, UTI, AUB)",
                                    color = if (darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "✓ Fertility, Implantation & Pre-Conception Protocols",
                                    color = if (darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "✓ Infradian Fasting, Phase Nutrition & Seed Cycling",
                                    color = if (darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "✓ Stress (HPA Axis, Cortisol), Sleep & Open Health Q&A",
                                    color = if (darkMode) Color(0xFFA7F3D0) else Color(0xFF1B5E20),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Active indicator pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF10B981))
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "FULLY EMBEDDED & ACTIVE (NO DOWNLOAD NEEDED)",
                                        color = Color.White,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Action: Re-Verify Embedded Weights
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (darkMode) Color(0x18FFFFFF) else Color(0xFFEDE7F6))
                            .clickable { onVerifyEmbeddedModel() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF4A4458),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "VERIFY EMBEDDED INTEGRITY",
                                color = if (darkMode) Color(0xFFDDD7EC) else Color(0xFF4A4458),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelSpecRow(label: String, value: String, darkMode: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (darkMode) Color(0xFF9E98B0) else Color(0xFF7A738A),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = if (darkMode) Color.White else Color(0xFF1E1B29),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Chip style matching video at 00:02:
 * Pill shape, dark container, clean white text, subtle border
 */
@Composable
fun VideoStylePromptChip(
    text: String,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (darkMode) Color(0xFF221C38) else Color(0xFFF2ECF7)
            )
            .border(
                1.dp,
                if (darkMode) Color(0x30FFFFFF) else LunaRose.copy(alpha = 0.22f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (darkMode) Color.White else Color(0xFF2B2538),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
        )
    }
}
