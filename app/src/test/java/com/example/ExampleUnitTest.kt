package com.example

import com.example.data.MarketEngine
import com.example.data.SymbolsRepository
import com.example.model.Bar
import com.example.model.Timeframe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPineScriptSymbolsCount() {
        val symbols = SymbolsRepository.allSymbols
        // Make sure we have 600+ symbols from Groups 1-16, Kripto, and Endeksler
        assertTrue("Expected over 600 symbols", symbols.size > 600)

        // Verify key BIST tickers exist
        assertNotNull(symbols.find { it.ticker == "THYAO" })
        assertNotNull(symbols.find { it.ticker == "SASA" })
        assertNotNull(symbols.find { it.ticker == "EREGL" })
        assertNotNull(symbols.find { it.ticker == "GARAN" })

        // Verify crypto pairs from script exist
        assertNotNull(symbols.find { it.ticker == "ADAUSDT" })
        assertNotNull(symbols.find { it.ticker == "DOGEUSDT" })
        assertNotNull(symbols.find { it.ticker == "PEPEUSDT" })

        // Verify index symbols exist
        assertNotNull(symbols.find { it.ticker == "XU100" })
        assertNotNull(symbols.find { it.ticker == "XBANK" })

        // Verify BRKSN price is 4.99
        val brksn = symbols.find { it.ticker == "BRKSN" }
        assertNotNull(brksn)
        assertEquals(4.99, brksn!!.basePrice, 0.01)

        val bars = MarketEngine.generateBars(brksn, Timeframe.D1, 30)
        assertEquals(4.99, bars.last().close, 0.15)
    }

    @Test
    fun testMostIndicatorCalculation() {
        // Construct synthetic bars simulating an uptrend crossover
        val rawBars = mutableListOf<Bar>()
        var price = 100.0
        val baseTime = 1700000000000L

        // 10 declining bars
        for (i in 0 until 10) {
            price -= 1.0
            rawBars.add(
                Bar(
                    timestamp = baseTime + (i * 60000L),
                    open = price + 0.5,
                    high = price + 1.0,
                    low = price - 0.5,
                    close = price,
                    volume = 10000L
                )
            )
        }

        // 10 sharply rising bars with solid bull candle
        for (i in 10 until 20) {
            val open = price
            price += 3.0 // Strong bull
            rawBars.add(
                Bar(
                    timestamp = baseTime + (i * 60000L),
                    open = open,
                    high = price + 0.5,
                    low = open - 0.2,
                    close = price,
                    volume = 50000L
                )
            )
        }

        val result = MarketEngine.calculateIndicators(rawBars)
        assertNotNull(result)
        assertTrue("Trail1 should be higher than Trail2 during strong uptrend", result.trail1 > result.trail2)
        assertTrue("Indicator should report bull trend", result.isBullTrend)
        assertTrue("Stop loss should be below current Trail1", result.stopLossLevel < result.trail1)
    }

    @Test
    fun testTimeframeEnumOptions() {
        val tfCodes = Timeframe.entries.map { it.code }
        assertTrue(tfCodes.contains("5dk"))
        assertTrue(tfCodes.contains("15dk"))
        assertTrue(tfCodes.contains("1s"))
        assertTrue(tfCodes.contains("4s"))
        assertTrue(tfCodes.contains("1g"))
        assertTrue(tfCodes.contains("1h"))
    }
}
