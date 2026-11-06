package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.SignalGreen

@Composable
fun RadarPulseAnimation(
    modifier: Modifier = Modifier,
    isScanning: Boolean = false,
    color: Color = RadarCyan
) {
    val transition = rememberInfiniteTransition(label = "radar_anim")
    val radiusRatio by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(36.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f

            // Inner Center Dot
            drawCircle(
                color = color,
                radius = 3.5f,
                center = center
            )

            // Concentric Rings
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = maxRadius * 0.5f,
                center = center,
                style = Stroke(width = 1f)
            )

            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = maxRadius * 0.9f,
                center = center,
                style = Stroke(width = 1f)
            )

            if (isScanning) {
                // Expanding ripple
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = maxRadius * radiusRatio,
                    center = center,
                    style = Stroke(width = 1.5f)
                )

                // Sonar Sweep Line
                val angleRad = Math.toRadians(rotationAngle.toDouble())
                val sweepEnd = Offset(
                    x = center.x + (maxRadius * kotlin.math.cos(angleRad)).toFloat(),
                    y = center.y + (maxRadius * kotlin.math.sin(angleRad)).toFloat()
                )
                drawLine(
                    color = color.copy(alpha = 0.7f),
                    start = center,
                    end = sweepEnd,
                    strokeWidth = 2f
                )
            }
        }
    }
}
