package com.example.model

enum class Timeframe(
    val code: String,
    val displayName: String,
    val description: String,
    val minutes: Int
) {
    M5("5dk", "5dk", "5 Dakika", 5),
    M15("15dk", "15dk", "15 Dakika", 15),
    H1("1s", "1s", "1 Saat", 60),
    H4("4s", "4s", "4 Saat", 240),
    D1("1g", "1g", "1 Gün", 1440),
    W1("1h", "1h", "1 Hafta", 10080);

    companion object {
        fun fromCode(code: String): Timeframe {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: D1
        }
    }
}
