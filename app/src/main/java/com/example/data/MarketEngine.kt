package com.example.data

import com.example.model.Bar
import com.example.model.IndicatorResult
import com.example.model.ScanResult
import com.example.model.SignalType
import com.example.model.StockItem
import com.example.model.Timeframe
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object MarketEngine {

    /**
     * Calculates the exact Pine Script indicator logic:
     * - Moving average EMA(3) = Trail1
     * - Percent Stop AF2 = 2% (0.02)
     * - Trailing stop = Trail2
     * - Buy crossover and Solid Body candle
     */
    fun calculateIndicators(rawBars: List<Bar>, emaPeriod: Int = 3, stopLossPercent: Double = 0.02): IndicatorResult {
        if (rawBars.isEmpty()) {
            return IndicatorResult(
                trail1 = 0.0,
                trail2 = 0.0,
                isBuyCrossover = false,
                isBullTrend = false,
                stopLossLevel = 0.0,
                distanceToStopPct = 0.0,
                isSuperBullCandle = false,
                isBullAboveAvg = false,
                bodyRange = 0.0,
                avgBody = 0.0,
                rsi = 50.0,
                bars = emptyList()
            )
        }

        val enrichedBars = mutableListOf<Bar>()
        val alpha = 2.0 / (emaPeriod + 1.0) // 0.5 for per=3
        var prevEma = rawBars.first().close
        var prevTrail2 = prevEma - (prevEma * stopLossPercent)
        var prevTrail1 = prevEma

        val bodyRanges = mutableListOf<Double>()

        for (i in rawBars.indices) {
            val bar = rawBars[i]
            val body = abs(bar.close - bar.open)
            bodyRanges.add(body)

            // 15-bar SMA of Body Range
            val lookbackStart = max(0, i - 14)
            val window = bodyRanges.subList(lookbackStart, i + 1)
            val avgBody = window.average()

            // Super BULL & Super BEAR
            val isBull = bar.close > bar.open
            val isSuperBull = body > (avgBody * 1.5) && isBull
            val isSuperBear = body > (avgBody * 1.5) && !isBull
            val isBullAboveAvg = body > avgBody && isBull

            // Trail1 (EMA 3)
            val currentEma = if (i == 0) bar.close else (bar.close * alpha) + (prevEma * (1.0 - alpha))
            val trail1 = currentEma

            // SL2
            val sl2 = trail1 * stopLossPercent

            // Pine Script Trail2 logic:
            // iff_1 = Trail1 > Trail2[1] ? Trail1 - SL2 : Trail1 + SL2
            // iff_2 = Trail1 < Trail2[1] and Trail1[1] < Trail2[1] ? min(Trail2[1], Trail1 + SL2) : iff_1
            // Trail2 = Trail1 > Trail2[1] and Trail1[1] > Trail2[1] ? max(Trail2[1], Trail1 - SL2) : iff_2
            val iff1 = if (trail1 > prevTrail2) trail1 - sl2 else trail1 + sl2
            val iff2 = if (trail1 < prevTrail2 && prevTrail1 < prevTrail2) {
                min(prevTrail2, trail1 + sl2)
            } else {
                iff1
            }
            val trail2 = if (trail1 > prevTrail2 && prevTrail1 > prevTrail2) {
                max(prevTrail2, trail1 - sl2)
            } else {
                iff2
            }

            // Buy / Sell crossover
            val isBuyCrossover = (trail1 > trail2) && (prevTrail1 <= prevTrail2)
            val isSellCrossover = (trail1 < trail2) && (prevTrail1 >= prevTrail2)

            enrichedBars.add(
                bar.copy(
                    trail1 = trail1,
                    trail2 = trail2,
                    bodyRange = body,
                    avgBody = avgBody,
                    isSuperBull = isSuperBull,
                    isSuperBear = isSuperBear,
                    isBullAboveAvg = isBullAboveAvg,
                    isBuyCrossover = isBuyCrossover,
                    isSellCrossover = isSellCrossover
                )
            )

            prevTrail1 = trail1
            prevTrail2 = trail2
            prevEma = currentEma
        }

        val lastBar = enrichedBars.last()
        val isBullTrend = lastBar.trail1 > lastBar.trail2
        val stopLossLevel = lastBar.trail2
        val distancePct = if (lastBar.close > 0) ((lastBar.close - stopLossLevel) / lastBar.close) * 100.0 else 0.0

        // Calculate RSI (14 period)
        val rsi = calculateRSI(enrichedBars.map { it.close }, 14)

        // Fresh crossover if occurred in the last 2 bars
        val isBuySignal = enrichedBars.takeLast(2).any { it.isBuyCrossover }

        return IndicatorResult(
            trail1 = lastBar.trail1,
            trail2 = lastBar.trail2,
            isBuyCrossover = isBuySignal,
            isBullTrend = isBullTrend,
            stopLossLevel = stopLossLevel,
            distanceToStopPct = distancePct,
            isSuperBullCandle = lastBar.isSuperBull,
            isBullAboveAvg = lastBar.isBullAboveAvg,
            bodyRange = lastBar.bodyRange,
            avgBody = lastBar.avgBody,
            rsi = rsi,
            bars = enrichedBars
        )
    }

    private fun calculateRSI(closes: List<Double>, period: Int = 14): Double {
        if (closes.size <= period) return 50.0
        var gains = 0.0
        var losses = 0.0

        for (i in 1..period) {
            val change = closes[i] - closes[i - 1]
            if (change > 0) gains += change else losses += -change
        }

        var avgGain = gains / period
        var avgLoss = losses / period

        for (i in (period + 1) until closes.size) {
            val change = closes[i] - closes[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) -change else 0.0
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    /**
     * Generates a coherent price series and candlesticks for any stock & timeframe.
     * Uses deterministic seed based on ticker + timeframe so data is stable yet dynamic.
     */
    fun generateBars(stock: StockItem, timeframe: Timeframe, barCount: Int = 45): List<Bar> {
        val seed = stock.ticker.hashCode().toLong() xor (timeframe.minutes.toLong() * 31337L)
        val rng = Random(seed)

        val bars = mutableListOf<Bar>()
        var currentClose = stock.basePrice

        // Timeframe factor: 5dk has smaller candle range than 1g
        val tfVolatility = when (timeframe) {
            Timeframe.M5 -> 0.003
            Timeframe.M15 -> 0.005
            Timeframe.H1 -> 0.009
            Timeframe.H4 -> 0.016
            Timeframe.D1 -> 0.025
            Timeframe.W1 -> 0.045
        }

        val baseStepTime = timeframe.minutes * 60 * 1000L
        val now = System.currentTimeMillis()
        val startTime = now - (barCount * baseStepTime)

        // Give a cyclical trend so certain stocks produce Buy crossovers and Solid candles
        val cycleFreq = 0.15 + (rng.nextDouble() * 0.1)
        val phaseShift = rng.nextDouble() * Math.PI * 2.0

        val rawPricePoints = mutableListOf<Double>()
        var walkPrice = stock.basePrice

        for (i in 0 until barCount) {
            val trendFactor = kotlin.math.sin(i * cycleFreq + phaseShift) * 0.008
            val randomStep = (rng.nextDouble() - 0.49) * tfVolatility * 1.5
            val pctDelta = trendFactor + randomStep
            walkPrice *= (1.0 + pctDelta)
            rawPricePoints.add(walkPrice)
        }

        // Scale factors so that the final bar is anchored to the true market price stock.basePrice
        val finalWalk = rawPricePoints.last().coerceAtLeast(0.0001)
        val scaleFactor = stock.basePrice / finalWalk

        for (i in 0 until barCount) {
            val barTime = startTime + (i * baseStepTime)
            val scaledClose = roundToDecimals(rawPricePoints[i] * scaleFactor, stock.basePrice)
            val prevClose = if (i == 0) scaledClose * (1.0 - (rng.nextDouble() - 0.5) * tfVolatility)
                            else roundToDecimals(rawPricePoints[i - 1] * scaleFactor, stock.basePrice)

            val open = prevClose
            val close = scaledClose

            val highDelta = rng.nextDouble() * tfVolatility * close
            val lowDelta = rng.nextDouble() * tfVolatility * close
            val high = roundToDecimals(max(open, close) + highDelta, stock.basePrice)
            val low = roundToDecimals(max(0.01, min(open, close) - lowDelta), stock.basePrice)

            val baseVol = if (stock.basePrice > 100) 250_000L else 2_500_000L
            val volMultiplier = 0.5 + rng.nextDouble() + (if (abs(close - open) / close > tfVolatility) 1.5 else 0.0)
            val volume = (baseVol * volMultiplier).toLong()

            bars.add(
                Bar(
                    timestamp = barTime,
                    open = open,
                    high = max(high, max(open, close)),
                    low = min(low, min(open, close)),
                    close = close,
                    volume = volume
                )
            )
        }

        return bars
    }

    private fun roundToDecimals(value: Double, basePrice: Double): Double {
        return when {
            basePrice < 0.01 -> Math.round(value * 1_000_000.0) / 1_000_000.0
            basePrice < 1.0 -> Math.round(value * 10_000.0) / 10_000.0
            basePrice < 100.0 -> Math.round(value * 100.0) / 100.0
            else -> Math.round(value * 10.0) / 10.0
        }
    }

    /**
     * Scans symbols and filters for AL signals according to Pine Script criteria.
     */
    fun scanSymbols(
        symbols: List<StockItem>,
        timeframe: Timeframe,
        onlyFreshCrossover: Boolean = false,
        onlySuperBull: Boolean = false,
        onlyPositiveChange: Boolean = false
    ): List<ScanResult> {
        val results = mutableListOf<ScanResult>()

        for (stock in symbols) {
            val rawBars = generateBars(stock, timeframe)
            val indicator = calculateIndicators(rawBars)
            val lastBar = indicator.bars.last()
            val firstBarOfPeriod = indicator.bars.first()
            val changePercent = ((lastBar.close - firstBarOfPeriod.close) / firstBarOfPeriod.close) * 100.0

            // Sinyal Belirleme:
            // 1. FRESH_BUY: Pine Script crossover(Trail1, Trail2)
            // 2. SUPER_BULL_BUY: Trail1 > Trail2 and Super_BULL solid candle
            // 3. BULL_TREND: Trail1 > Trail2
            val isBull = indicator.isBullTrend
            val isCrossover = indicator.isBuyCrossover
            val isSuper = indicator.isSuperBullCandle

            if (!isBull && !isCrossover) {
                continue // Pine script AL taraması sadece AL durumundakileri listeler
            }

            val signalType = when {
                isCrossover -> SignalType.FRESH_BUY
                isSuper -> SignalType.SUPER_BULL_BUY
                isBull -> SignalType.BULL_TREND
                else -> SignalType.NEUTRAL
            }

            // Apply filter options if requested
            if (onlyFreshCrossover && !isCrossover) continue
            if (onlySuperBull && !isSuper) continue
            if (onlyPositiveChange && changePercent < 0) continue

            // Determine bars since buy
            val barsSinceBuy = indicator.bars.reversed().indexOfFirst { it.isBuyCrossover }
                .let { if (it >= 0) it else 5 }

            results.add(
                ScanResult(
                    stock = stock,
                    lastPrice = lastBar.close,
                    changePercent = changePercent,
                    volume = lastBar.volume,
                    timeframe = timeframe,
                    indicator = indicator,
                    signalType = signalType,
                    barsSinceBuy = barsSinceBuy
                )
            )
        }

        // Sort priority: FRESH_BUY first, then SUPER_BULL_BUY, then highest changePercent
        return results.sortedWith(
            compareByDescending<ScanResult> { it.signalType == SignalType.FRESH_BUY }
                .thenByDescending { it.signalType == SignalType.SUPER_BULL_BUY }
                .thenByDescending { it.changePercent }
        )
    }
}
