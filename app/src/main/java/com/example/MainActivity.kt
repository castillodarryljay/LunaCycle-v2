package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.LunaNotificationHelper
import com.example.util.ProfileImageUtil
import java.io.File
import com.example.ui.MainViewModel
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FastLogScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.LunaChatSheet
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.AmbientLiquidBackground
import com.example.ui.theme.LunaCycleTheme
import com.example.ui.theme.LunaRose
import com.example.ui.theme.LunaRoseDeep
import com.example.ui.theme.LunaViolet
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LunaNotificationHelper.initChannels(this)
        setContent {
            val user by viewModel.user.collectAsStateWithLifecycle()
            val cycles by viewModel.cycles.collectAsStateWithLifecycle()
            val logs by viewModel.logs.collectAsStateWithLifecycle()
            val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
            val activeView by viewModel.activeView.collectAsStateWithLifecycle()
            val showSplash by viewModel.showSplash.collectAsStateWithLifecycle()
            val isChatOpen by viewModel.isChatOpen.collectAsStateWithLifecycle()
            val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
            val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()

            LunaCycleTheme(darkTheme = user.darkMode) {
                AnimatedContent(
                    targetState = showSplash,
                    transitionSpec = {
                        (fadeIn(tween(500, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.96f, animationSpec = tween(500, easing = FastOutSlowInEasing)))
                            .togetherWith(
                                fadeOut(tween(400, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 1.04f, animationSpec = tween(400, easing = FastOutSlowInEasing))
                            )
                    },
                    label = "splash_screen_transition"
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(onSkip = { viewModel.skipSplash() })
                    } else {
                        LunaCycleMainContent(
                            viewModel = viewModel,
                            activeView = activeView,
                            user = user,
                            cycles = cycles,
                            logs = logs,
                            chatMessages = chatMessages,
                            isChatOpen = isChatOpen,
                            isSettingsOpen = isSettingsOpen,
                            isAiThinking = isAiThinking
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSkip: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onSkip()
    }

    val pulse = rememberInfiniteTransition(label = "splash_pulse")

    // Outer glow breathing
    val glowScale by pulse.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Secondary ripple aura expanding outward
    val rippleScale by pulse.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple"
    )
    val rippleAlpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )

    // Axolotl mascot vertical bobbing hover
    val floatOffset by pulse.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Mascot playful tilt/wobble angle
    val tiltAngle by pulse.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    // Mascot breathing scale
    val mascotScale by pulse.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_scale"
    )

    // Rotating iridescent halo ring
    val ringRotation by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Liquid progress sweep animation
    val progressSweep by pulse.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    // Shimmering sparkle twinkle
    val sparkleAlpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0A1C),
                        Color(0xFF140F2D),
                        Color(0xFF090714)
                    )
                )
            )
            .clickable(onClick = onSkip), // Tap anywhere to enter immediately!
        contentAlignment = Alignment.Center
    ) {
        // Celestial starry dust canvas in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.15f, size.height * 0.12f),
                Offset(size.width * 0.82f, size.height * 0.18f),
                Offset(size.width * 0.28f, size.height * 0.28f),
                Offset(size.width * 0.74f, size.height * 0.34f),
                Offset(size.width * 0.10f, size.height * 0.48f),
                Offset(size.width * 0.88f, size.height * 0.55f),
                Offset(size.width * 0.22f, size.height * 0.72f),
                Offset(size.width * 0.78f, size.height * 0.78f),
                Offset(size.width * 0.35f, size.height * 0.86f),
                Offset(size.width * 0.65f, size.height * 0.88f),
                Offset(size.width * 0.50f, size.height * 0.08f),
                Offset(size.width * 0.92f, size.height * 0.92f)
            )
            starPositions.forEachIndexed { index, pos ->
                val radius = if (index % 3 == 0) 2.2f else if (index % 2 == 0) 1.5f else 1.0f
                val alpha = if (index % 2 == 0) sparkleAlpha * 0.8f else (1f - sparkleAlpha) * 0.7f + 0.2f
                val color = if (index % 3 == 0) Color(0xFFFF85A1).copy(alpha = alpha) else Color(0xFFA29BFE).copy(alpha = alpha)
                drawCircle(
                    color = color,
                    radius = radius * density,
                    center = pos
                )
            }
        }

        // Ambient deep radial glowing aura
        Box(
            modifier = Modifier
                .size(380.dp)
                .scale(glowScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x35FF85A1),
                            Color(0x286C5CE7),
                            Color.Transparent
                        )
                    )
                )
        )

        // Concentric expanding liquid ripple wave
        Box(
            modifier = Modifier
                .size(260.dp)
                .scale(rippleScale)
                .clip(CircleShape)
                .border(1.5.dp, Color(0xFFFF85A1).copy(alpha = rippleAlpha), CircleShape)
        )

        // Skip Button (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
                .clickable(onClick = onSkip)
                .padding(horizontal = 16.dp, vertical = 9.dp)
        ) {
            Text(
                text = "ENTER ➔",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }

        // Central Mascot & Animated Brand Branding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mascot Animated Container
            Box(
                modifier = Modifier
                    .offset(y = floatOffset.dp)
                    .size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer breathing aura ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x55FF85A1),
                                    Color(0x30A29BFE),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating iridescent gradient border ring
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .rotate(ringRotation)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    LunaRose,
                                    LunaViolet,
                                    Color(0xFF74B9FF),
                                    LunaRoseDeep,
                                    LunaRose
                                )
                            ),
                            CircleShape
                        )
                )

                // Frosted glass inner capsule
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color(0x40FF85A1)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                )

                // Floating, Tilting, Breathing Axolotl Mascot
                Image(
                    painter = painterResource(id = R.drawable.mascot),
                    contentDescription = "Luna",
                    modifier = Modifier
                        .size(130.dp)
                        .scale(mascotScale)
                        .rotate(tiltAngle),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Shimmering Gradient Brand Title
            Text(
                text = "LunaCycle",
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,
                            LunaRose,
                            LunaViolet,
                            Color.White
                        )
                    ),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "✦ LIQUID GLASS CYCLE TRACKING ✦",
                color = LunaViolet.copy(alpha = 0.9f + (sparkleAlpha * 0.1f)),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "In sync with your natural rhythm & hormonal wellness",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Hormonal Phase Pills Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val phases = listOf("🩸 MENSTRUAL", "🌱 FOLLICULAR", "✨ OVULATORY", "🌙 LUTEAL")
                phases.forEach { phase ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(0.8.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phase,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Animated Frosted Glass Loading Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressSweep)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(LunaRoseDeep, LunaViolet, Color.White)
                                    )
                                )
                        )
                    }

                    val progressPercent = (progressSweep * 100).toInt().coerceIn(12, 100)
                    val statusText = when {
                        progressSweep < 0.40f -> "ALIGNING CYCLES... $progressPercent%"
                        progressSweep < 0.75f -> "HARMONIZING DATA... $progressPercent%"
                        else -> "PREPARING INSIGHTS... $progressPercent%"
                    }

                    Text(
                        text = statusText,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LunaCycleMainContent(
    viewModel: MainViewModel,
    activeView: String,
    user: com.example.data.model.UserEntity,
    cycles: List<com.example.data.model.CycleEntity>,
    logs: List<com.example.data.model.LogEntity>,
    chatMessages: List<com.example.data.model.ChatMessageEntity>,
    isChatOpen: Boolean,
    isSettingsOpen: Boolean,
    isAiThinking: Boolean
) {
    // BackHandler: return to dashboard or close chat
    BackHandler(enabled = isChatOpen) {
        viewModel.closeChat()
    }
    BackHandler(enabled = !isChatOpen && activeView != "dashboard") {
        viewModel.setActiveView("dashboard")
    }

    val bgBlurRadius by animateDpAsState(
        targetValue = if (isSettingsOpen) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "settings_bg_blur"
    )

    val insightsTab by viewModel.insightsTab.collectAsStateWithLifecycle()

    val offlineModelStatus by viewModel.offlineModelStatus.collectAsStateWithLifecycle()
    val availableModels by viewModel.availableModels.collectAsStateWithLifecycle()
    val isModelUpdating by viewModel.isModelUpdating.collectAsStateWithLifecycle()
    val modelUpdateProgress by viewModel.modelUpdateProgress.collectAsStateWithLifecycle()
    val modelUpdateMessage by viewModel.modelUpdateMessage.collectAsStateWithLifecycle()

    AmbientLiquidBackground(darkMode = user.darkMode) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .then(if (bgBlurRadius > 0.dp) Modifier.blur(bgBlurRadius) else Modifier),
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth()) {
                        LunaCycleHeader(
                            user = user,
                            onProfileClick = { viewModel.setActiveView("profile") }
                        )
                    }
                }
            },
            bottomBar = {
                VideoStyleLiquidGlassBottomBar(
                    activeView = activeView,
                    darkMode = user.darkMode,
                    onNavigate = { view -> viewModel.setActiveView(view) },
                    onOpenFastLog = { viewModel.setActiveView("logs") }
                )
            },
            floatingActionButton = {
                if (!isChatOpen && activeView != "logs") {
                    FloatingLunaAiButton(
                        darkMode = user.darkMode,
                        onClick = { viewModel.openChat() }
                    )
                }
            }
        ) { innerPadding ->
            // Flexible responsive container: beautifully centered on any screen (phone, foldable, tablet)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 640.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    AnimatedContent(
                        targetState = activeView,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                                scaleIn(initialScale = 0.95f, animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                                        scaleOut(targetScale = 1.02f, animationSpec = tween(180, easing = FastOutSlowInEasing))
                                )
                        },
                        label = "screen_transition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            "dashboard" -> DashboardScreen(
                                user = user,
                                cycles = cycles,
                                logs = logs,
                                onNavigate = { view -> viewModel.setActiveView(view) },
                                onAddWater = { amount -> viewModel.addWater(amount) }
                            )
                            "calendar" -> CalendarScreen(
                                user = user,
                                cycles = cycles,
                                logs = logs,
                                onSaveLog = { log -> viewModel.saveLog(log) },
                                onDeleteLog = { id -> viewModel.deleteLog(id) }
                            )
                            "logs" -> FastLogScreen(
                                user = user,
                                cycles = cycles,
                                logs = logs,
                                onSave = { log -> viewModel.saveLog(log) },
                                onComplete = { viewModel.setActiveView("dashboard") }
                            )
                            "insights" -> InsightsScreen(
                                user = user,
                                cycles = cycles,
                                logs = logs,
                                onDeleteLog = { id -> viewModel.deleteLog(id) },
                                onResetData = { viewModel.resetData() },
                                initialTab = insightsTab
                            )
                            "profile" -> ProfileScreen(
                                user = user,
                                cycles = cycles,
                                logs = logs,
                                onSaveUser = { u -> viewModel.saveUser(u) },
                                onOpenSettings = { viewModel.openSettings() },
                                onResetData = { viewModel.resetData() }
                            )
                        }
                    }
                }
            }
        }

        // Floating Luna AI Assistant Card (Matches video at 00:02)
        AnimatedVisibility(
            visible = isChatOpen,
            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.92f, animationSpec = tween(250, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.92f, animationSpec = tween(200, easing = FastOutSlowInEasing))
        ) {
            LunaChatSheet(
                user = user,
                messages = chatMessages,
                isThinking = isAiThinking,
                offlineModelStatus = offlineModelStatus,
                availableModels = availableModels,
                isModelUpdating = isModelUpdating,
                modelUpdateProgress = modelUpdateProgress,
                modelUpdateMessage = modelUpdateMessage,
                onDownloadOrSwitchModel = { modelId -> viewModel.downloadOrSwitchModel(modelId) },
                onVerifyEmbeddedModel = { viewModel.verifyEmbeddedWeights() },
                onSendMessage = { text -> viewModel.sendChatMessage(text) },
                onClearChat = { viewModel.clearChat() },
                onClose = { viewModel.closeChat() }
            )
        }

        // Settings dialog
        if (isSettingsOpen) {
            SettingsDialog(
                user = user,
                onSave = { updated -> viewModel.saveUser(updated) },
                onDismiss = { viewModel.closeSettings() },
                onResetData = { viewModel.resetData() }
            )
        }
    }
}

@Composable
fun LunaCycleHeader(
    user: com.example.data.model.UserEntity,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(listOf(LunaRose.copy(alpha = 0.25f), LunaViolet.copy(alpha = 0.25f)))
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mascot),
                    contentDescription = "Luna Mascot",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "LunaCycle",
                color = if (user.darkMode) Color.White else Color(0xFF1E1B29),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }

        // Profile Avatar Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(8.dp, CircleShape, spotColor = LunaRose.copy(alpha = 0.3f))
                .clip(CircleShape)
                .background(if (user.darkMode) Color(0x33FFFFFF) else Color.White)
                .border(2.dp, LunaRose, CircleShape)
                .clickable(onClick = onProfileClick)
                .testTag("profile_button"),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            val currentAvatarPath = user.profilePic ?: ProfileImageUtil.getPersistedAvatarPath(context)
            val avatarFile = remember(currentAvatarPath) {
                currentAvatarPath?.let { File(it) }
            }
            val hasValidPic = (avatarFile != null && avatarFile.exists() && avatarFile.length() > 0) ||
                (!currentAvatarPath.isNullOrEmpty() && (currentAvatarPath.startsWith("http") || currentAvatarPath.startsWith("content://")))

            if (hasValidPic) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(if (avatarFile?.exists() == true) avatarFile else currentAvatarPath)
                        .memoryCacheKey(avatarFile?.lastModified()?.toString() ?: currentAvatarPath)
                        .diskCacheKey(avatarFile?.lastModified()?.toString() ?: currentAvatarPath)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.mascot),
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingLunaAiButton(
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val pulse = rememberInfiniteTransition(label = "ai_pulse")
    val dotScale by pulse.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot"
    )

    // Smooth floating hovering animation
    val floatAnim by pulse.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .offset(y = floatAnim.dp)
            .padding(bottom = 14.dp, end = 8.dp)
            .size(54.dp)
            .shadow(16.dp, RoundedCornerShape(18.dp), ambientColor = Color.Transparent, spotColor = Color(0xFFA29BFE).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFB4A7D6))
            .border(
                1.5.dp,
                Color.White.copy(alpha = 0.65f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .testTag("luna_ai_button"),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.mascot_normal),
            contentDescription = "Luna AI",
            modifier = Modifier.size(36.dp),
            contentScale = ContentScale.Fit
        )

        // Pulsing notification dot (coral/pink from video)
        Box(
            modifier = Modifier
                .size(10.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-4).dp, y = 4.dp)
                .scale(dotScale)
                .clip(CircleShape)
                .background(Color(0xFFFF5277))
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

/**
 * Exact replica of the video's Liquid Glass Bottom Navigation Bar:
 * - Frosted liquid glass pill on the left containing the items
 * - Soft rounded spotlight highlight capsule behind the active item
 * - Active icon & text tinted with vibrant color
 * - Separate detached circular floating glass action button on the right
 * - Flexible across any screen size with widthIn constraint
 * - Crystalline glass without dark murky box artifacts
 */
@Composable
fun VideoStyleLiquidGlassBottomBar(
    activeView: String,
    darkMode: Boolean,
    onNavigate: (String) -> Unit,
    onOpenFastLog: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Left Liquid Glass Pill Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color.Transparent,
                        spotColor = if (darkMode) LunaViolet.copy(alpha = 0.25f) else LunaRose.copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        if (darkMode) Color(0xEE161324) else Color(0xF8FFFFFF)
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (darkMode) 0.35f else 0.95f),
                                Color.White.copy(alpha = if (darkMode) 0.08f else 0.40f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navItems = listOf(
                        Triple("dashboard", "Home", Icons.Default.GridView),
                        Triple("calendar", "Calendar", Icons.Default.CalendarMonth),
                        Triple("insights", "Insights", Icons.Default.Timeline),
                        Triple("profile", "Profile", Icons.Default.Person)
                    )

                    navItems.forEach { (viewKey, label, icon) ->
                        val isActive = (activeView == viewKey)

                        val iconScale by animateFloatAsState(
                            targetValue = if (isActive) 1.15f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                            label = "tab_icon_scale"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    if (isActive) {
                                        if (darkMode) Color(0x35FFFFFF) else LunaRose.copy(alpha = 0.16f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable { onNavigate(viewKey) }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.scale(iconScale)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isActive) {
                                        LunaRoseDeep
                                    } else {
                                        if (darkMode) Color.White.copy(alpha = 0.85f) else Color(0xFF554F65)
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    color = if (isActive) {
                                        LunaRoseDeep
                                    } else {
                                        if (darkMode) Color(0xFF9E98AE) else Color(0xFF7A748A)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Right Circular Floating Glass Action Button
            val isLogActive = (activeView == "logs")
            val logScale by animateFloatAsState(
                targetValue = if (isLogActive) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "log_fab_scale"
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(logScale)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = Color.Transparent,
                        spotColor = if (darkMode) LunaViolet.copy(alpha = 0.35f) else LunaRoseDeep.copy(alpha = 0.35f)
                    )
                    .clip(CircleShape)
                    .then(
                        if (isLogActive) {
                            Modifier.background(Brush.linearGradient(listOf(LunaRoseDeep, LunaViolet)))
                        } else {
                            Modifier.background(if (darkMode) Color(0xEE161324) else Color(0xF8FFFFFF))
                        }
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (darkMode) 0.35f else 0.95f),
                                Color.White.copy(alpha = if (darkMode) 0.08f else 0.40f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(onClick = onOpenFastLog)
                    .testTag("nav_fast_log_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Fast Log",
                    tint = if (isLogActive) Color.White else (if (darkMode) Color.White else Color(0xFF1E1B29)),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
