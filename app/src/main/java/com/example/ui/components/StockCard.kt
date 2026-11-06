package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScanResult
import com.example.model.SignalType
import com.example.ui.theme.RadarBorder
import com.example.ui.theme.RadarGold
import com.example.ui.theme.RadarSurface
import com.example.ui.theme.RadarSurfaceVariant
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalRed
import com.example.ui.theme.SuperBullColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Trail1LineColor
import java.util.Locale

@Composable
fun StockCard(
    result: ScanResult,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stock = result.stock
    val indicator = result.indicator
    val isPositive = result.changePercent >= 0

    val (badgeBg, badgeText, badgeTitle) = when (result.signalType) {
        SignalType.FRESH_BUY -> Triple(SignalGreen.copy(alpha = 0.2f), SignalGreen, "YENİ AL")
        SignalType.SUPER_BULL_BUY -> Triple(SuperBullColor.copy(alpha = 0.2f), SuperBullColor, "SÜPER MUM")
        SignalType.BULL_TREND -> Triple(Color(0xFF00B0FF).copy(alpha = 0.2f), Color(0xFF00B0FF), "AL TRENDİ")
        SignalType.NEUTRAL -> Triple(RadarSurfaceVariant, TextMuted, "NÖTR")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RadarSurface)
            .border(1.dp, if (result.signalType == SignalType.FRESH_BUY) SignalGreen.copy(alpha = 0.6f) else RadarBorder, RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .testTag("stock_card_${stock.ticker}")
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Ticker, Badges, Favorite & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ticker & Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.ticker,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            if (result.signalType == SignalType.FRESH_BUY) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = null,
                                    tint = badgeText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                            Text(
                                text = badgeTitle,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                    }

                    if (indicator.isSuperBullCandle && result.signalType != SignalType.SUPER_BULL_BUY) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SuperBullColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "GÜÇLÜ MUM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperBullColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Price & Change
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${formatPrice(result.lastPrice, stock.basePrice)} ${stock.currency}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                            contentDescription = null,
                            tint = if (isPositive) SignalGreen else SignalRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format(Locale.US, "%+.2f%%", result.changePercent),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPositive) SignalGreen else SignalRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle: Company Name & Group/Sector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stock.name,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${stock.group} • ${stock.sector}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Indicators bar: Trail1 (EMA 3), Trail2 (MOST Stop), Stop mesafesi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(RadarSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = Trail1LineColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MOST Stop: ${formatPrice(indicator.stopLossLevel, stock.basePrice)} ${stock.currency}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "Fark: -%${String.format(Locale.US, "%.1f", indicator.distanceToStopPct)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (indicator.distanceToStopPct < 3.0) SignalGreen else RadarGold
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp).testTag("fav_button_${stock.ticker}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favori",
                        tint = if (isFavorite) RadarGold else TextMuted,
                        modifier = Modifier.size(18.dp)
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
