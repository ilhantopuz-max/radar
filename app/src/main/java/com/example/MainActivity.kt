package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.RadarViewModel
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.RadarScanScreen
import com.example.ui.screens.StockDetailScreen
import com.example.ui.theme.IlkhanateRadarTheme
import com.example.ui.theme.RadarBackground
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarGold
import com.example.ui.theme.RadarSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: RadarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IlkhanateRadarTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.selectedStock != null) {
                        StockDetailScreen(
                            stock = state.selectedStock!!,
                            bars = state.selectedStockBars,
                            indicator = state.selectedStockIndicator,
                            report = state.selectedStockReport,
                            timeframe = state.detailTimeframe,
                            isLoading = state.isDetailLoading,
                            isFavorite = state.favorites.contains(state.selectedStock!!.ticker),
                            onBackClick = { viewModel.clearSelectedStock() },
                            onTimeframeChange = { viewModel.setDetailTimeframe(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(state.selectedStock!!.ticker) },
                            onRefreshAnalysis = { viewModel.setDetailTimeframe(state.detailTimeframe) }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = RadarBackground,
                            bottomBar = {
                                NavigationBar(
                                    containerColor = RadarSurface,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.testTag("bottom_nav_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = state.activeTab == 0,
                                        onClick = { viewModel.setActiveTab(0) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Radar,
                                                contentDescription = "Radar Tarama"
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = "Tarama",
                                                fontSize = 11.sp,
                                                fontWeight = if (state.activeTab == 0) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = RadarCyan,
                                            selectedTextColor = RadarCyan,
                                            indicatorColor = RadarCyan.copy(alpha = 0.2f),
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_tab_scan")
                                    )

                                    NavigationBarItem(
                                        selected = state.activeTab == 1,
                                        onClick = { viewModel.setActiveTab(1) },
                                        icon = {
                                            Icon(
                                                imageVector = if (state.activeTab == 1) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "Favorilerim"
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = "Favoriler (${state.favorites.size})",
                                                fontSize = 11.sp,
                                                fontWeight = if (state.activeTab == 1) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = RadarGold,
                                            selectedTextColor = RadarGold,
                                            indicatorColor = RadarGold.copy(alpha = 0.2f),
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_tab_favorites")
                                    )

                                    NavigationBarItem(
                                        selected = state.activeTab == 2,
                                        onClick = { viewModel.setActiveTab(2) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Rounded.MenuBook,
                                                contentDescription = "Strateji Rehberi"
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = "Rehber",
                                                fontSize = 11.sp,
                                                fontWeight = if (state.activeTab == 2) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = RadarCyan,
                                            selectedTextColor = RadarCyan,
                                            indicatorColor = RadarCyan.copy(alpha = 0.2f),
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_tab_guide")
                                    )
                                }
                            }
                        ) { innerPadding ->
                            AnimatedContent(
                                targetState = state.activeTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition",
                                modifier = Modifier.padding(innerPadding)
                            ) { tab ->
                                when (tab) {
                                    0 -> RadarScanScreen(
                                        scanResults = state.scanResults,
                                        selectedTimeframe = state.selectedTimeframe,
                                        selectedGroupFilter = state.selectedGroupFilter,
                                        filterFreshBuyOnly = state.filterFreshBuyOnly,
                                        filterSuperBullOnly = state.filterSuperBullOnly,
                                        filterPositiveOnly = state.filterPositiveOnly,
                                        searchQuery = state.searchQuery,
                                        isScanning = state.isScanning,
                                        favorites = state.favorites,
                                        totalScanned = state.totalScannedCount,
                                        buySignalsCount = state.buySignalsCount,
                                        onTimeframeSelected = { viewModel.changeTimeframe(it) },
                                        onGroupSelected = { viewModel.changeGroupFilter(it) },
                                        onToggleFreshBuyOnly = { viewModel.toggleFreshBuyOnly() },
                                        onToggleSuperBullOnly = { viewModel.toggleSuperBullOnly() },
                                        onTogglePositiveOnly = { viewModel.togglePositiveOnly() },
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                        onRefreshScan = { viewModel.refreshScan() },
                                        onStockClick = { viewModel.selectStock(it) },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                                    )

                                    1 -> FavoritesScreen(
                                        allResults = state.scanResults,
                                        favorites = state.favorites,
                                        selectedTimeframe = state.selectedTimeframe,
                                        onTimeframeSelected = { viewModel.changeTimeframe(it) },
                                        onStockClick = { viewModel.selectStock(it) },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                                    )

                                    2 -> GuideScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
