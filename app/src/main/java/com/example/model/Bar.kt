package com.example.model

data class Bar(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val trail1: Double = 0.0,
    val trail2: Double = 0.0,
    val bodyRange: Double = kotlin.math.abs(close - open),
    val avgBody: Double = 0.0,
    val isSuperBull: Boolean = false,
    val isSuperBear: Boolean = false,
    val isBullAboveAvg: Boolean = false,
    val isBuyCrossover: Boolean = false,
    val isSellCrossover: Boolean = false
)
