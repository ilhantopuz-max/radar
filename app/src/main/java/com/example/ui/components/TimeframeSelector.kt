package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Timeframe
import com.example.ui.theme.RadarBackground
import com.example.ui.theme.RadarBorder
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun TimeframeSelector(
    selectedTimeframe: Timeframe,
    onTimeframeSelected: (Timeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeframes = listOf(
        Timeframe.M5,
        Timeframe.M15,
        Timeframe.H1,
        Timeframe.H4,
        Timeframe.D1,
        Timeframe.W1
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RadarSurfaceVariant)
            .border(1.dp, RadarBorder, RoundedCornerShape(10.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        timeframes.forEach { tf ->
            val isSelected = tf == selectedTimeframe
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) RadarCyan else Color.Transparent,
                label = "tf_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF00363F) else TextSecondaryColor(isSelected),
                label = "tf_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(bgColor)
                    .clickable { onTimeframeSelected(tf) }
                    .testTag("timeframe_chip_${tf.code}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tf.displayName,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun TextSecondaryColor(isSelected: Boolean): Color {
    return if (isSelected) Color.Black else TextMuted
}
