package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AmbientLiquidBackground(
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "liquid_orbs")
    val animOffset1 by transition.animateFloat(
        initialValue = -60f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )
    val animOffset2 by transition.animateFloat(
        initialValue = 50f,
        targetValue = -90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (darkMode) Color(0xFF110E21) else Color(0xFFFBF7FC))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (darkMode) {
                // Top-Right Rose Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x32FF85A1),
                            Color(0x14A29BFE),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f + animOffset1, h * 0.15f + animOffset2),
                        radius = w * 0.7f
                    )
                )

                // Center-Left Ethereal Violet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x28A29BFE),
                            Color(0x0E60A5FA),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f + animOffset2, h * 0.55f - animOffset1),
                        radius = w * 0.75f
                    )
                )

                // Bottom-Right Coral
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x22FF98B2),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.75f - animOffset2, h * 0.85f + animOffset1),
                        radius = w * 0.65f
                    )
                )
            } else {
                // Light Mode Pearlescent Iridescence
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x28FEB6C7),
                            Color(0x10E4C6FE),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f + animOffset1, h * 0.12f + animOffset2),
                        radius = w * 0.75f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1CD4C6FE),
                            Color(0x0CB6E3FE),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f + animOffset2, h * 0.5f - animOffset1),
                        radius = w * 0.7f
                    )
                )
            }
        }

        content()
    }
}

/**
 * Returns a luminous, translucent frosted glass background color for inner components.
 * Eliminates all harsh dark/black box artifacts in both light and dark themes.
 */
@Composable
fun glassComponentBg(darkMode: Boolean, isHighlighted: Boolean = false): Brush {
    return if (isHighlighted) {
        Brush.horizontalGradient(listOf(LunaRoseDeep.copy(alpha = 0.25f), LunaViolet.copy(alpha = 0.20f)))
    } else if (darkMode) {
        Brush.verticalGradient(
            listOf(
                Color(0x2AFFFFFF),
                Color(0x1AFFFFFF)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xD9FFFFFF),
                Color(0xBFFFFFFF)
            )
        )
    }
}

fun glassBorderColor(darkMode: Boolean, isSelected: Boolean = false): Color {
    return if (isSelected) {
        LunaRose
    } else if (darkMode) {
        Color.White.copy(alpha = 0.20f)
    } else {
        LunaRose.copy(alpha = 0.18f)
    }
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    shape: Shape = RoundedCornerShape(32.dp),
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundBrush = if (darkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x42271F42),
                Color(0x2D1D1635),
                Color(0x38221B3E)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xF5FFFFFF),
                Color(0xEBFFFFFF),
                Color(0xF0FFFFFF)
            )
        )
    }

    val borderBrush = if (darkMode) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.32f),
                Color.White.copy(alpha = 0.08f),
                LunaViolet.copy(alpha = 0.22f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.70f),
                LunaRose.copy(alpha = 0.25f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    }

    // Clean chromatic glow - zero dark rectangle artifacts
    val baseModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = Color.Transparent,
            spotColor = if (darkMode) LunaViolet.copy(alpha = 0.22f) else LunaRose.copy(alpha = 0.16f)
        )
        .clip(shape)
        .background(backgroundBrush)
        .border(width = 1.2.dp, brush = borderBrush, shape = shape)

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = LunaRose),
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(modifier = finalModifier, content = content)
}

@Composable
fun LiquidGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    accentColor: Color = LunaRose,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(24.dp)
    val bgBrush = if (darkMode) {
        Brush.horizontalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.24f),
                accentColor.copy(alpha = 0.10f)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.16f),
                Color.White.copy(alpha = 0.90f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgBrush)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = if (darkMode) Color.White else accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
        }
    }
}

/**
 * Reusable luminous Liquid Glass Chip to replace all murky dark gray boxes
 */
@Composable
fun LuminousGlassChip(
    text: String,
    isSelected: Boolean,
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    selectedColor: Color = LunaViolet,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(selectedColor, LunaRoseDeep))
    } else {
        if (darkMode) {
            Brush.horizontalGradient(
                listOf(Color(0x22FFFFFF), Color(0x14FFFFFF))
            )
        } else {
            Brush.horizontalGradient(
                listOf(Color(0xFFF7F3FA), Color(0xFFFDF8FC))
            )
        }
    }

    val borderColor = if (isSelected) {
        Color.White.copy(alpha = 0.5f)
    } else {
        if (darkMode) Color.White.copy(alpha = 0.18f) else LunaRose.copy(alpha = 0.20f)
    }

    val textColor = if (isSelected) {
        Color.White
    } else {
        if (darkMode) Color(0xFFECE7F6) else Color(0xFF383147)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgBrush)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
        )
    }
}
