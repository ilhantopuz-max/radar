package com.example.data

import android.util.Log
import com.example.model.Bar
import com.example.model.StockItem
import com.example.model.Timeframe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

object MarketApiRepository {

    private const val TAG = "MarketApiRepository"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    // In-memory cache for fetched live bars: Key = "TICKER_TIMEFRAME"
    private val barsCache = mutableMapOf<String, Pair<Long, List<Bar>>>()
    private const val CACHE_EXPIRY_MS = 30_000L // 30 seconds

    suspend fun fetchRealBars(stock: StockItem, timeframe: Timeframe): List<Bar>? = withContext(Dispatchers.IO) {
        val cacheKey = "${stock.ticker}_${timeframe.code}"
        val cached = barsCache[cacheKey]
        val now = System.currentTimeMillis()
        if (cached != null && (now - cached.first) < CACHE_EXPIRY_MS) {
            return@withContext cached.second
        }

        try {
            val bars = when {
                stock.group == "Kripto" || stock.fullSymbol.startsWith("BINANCE") -> {
                    fetchBinanceBars(stock.ticker, timeframe)
                }
                else -> {
                    // BIST hisseleri ve endeksleri Yahoo Finance'ta .IS uzantısını kullanır.
                    fetchYahooFinanceBars("${stock.ticker}.IS", timeframe)
                }
            }

            if (!bars.isNullOrEmpty()) {
                barsCache[cacheKey] = Pair(now, bars)
                return@withContext bars
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch real market data for ${stock.ticker}: ${e.message}")
        }
        null
    }

    /**
     * Yahoo Finance interval/range değerleri seçilen grafiğe göre belirlenir.
     * Yahoo 4 saatlik mum sağlamadığı için H4 verisi 1 saatlik mumlardan birleştirilir.
     */
    private fun fetchYahooFinanceBars(symbol: String, timeframe: Timeframe): List<Bar>? {
        val (interval, range) = when (timeframe) {
            Timeframe.M5 -> "5m" to "5d"
            Timeframe.M15 -> "15m" to "5d"
            Timeframe.H1 -> "1h" to "30d"
            Timeframe.H4 -> "1h" to "30d" // Yahoo'da native 4h interval yok
            Timeframe.D1 -> "1d" to "1y"
            Timeframe.W1 -> "1wk" to "5y"
        }
        val url = "https://query1.finance.yahoo.com/v8/finance/chart/$symbol?interval=$interval&range=$range"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .header("Accept", "application/json")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null

            val json = JSONObject(body)
            val chart = json.optJSONObject("chart") ?: return null
            val resultArr = chart.optJSONArray("result") ?: return null
            if (resultArr.length() == 0) return null

            val firstResult = resultArr.getJSONObject(0)
            val timestamps = firstResult.optJSONArray("timestamp") ?: return null
            val indicators = firstResult.optJSONObject("indicators") ?: return null
            val quoteArr = indicators.optJSONArray("quote") ?: return null
            if (quoteArr.length() == 0) return null

            val quote = quoteArr.getJSONObject(0)
            val opens = quote.optJSONArray("open") ?: return null
            val highs = quote.optJSONArray("high") ?: return null
            val lows = quote.optJSONArray("low") ?: return null
            val closes = quote.optJSONArray("close") ?: return null
            val volumes = quote.optJSONArray("volume")

            val bars = mutableListOf<Bar>()
            for (i in 0 until timestamps.length()) {
                val t = timestamps.optLong(i) * 1000L
                val o = opens.optDouble(i)
                val h = highs.optDouble(i)
                val l = lows.optDouble(i)
                val c = closes.optDouble(i)
                val v = volumes?.optLong(i) ?: 10000L

                // Yahoo Finance boş seans aralıklarını JSON null olarak döndürebilir.
                if (c.isNaN() || o.isNaN() || h.isNaN() || l.isNaN() || c <= 0.0) {
                    continue
                }

                bars.add(
                    Bar(
                        timestamp = t,
                        open = roundDouble(o),
                        high = roundDouble(max(h, max(o, c))),
                        low = roundDouble(min(l, min(o, c))),
                        close = roundDouble(c),
                        volume = max(1L, v)
                    )
                )
            }

            val timeframeBars = if (timeframe == Timeframe.H4) {
                aggregateBistHourlyBarsToFourHours(bars)
            } else {
                bars
            }

            return if (timeframeBars.size >= 5) timeframeBars.takeLast(50) else null
        }
    }

    /**
     * Bir saatlik OHLC mumlarını BIST seansı başlangıcına (10:00 TRT) göre dört saatlik
     * gruplar. Her çıktı mumunda açılış ilk, kapanış son mumdan; high/low uç değerlerden,
     * hacim ise saatlik hacimlerin toplamından alınır.
     */
    private fun aggregateBistHourlyBarsToFourHours(hourlyBars: List<Bar>): List<Bar> {
        val bucketDurationMs = Timeframe.H4.minutes * 60_000L
        val dayDurationMs = 24L * 60L * 60L * 1000L
        val bistUtcOffsetMs = 3L * 60L * 60L * 1000L
        val sessionStartMs = 10L * 60L * 60L * 1000L

        val grouped = hourlyBars.groupBy { bar ->
            val localTimestamp = bar.timestamp + bistUtcOffsetMs
            val localDay = Math.floorDiv(localTimestamp, dayDurationMs)
            val timeOfDay = Math.floorMod(localTimestamp, dayDurationMs)
            val sessionSlot = Math.floorDiv(timeOfDay - sessionStartMs, bucketDurationMs)
            localDay to sessionSlot
        }

        return grouped.entries
            .sortedWith(compareBy<Map.Entry<Pair<Long, Long>, List<Bar>>> { it.key.first }.thenBy { it.key.second })
            .map { (_, bars) ->
                val orderedBars = bars.sortedBy { it.timestamp }
                val first = orderedBars.first()
                val last = orderedBars.last()
                Bar(
                    timestamp = first.timestamp,
                    open = first.open,
                    high = orderedBars.maxOf { it.high },
                    low = orderedBars.minOf { it.low },
                    close = last.close,
                    volume = orderedBars.sumOf { it.volume }
                )
            }
    }

    private fun fetchBinanceBars(pair: String, timeframe: Timeframe): List<Bar>? {
        val interval = when (timeframe) {
            Timeframe.M5 -> "5m"
            Timeframe.M15 -> "15m"
            Timeframe.H1 -> "1h"
            Timeframe.H4 -> "4h"
            Timeframe.D1 -> "1d"
            Timeframe.W1 -> "1w"
        }
        val url = "https://api.binance.com/api/v3/klines?symbol=$pair&interval=$interval&limit=50"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null

            val klines = org.json.JSONArray(body)
            val bars = mutableListOf<Bar>()

            for (i in 0 until klines.length()) {
                val item = klines.getJSONArray(i)
                val t = item.getLong(0)
                val o = item.getString(1).toDoubleOrNull() ?: 0.0
                val h = item.getString(2).toDoubleOrNull() ?: 0.0
                val l = item.getString(3).toDoubleOrNull() ?: 0.0
                val c = item.getString(4).toDoubleOrNull() ?: 0.0
                val v = item.getString(5).toDoubleOrNull()?.toLong() ?: 1000L

                if (c > 0.0) {
                    bars.add(
                        Bar(
                            timestamp = t,
                            open = o,
                            high = h,
                            low = l,
                            close = c,
                            volume = v
                        )
                    )
                }
            }

            return if (bars.isNotEmpty()) bars else null
        }
    }

    private fun roundDouble(v: Double): Double {
        return Math.round(v * 100.0) / 100.0
    }
}
