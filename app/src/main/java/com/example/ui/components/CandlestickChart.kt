package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Bar
import com.example.ui.theme.AboveAvgBullColor
import com.example.ui.theme.RadarBackground
import com.example.ui.theme.RadarBorder
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarGold
import com.example.ui.theme.RadarSurface
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalRed
import com.example.ui.theme.SuperBullColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Trail1LineColor
import com.example.ui.theme.Trail2BullColor
import com.example.ui.theme.Trail2BearColor
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun CandlestickChart(
    bars: List<Bar>,
    modifier: Modifier = Modifier,
    currency: String = "₺",
    basePrice: Double = 50.0
) {
    if (bars.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(RadarSurface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Grafik verisi yükleniyor...", color = TextSecondary)
        }
        return
    }

    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    val displayBar = selectedBarIndex?.let { bars.getOrNull(it) } ?: bars.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(RadarSurface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Inspection Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "F: ${formatPrice(displayBar.close, basePrice)} $currency",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (displayBar.close >= displayBar.open) SignalGreen else SignalRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (displayBar.isSuperBull) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SuperBullColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "SÜPER GÜÇLÜ MUM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperBullColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Row(modifier = Modifier.padding(top = 2.dp)) {
                    Text(
                        text = "A: ${formatPrice(displayBar.open, basePrice)} | Y: ${formatPrice(displayBar.high, basePrice)} | D: ${formatPrice(displayBar.low, basePrice)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Legend indicators
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Trail1LineColor, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trail1 (EMA3): ${formatPrice(displayBar.trail1, basePrice)}", fontSize = 10.sp, color = Trail1LineColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    val stopColor = if (displayBar.trail1 > displayBar.trail2) Trail2BullColor else Trail2BearColor
                    Box(modifier = Modifier.size(8.dp).background(stopColor, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trail2 (MOST): ${formatPrice(displayBar.trail2, basePrice)}", fontSize = 10.sp, color = stopColor)
                }
            }
        }

        // Chart Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .testTag("candlestick_chart_canvas")
                .pointerInput(bars) {
                    detectTapGestures { offset ->
                        val barWidth = size.width / bars.size.toFloat()
                        val index = (offset.x / barWidth).toInt().coerceIn(0, bars.lastIndex)
                        selectedBarIndex = index
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Layout: top 75% for candles & MOST lines, bottom 25% for volume
                val priceAreaHeight = canvasHeight * 0.74f
                val volumeAreaHeight = canvasHeight * 0.22f
                val volumeAreaTop = canvasHeight - volumeAreaHeight

                // Price min/max
                var minPrice = bars.minOf { min(it.low, min(it.trail1, it.trail2)) }
                var maxPrice = bars.maxOf { max(it.high, max(it.trail1, it.trail2)) }
                val pricePadding = (maxPrice - minPrice) * 0.08
                minPrice -= pricePadding
                maxPrice += pricePadding
                val priceRange = if (maxPrice - minPrice > 0) maxPrice - minPrice else 1.0

                // Max volume
                val maxVolume = bars.maxOf { it.volume }.coerceAtLeast(1L).toFloat()

                // Draw Grid lines
                val gridLines = 4
                for (g in 0..gridLines) {
                    val y = (priceAreaHeight / gridLines) * g
                    drawLine(
                        color = RadarBorder.copy(alpha = 0.35f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                val barCount = bars.size
                val barSpacing = canvasWidth / barCount.toFloat()
                val candleWidth = (barSpacing * 0.65f).coerceAtLeast(3f)

                // Trail1 and Trail2 paths
                val trail1Path = Path()
                val trail2Path = Path()

                // Coordinates function
                fun priceToY(price: Double): Float {
                    val normalized = (price - minPrice) / priceRange
                    return (priceAreaHeight - (normalized * priceAreaHeight)).toFloat().coerceIn(0f, priceAreaHeight)
                }

                fun volumeToY(vol: Long): Float {
                    val normalized = (vol.toFloat() / maxVolume).coerceIn(0f, 1f)
                    return canvasHeight - (normalized * volumeAreaHeight)
                }

                // Draw Candlesticks and Volume bars
                bars.forEachIndexed { i, bar ->
                    val xCenter = (i * barSpacing) + (barSpacing / 2f)

                    // Volume Bar
                    val volY = volumeToY(bar.volume)
                    val candleIsBull = bar.close >= bar.open

                    val candleColor = when {
                        bar.isSuperBull -> SuperBullColor
                        bar.isBullAboveAvg -> AboveAvgBullColor
                        candleIsBull -> SignalGreen
                        bar.isSuperBear -> Color(0xFFD201FC)
                        else -> SignalRed
                    }

                    drawRect(
                        color = candleColor.copy(alpha = 0.35f),
                        topLeft = Offset(xCenter - (candleWidth / 2f), volY),
                        size = Size(candleWidth, canvasHeight - volY)
                    )

                    // Wick
                    val highY = priceToY(bar.high)
                    val lowY = priceToY(bar.low)
                    drawLine(
                        color = candleColor,
                        start = Offset(xCenter, highY),
                        end = Offset(xCenter, lowY),
                        strokeWidth = 1.8f
                    )

                    // Candle Body
                    val openY = priceToY(bar.open)
                    val closeY = priceToY(bar.close)
                    val bodyTop = min(openY, closeY)
                    val bodyHeight = max(abs(closeY - openY), 2.5f)

                    drawRect(
                        color = candleColor,
                        topLeft = Offset(xCenter - (candleWidth / 2f), bodyTop),
                        size = Size(candleWidth, bodyHeight)
                    )

                    // Buy Crossover Signal Arrow / Marker
                    if (bar.isBuyCrossover) {
                        val markerY = lowY + 14f
                        val markerPath = Path().apply {
                            moveTo(xCenter, markerY - 8f)
                            lineTo(xCenter - 5f, markerY + 4f)
                            lineTo(xCenter + 5f, markerY + 4f)
                            close()
                        }
                        drawPath(markerPath, color = SignalGreen)
                    }

                    // Add to paths for MOST lines
                    val t1Y = priceToY(bar.trail1)
                    val t2Y = priceToY(bar.trail2)
                    if (i == 0) {
                        trail1Path.moveTo(xCenter, t1Y)
                        trail2Path.moveTo(xCenter, t2Y)
                    } else {
                        trail1Path.lineTo(xCenter, t1Y)
                        trail2Path.lineTo(xCenter, t2Y)
                    }
                }

                // Draw Trail1 Line (EMA 3)
                drawPath(
                    path = trail1Path,
                    color = Trail1LineColor,
                    style = Stroke(width = 2.5f)
                )

                // Draw Trail2 Line (MOST Stop line)
                val isBullLast = bars.last().trail1 > bars.last().trail2
                drawPath(
                    path = trail2Path,
                    color = if (isBullLast) Trail2BullColor else Trail2BearColor,
                    style = Stroke(
                        width = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                    )
                )

                // Highlight selected inspection bar line
                selectedBarIndex?.let { index ->
                    val x = (index * barSpacing) + (barSpacing / 2f)
                    drawLine(
                        color = RadarCyan.copy(alpha = 0.8f),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double, basePrice: Double): String {
    return when {
        basePrice < 0.01 -> String.format(Locale.US, "%.6f", price)
        basePrice < 1.0 -> String.format(Locale.US, "%.4f", price)
        basePrice < 100.0 -> String.format(Locale.US, "%.2f", price)
        else -> String.format(Locale.US, "%.1f", price)
    }
}
