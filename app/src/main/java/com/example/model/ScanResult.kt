package com.example.model

data class IndicatorResult(
    val trail1: Double,
    val trail2: Double,
    val isBuyCrossover: Boolean,
    val isBullTrend: Boolean, // Trail1 > Trail2
    val stopLossLevel: Double,
    val distanceToStopPct: Double,
    val isSuperBullCandle: Boolean,
    val isBullAboveAvg: Boolean,
    val bodyRange: Double,
    val avgBody: Double,
    val rsi: Double,
    val bars: List<Bar>
)

enum class SignalType(val label: String, val badgeColorHex: Long) {
    FRESH_BUY("YENİ AL", 0xFF00E676),
    SUPER_BULL_BUY("GÜÇLÜ MUM AL", 0xFF9DFF00),
    BULL_TREND("AL TRENDİNDE", 0xFF00B0FF),
    NEUTRAL("NÖTR", 0xFF64748B)
}

data class ScanResult(
    val stock: StockItem,
    val lastPrice: Double,
    val changePercent: Double,
    val volume: Long,
    val timeframe: Timeframe,
    val indicator: IndicatorResult,
    val signalType: SignalType,
    val barsSinceBuy: Int = 0
)
