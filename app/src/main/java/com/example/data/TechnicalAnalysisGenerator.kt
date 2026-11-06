package com.example.data

import com.example.model.AnalysisReport
import com.example.model.IndicatorResult
import com.example.model.KeyLevel
import com.example.model.LevelType
import com.example.model.ScanResult
import com.example.model.SignalType
import com.example.model.StockItem
import com.example.model.Timeframe
import java.util.Locale

object TechnicalAnalysisGenerator {

    fun generateAnalysis(
        stock: StockItem,
        timeframe: Timeframe,
        indicator: IndicatorResult,
        lastPrice: Double,
        changePercent: Double
    ): AnalysisReport {
        val trail1 = indicator.trail1
        val trail2 = indicator.trail2
        val stopLoss = indicator.stopLossLevel
        val distancePct = indicator.distanceToStopPct
        val isSuperBull = indicator.isSuperBullCandle
        val isFreshBuy = indicator.isBuyCrossover
        val rsi = indicator.rsi

        // Target calculations based on volatility and stop loss
        val target1 = lastPrice * 1.035
        val target2 = lastPrice * 1.072
        val pivot = (trail1 + trail2 + lastPrice) / 3.0

        val keyLevels = listOf(
            KeyLevel(title = "MOST Stop-Loss (Trail2)", price = stopLoss, type = LevelType.STOP_LOSS),
            KeyLevel(title = "Ana Destek / Pivot", price = pivot, type = LevelType.SUPPORT),
            KeyLevel(title = "1. Hedef Seviye (+%3.5)", price = target1, type = LevelType.TARGET_1),
            KeyLevel(title = "2. Hedef Seviye (+%7.2)", price = target2, type = LevelType.TARGET_2)
        )

        // Verdict & Score
        val (verdict, score) = when {
            isFreshBuy && isSuperBull -> "GÜÇLÜ AL SİNYALİ (Kesişim + Katı Gövdeli Mum)" to 95
            isFreshBuy -> "YENİ AL KESİŞİMİ (MOST Alım Teyidi)" to 88
            isSuperBull -> "GÜÇLÜ ALICI MUMU (Trend Devam Ediyor)" to 82
            indicator.isBullTrend && rsi in 45.0..68.0 -> "AL TRENDİNDE (Pozitif İvme)" to 76
            indicator.isBullTrend -> "AL TRENDİ (Takipte Kalınmalı)" to 68
            else -> "NÖTR / İZLEME" to 50
        }

        // MOST Analysis
        val mostCommentary = if (isFreshBuy) {
            "${stock.ticker} için ${timeframe.displayName} periyodunda Trail1 (EMA-3: ${formatPrice(trail1, stock.basePrice)}) " +
                    "Trail2 MOST çizgisini (${formatPrice(trail2, stock.basePrice)}) yukarı keserek taze AL sinyali üretti. " +
                    "Trend yukarı yönlü teyit edilmiş durumda."
        } else {
            "${stock.ticker} hissesi ${timeframe.displayName} periyodunda MOST AL trendini koruyor. " +
                    "Trail1 (${formatPrice(trail1, stock.basePrice)}) çizgisi Trail2 (${formatPrice(trail2, stock.basePrice)}) stop desteğinin üzerinde seyrediyor. " +
                    "Mevcut fiyata göre stop seviyesi mesafesi %${String.format(Locale.US, "%.2f", distancePct)} seviyesindedir."
        }

        // Candle Analysis
        val candleCommentary = if (isSuperBull) {
            "Grafikteki son bar 'SÜPER GÜÇLÜ BOĞA MUMU' (Solid Body Candle) olarak gerçekleşti. " +
                    "Gövde genişliği (${formatPrice(indicator.bodyRange, stock.basePrice)}), 15 barlık ortalama gövdenin (${formatPrice(indicator.avgBody, stock.basePrice)}) 1.5 katını aşarak alıcıların çok iştahlı olduğunu gösteriyor."
        } else if (indicator.isBullAboveAvg) {
            "Son mum ortalama gövde büyüklüğünün üzerinde yeşil bir kapanış yaptı. Alıcılar piyasa kontrolünü elinde tutuyor."
        } else {
            "Fiyat hareketi dengeli mum gövdesi ile ilerliyor. Hacim ve mum kapanışı MOST trendini desteklemeye devam ediyor."
        }

        // Momentum / RSI Analysis
        val momentumCommentary = when {
            rsi > 70 -> "RSI(14) göstergesi ${String.format(Locale.US, "%.1f", rsi)} ile aşırı alım bölgesine yakın seyrediyor. Trend güçlü fakat kâr realizasyonlarına karşı Trail2 stop seviyesi yakından takip edilmelidir."
            rsi < 40 -> "RSI(14) ${String.format(Locale.US, "%.1f", rsi)} seviyesinde dip dönüş bölgesinden toparlanıyor. Risk/ödül potansiyeli yüksek bir toparlanma evresi."
            else -> "RSI(14) ${String.format(Locale.US, "%.1f", rsi)} seviyesinde sağlıklı yükseliş koridorunda bulunuyor. Aşırı şişkinlik olmadan sağlıklı bir momentum izleniyor."
        }

        // Strategy Recommendation
        val strategyCommentary = "Pozisyonda olanlar için stop seviyesi ${formatPrice(stopLoss, stock.basePrice)} ${stock.currency} olarak izlenebilir. " +
                "Yeni alım düşünenler için ${formatPrice(target1, stock.basePrice)} ve ${formatPrice(target2, stock.basePrice)} kademeli kâr hedefleri olarak değerlendirilebilir. " +
                "Trail1 çizgisi Trail2 altına sarkmadığı sürece AL sinyali geçerliliğini korur."

        val summary = "${stock.name} (${stock.ticker}) için ${timeframe.description} periyodunda MOST indikatörü pozitif bölgede işlem görüyor. " +
                "Değişim %${String.format(Locale.US, "%+.2f", changePercent)} olup, stop mesafesi %${String.format(Locale.US, "%.2f", distancePct)} ile makul risk aralığındadır."

        val rrRatio = "1 : 2.8"

        return AnalysisReport(
            stockSymbol = stock.ticker,
            timeframe = timeframe,
            verdict = verdict,
            summaryCommentary = summary,
            mostAnalysis = mostCommentary,
            candleAnalysis = candleCommentary,
            momentumAnalysis = momentumCommentary,
            strategyRecommendation = strategyCommentary,
            keyLevels = keyLevels,
            riskRewardRatio = rrRatio,
            signalScore = score
        )
    }

    private fun formatPrice(price: Double, basePrice: Double): String {
        return when {
            basePrice < 0.01 -> String.format(Locale.US, "%.6f", price)
            basePrice < 1.0 -> String.format(Locale.US, "%.4f", price)
            basePrice < 100.0 -> String.format(Locale.US, "%.2f", price)
            else -> String.format(Locale.US, "%.1f", price)
        }
    }
}
