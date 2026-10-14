package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftContainer
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.SleekNavyBg

/**
 * A decorative container providing a clean white patterned background
 * featuring an architectural micro-dot grid matrix and playful ambient aura.
 */
@Composable
fun SoftLimePatternBackground(
    modifier: Modifier = Modifier,
    dotSpacing: Dp = 24.dp,
    dotRadius: Dp = 1.0.dp,
    patternAlpha: Float = 0.22f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SleekNavyBg)
            .testTag("soft_lime_pattern_container")
    ) {
        // Decorative Canvas Background with Clean Dot Matrix Pattern & Pastel Auras on White
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val spacingPx = dotSpacing.toPx()
            val radiusPx = dotRadius.toPx()

            // 1. Ambient Bright Sky Glow (Top Right - Fresh & Cheerful)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.25f),
                        Color(0xFFBAE6FD).copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.95f, height * 0.06f),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(width * 0.95f, height * 0.06f)
            )

            // 2. Ambient Azure Glow (Mid-Bottom Left - Focused Learning Energy)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0284C7).copy(alpha = 0.16f),
                        Color(0xFFE0F2FE).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.05f, height * 0.60f),
                    radius = width * 0.65f
                ),
                radius = width * 0.65f,
                center = Offset(width * 0.05f, height * 0.60f)
            )

            // 3. Subtle Sky Blue Dot Matrix Grid Pattern
            if (spacingPx > 0) {
                var x = spacingPx / 2
                while (x < width) {
                    var y = spacingPx / 2
                    while (y < height) {
                        drawCircle(
                            color = Color(0xFF7DD3FC).copy(alpha = 0.40f),
                            radius = radiusPx,
                            center = Offset(x, y)
                        )
                        y += spacingPx
                    }
                    x += spacingPx
                }
            }
        }

        // Child Content
        content()
    }
}
