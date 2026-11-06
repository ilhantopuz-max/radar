package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnalysisReport
import com.example.model.Bar
import com.example.model.IndicatorResult
import com.example.model.LevelType
import com.example.model.StockItem
import com.example.model.Timeframe
import com.example.ui.components.CandlestickChart
import com.example.ui.components.TimeframeSelector
import com.example.ui.theme.AboveAvgBullColor
import com.example.ui.theme.RadarBackground
import com.example.ui.theme.RadarBorder
import com.example.ui.theme.RadarCyan
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    stock: StockItem,
    bars: List<Bar>,
    indicator: IndicatorResult?,
    report: AnalysisReport?,
    timeframe: Timeframe,
    isLoading: Boolean,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onTimeframeChange: (Timeframe) -> Unit,
    onToggleFavorite: () -> Unit,
    onRefreshAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackClick()
    }

    val lastBar = bars.lastOrNull()
    val firstBar = bars.firstOrNull()
    val lastPrice = lastBar?.close ?: stock.basePrice
    val changePercent = if (firstBar != null && firstBar.close > 0) {
        ((lastPrice - firstBar.close) / firstBar.close) * 100.0
    } else 0.0
    val isPositive = changePercent >= 0

    var showAlarmSetNotification by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RadarBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stock.ticker,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = RadarCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = stock.group,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RadarCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = stock.name,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("detail_fav_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) RadarGold else TextMuted
                        )
                    }
                    IconButton(
                        onClick = onRefreshAnalysis,
                        modifier = Modifier.testTag("refresh_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Yenile",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RadarSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Price & 24h Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RadarSurface)
                    .border(1.dp, RadarBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Son Fiyat",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${formatPrice(lastPrice, stock.basePrice)} ${stock.currency}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Periyot Değişimi",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPositive) SignalGreen.copy(alpha = 0.2f) else SignalRed.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%+.2f%%", changePercent),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) SignalGreen else SignalRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timeframe Selector: 5dk, 15dk, 1s, 4s, 1g, 1h
            Text(
                text = "ZAMAN PERİYODU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RadarCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            TimeframeSelector(
                selectedTimeframe = timeframe,
                onTimeframeSelected = onTimeframeChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Candlestick Chart with MOST Lines & Volume
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(RadarSurface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RadarCyan, modifier = Modifier.size(36.dp))
                }
            } else {
                CandlestickChart(
                    bars = bars,
                    currency = stock.currency,
                    basePrice = stock.basePrice
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pine Script Indicator Metrics Cards
            indicator?.let { ind ->
                Text(
                    text = "MOST VE MUM GÖSTERGELERİ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Trail1 (EMA 3)
                    MetricCard(
                        title = "Trail1 (EMA-3)",
                        value = "${formatPrice(ind.trail1, stock.basePrice)} ${stock.currency}",
                        subtitle = "Hızlı Ortalama",
                        accentColor = Trail1LineColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Trail2 (MOST Stop)
                    MetricCard(
                        title = "Trail2 (MOST Stop)",
                        value = "${formatPrice(ind.trail2, stock.basePrice)} ${stock.currency}",
                        subtitle = if (ind.isBullTrend) "Stop Desteği" else "Direnç",
                        accentColor = if (ind.isBullTrend) SignalGreen else SignalRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Stop Mesafesi
                    MetricCard(
                        title = "Stop Mesafesi",
                        value = "-%${String.format(Locale.US, "%.2f", ind.distanceToStopPct)}",
                        subtitle = "Risk Oranı",
                        accentColor = RadarGold,
                        modifier = Modifier.weight(1f)
                    )

                    // Katı Gövdeli Mum
                    MetricCard(
                        title = "Mum Analizi",
                        value = if (ind.isSuperBullCandle) "Süper Güçlü" else if (ind.isBullAboveAvg) "Ort. Üstü" else "Normal",
                        subtitle = "Gövde / 15-SMA",
                        accentColor = if (ind.isSuperBullCandle) SuperBullColor else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // YORUM VE TEKNİK ANALİZ (Requested in prompt: "kısa bır yorum analız yap")
            report?.let { rep ->
                Text(
                    text = "TEKNİK YORUM VE ANALİZ RAPORU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RadarSurface)
                        .border(1.dp, RadarGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Verdict Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(RadarGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Insights,
                                    contentDescription = null,
                                    tint = RadarGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = rep.verdict,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SignalGreen
                                )
                                Text(
                                    text = "Sinyal Skoru: %${rep.signalScore}/100",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RadarSurfaceVariant
                        ) {
                            Text(
                                text = "R/R: ${rep.riskRewardRatio}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadarCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary commentary
                    Text(
                        text = rep.summaryCommentary,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 1: MOST İndikatörü
                    SectionCard(
                        title = "MOST Sinyal Durumu",
                        content = rep.mostAnalysis,
                        icon = Icons.Rounded.Shield,
                        tint = Trail1LineColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Section 2: Mum Formasyonu
                    SectionCard(
                        title = "Mum ve Alıcı Gücü (Solid Candle)",
                        content = rep.candleAnalysis,
                        icon = Icons.Rounded.Bolt,
                        tint = SuperBullColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Section 3: Momentum & RSI
                    SectionCard(
                        title = "RSI ve Momentum Değerlendirmesi",
                        content = rep.momentumAnalysis,
                        icon = Icons.Rounded.Speed,
                        tint = RadarCyan
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Levels Table (Stop, Pivot, Hedef 1, Hedef 2)
                    Text(
                        text = "Kritik Fiyat Seviyeleri & Hedefler",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RadarSurfaceVariant)
                            .padding(8.dp)
                    ) {
                        rep.keyLevels.forEach { level ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val dotColor = when (level.type) {
                                        LevelType.STOP_LOSS -> SignalRed
                                        LevelType.SUPPORT -> RadarCyan
                                        LevelType.PIVOT -> RadarGold
                                        LevelType.TARGET_1 -> SignalGreen
                                        LevelType.TARGET_2 -> SuperBullColor
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = level.title,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "${formatPrice(level.price, stock.basePrice)} ${stock.currency}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Strategy Recommendation
                    Text(
                        text = "Strateji ve Risk Yönetimi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadarGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rep.strategyRecommendation,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Alarm Kur & Bildirim
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RadarCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showAlarmSetNotification = true }
                        .testTag("set_alarm_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = RadarCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showAlarmSetNotification) "Alarm Kuruldu!" else "Fiyat Alarmı Kur",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RadarCyan
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SignalGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SignalGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onRefreshAnalysis() }
                        .testTag("refresh_radar_analysis_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = SignalGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analizi Güncelle",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SignalGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(RadarSurface)
            .border(1.dp, RadarBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(RadarSurfaceVariant.copy(alpha = 0.6f))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )
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
