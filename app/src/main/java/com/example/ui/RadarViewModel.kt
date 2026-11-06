package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarketApiRepository
import com.example.data.MarketEngine
import com.example.data.SymbolsRepository
import com.example.data.TechnicalAnalysisGenerator
import com.example.model.AnalysisReport
import com.example.model.Bar
import com.example.model.IndicatorResult
import com.example.model.ScanResult
import com.example.model.StockItem
import com.example.model.Timeframe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RadarUiState(
    val selectedTimeframe: Timeframe = Timeframe.D1,
    val selectedGroupFilter: String = "Tümü",
    val filterFreshBuyOnly: Boolean = false,
    val filterSuperBullOnly: Boolean = false,
    val filterPositiveOnly: Boolean = false,
    val searchQuery: String = "",
    val isScanning: Boolean = false,
    val scanResults: List<ScanResult> = emptyList(),
    val totalScannedCount: Int = 0,
    val buySignalsCount: Int = 0,
    val favorites: Set<String> = setOf("THYAO", "ASELS", "BIMAS", "BTCIM", "ADAUSDT", "XU100"),
    val activeTab: Int = 0, // 0: Radar Tarama, 1: Favorilerim, 2: İndikatör Rehberi

    // Detail Screen State
    val selectedStock: StockItem? = null,
    val detailTimeframe: Timeframe = Timeframe.D1,
    val selectedStockBars: List<Bar> = emptyList(),
    val selectedStockIndicator: IndicatorResult? = null,
    val selectedStockReport: AnalysisReport? = null,
    val isDetailLoading: Boolean = false
)

class RadarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    init {
        performScan()
    }

    fun changeTimeframe(tf: Timeframe) {
        _uiState.update { it.copy(selectedTimeframe = tf, detailTimeframe = tf) }
        performScan()
        // If viewing detail, update detail too
        _uiState.value.selectedStock?.let { loadStockDetail(it, tf) }
    }

    fun changeGroupFilter(group: String) {
        _uiState.update { it.copy(selectedGroupFilter = group) }
        performScan()
    }

    fun toggleFreshBuyOnly() {
        _uiState.update { it.copy(filterFreshBuyOnly = !it.filterFreshBuyOnly) }
        performScan()
    }

    fun toggleSuperBullOnly() {
        _uiState.update { it.copy(filterSuperBullOnly = !it.filterSuperBullOnly) }
        performScan()
    }

    fun togglePositiveOnly() {
        _uiState.update { it.copy(filterPositiveOnly = !it.filterPositiveOnly) }
        performScan()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setActiveTab(tab: Int) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun toggleFavorite(ticker: String) {
        _uiState.update { state ->
            val newFavs = if (state.favorites.contains(ticker)) {
                state.favorites - ticker
            } else {
                state.favorites + ticker
            }
            state.copy(favorites = newFavs)
        }
    }

    fun selectStock(stock: StockItem) {
        _uiState.update { it.copy(selectedStock = stock, isDetailLoading = true) }
        loadStockDetail(stock, _uiState.value.selectedTimeframe)
    }

    fun setDetailTimeframe(tf: Timeframe) {
        _uiState.update { it.copy(detailTimeframe = tf) }
        _uiState.value.selectedStock?.let { loadStockDetail(it, tf) }
    }

    fun clearSelectedStock() {
        _uiState.update { it.copy(selectedStock = null, selectedStockReport = null) }
    }

    fun refreshScan() {
        performScan()
    }

    private fun loadStockDetail(stock: StockItem, timeframe: Timeframe) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetailLoading = true) }
            val (bars, indicator, report) = withContext(Dispatchers.Default) {
                // Fetch real live market candles from Yahoo Finance (.IS) or Binance
                val realBars = MarketApiRepository.fetchRealBars(stock, timeframe)
                val rawBars = if (!realBars.isNullOrEmpty()) realBars else MarketEngine.generateBars(stock, timeframe, barCount = 55)
                val ind = MarketEngine.calculateIndicators(rawBars)
                val lastPrice = rawBars.last().close
                val changePct = ((lastPrice - rawBars.first().close) / rawBars.first().close) * 100.0
                val rep = TechnicalAnalysisGenerator.generateAnalysis(stock, timeframe, ind, lastPrice, changePct)
                Triple(ind.bars, ind, rep)
            }
            _uiState.update {
                it.copy(
                    selectedStockBars = bars,
                    selectedStockIndicator = indicator,
                    selectedStockReport = report,
                    isDetailLoading = false
                )
            }
        }
    }

    private fun performScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }

            val currentState = _uiState.value
            val timeframe = currentState.selectedTimeframe
            val groupFilter = currentState.selectedGroupFilter

            val filteredSymbols = withContext(Dispatchers.Default) {
                val all = SymbolsRepository.allSymbols
                when (groupFilter) {
                    "Tümü" -> all
                    "Kripto" -> all.filter { it.group == "Kripto" }
                    "Endeksler" -> all.filter { it.group == "Endeksler" }
                    "BIST 1-4" -> all.filter { it.group in listOf("Grup 1", "Grup 2", "Grup 3", "Grup 4") }
                    "BIST 5-8" -> all.filter { it.group in listOf("Grup 5", "Grup 6", "Grup 7", "Grup 8") }
                    "BIST 9-12" -> all.filter { it.group in listOf("Grup 9", "Grup 10", "Grup 11", "Grup 12") }
                    "BIST 13-16" -> all.filter { it.group in listOf("Grup 13", "Grup 14", "Grup 15", "Grup 16") }
                    else -> all
                }
            }

            val results = withContext(Dispatchers.Default) {
                MarketEngine.scanSymbols(
                    symbols = filteredSymbols,
                    timeframe = timeframe,
                    onlyFreshCrossover = currentState.filterFreshBuyOnly,
                    onlySuperBull = currentState.filterSuperBullOnly,
                    onlyPositiveChange = currentState.filterPositiveOnly
                )
            }

            _uiState.update {
                it.copy(
                    scanResults = results,
                    totalScannedCount = filteredSymbols.size,
                    buySignalsCount = results.size,
                    isScanning = false
                )
            }
        }
    }
}
