package com.example.model

data class KeyLevel(
    val title: String,
    val price: Double,
    val type: LevelType
)

enum class LevelType {
    STOP_LOSS,
    SUPPORT,
    PIVOT,
    TARGET_1,
    TARGET_2
}

data class AnalysisReport(
    val stockSymbol: String,
    val timeframe: Timeframe,
    val verdict: String, // e.g. "GÜÇLÜ AL SİNYALİ", "AL TRENDİ KORUNUYOR"
    val summaryCommentary: String,
    val mostAnalysis: String,
    val candleAnalysis: String,
    val momentumAnalysis: String,
    val strategyRecommendation: String,
    val keyLevels: List<KeyLevel>,
    val riskRewardRatio: String,
    val signalScore: Int // 0 to 100
)
