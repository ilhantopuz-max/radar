package com.example.model

data class StockItem(
    val ticker: String,
    val fullSymbol: String,
    val name: String,
    val group: String,
    val sector: String,
    val basePrice: Double,
    val currency: String = if (fullSymbol.startsWith("BINANCE")) "$" else "₺",
    val isFavorite: Boolean = false
)
