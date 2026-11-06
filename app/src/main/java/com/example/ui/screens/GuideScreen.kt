package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.Trail2BullColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RadarBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = RadarCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Empires VI Tarama Rehberi",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RadarSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RadarSurface)
                    .border(1.dp, RadarCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = RadarCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Radar,
                                    contentDescription = null,
                                    tint = RadarCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ilkhanate Radar Sistemi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "MOST (Moving Stop Loss) + Solid Candle",
                                fontSize = 12.sp,
                                color = RadarCyan
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Bu uygulama yüklediğiniz 'Empires VI Tarama [ilkhanate]' Pine Script algoritmasını doğrudan çalıştırır. Borsa İstanbul Grup 1-16 (tüm hisseler), Binance kripto çiftleri ve BIST endekslerini 5dk, 15dk, 1s, 4s, 1g ve 1h periyotlarında tarayarak AL sinyallerini listeler.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // Section 1: MOST İndikatörü
            GuideCard(
                title = "1. MOST (Moving Stop Loss) Mantığı",
                icon = Icons.Rounded.Shield,
                iconTint = Trail1LineColor
            ) {
                Text(
                    text = "• Trail1: 3 periyotluk Üstel Hareketli Ortalama (EMA-3).\n" +
                            "• AF2 (%2): Dinamik takip eden stop yüzdesi.\n" +
                            "• Trail2 (MOST Stop): Fiyat yükseldikçe yukarı çekilen, düşüşlerde sabit kalan akıllı stop çizgisi.\n" +
                            "• AL Sinyali (Buy): Trail1 çizgisi Trail2 çizgisini yukarı yönlü kestiğinde (ta.crossover) oluşur.\n" +
                            "• Trend Durumu: Trail1 > Trail2 olduğu sürece AL trendi korunur.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Section 2: Solid Candle (Katı Gövdeli Mum)
            GuideCard(
                title = "2. Solid Body Candle (Güçlü Mum)",
                icon = Icons.Rounded.Bolt,
                iconTint = SuperBullColor
            ) {
                Text(
                    text = "• Gövde Büyüklüğü: BodyRange = |Kapanış - Açılış|\n" +
                            "• 15 Bar Ortalaması: ta.sma(BodyRange, 15)\n" +
                            "• SÜPER BOĞA MUMU: Gövde büyüklüğü, son 15 barın ortalamasının 1.5 katından büyük ve yeşilse oluşur (Parlak Yeşil Mum).\n" +
                            "• Alıcıların kurumsal ve hacimli şekilde tahtayı ele geçirdiğini teyit eder.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Section 3: Periyot Seçimi
            GuideCard(
                title = "3. Zaman Periyotları Kullanımı",
                icon = Icons.Rounded.Timeline,
                iconTint = RadarGold
            ) {
                Text(
                    text = "• 5dk & 15dk: Gün içi (scalping & day-trading) hızlı sinyaller.\n" +
                            "• 1s & 4s: Birkaç günlük kısa vadeli swing trade işlemleri.\n" +
                            "• 1g (Günlük): Orta vadeli ana trend takibi ve güçlü kurumsal kırılımlar.\n" +
                            "• 1h (Haftalık): Uzun vadeli ana yön belirleme.\n" +
                            "Üst barda yer alan 6 periyot butonundan dilediğinizi seçerek anında tarama yapabilirsiniz.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Section 4: Hisse Tıklama & Analiz
            GuideCard(
                title = "4. Detaylı Veri ve Otomatik Analiz",
                icon = Icons.Rounded.CheckCircle,
                iconTint = SignalGreen
            ) {
                Text(
                    text = "Taramada çıkan herhangi bir hisseye tıkladığınızda:\n" +
                            "1. Canlı mum grafiği, Trail1 ve Trail2 çizgileri açılır.\n" +
                            "2. Grafiğin zaman dilimini tek tıkla değiştirebilirsiniz.\n" +
                            "3. Stop-Loss seviyesi ve stop mesafesi hesaplanır.\n" +
                            "4. Yapay zeka ve kural tabanlı uzman teknik yorum (MOST sinyali, mum formasyonu, RSI ve kâr hedefleri) anında sunulur.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun GuideCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RadarSurface)
            .border(1.dp, RadarBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
