package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScanResult
import com.example.model.SignalType
import com.example.model.StockItem
import com.example.model.Timeframe
import com.example.ui.components.RadarPulseAnimation
import com.example.ui.components.StockCard
import com.example.ui.components.TimeframeSelector
import com.example.ui.theme.RadarBackground
import com.example.ui.theme.RadarBorder
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarGold
import com.example.ui.theme.RadarSurface
import com.example.ui.theme.RadarSurfaceVariant
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SuperBullColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarScanScreen(
    scanResults: List<ScanResult>,
    selectedTimeframe: Timeframe,
    selectedGroupFilter: String,
    filterFreshBuyOnly: Boolean,
    filterSuperBullOnly: Boolean,
    filterPositiveOnly: Boolean,
    searchQuery: String,
    isScanning: Boolean,
    favorites: Set<String>,
    totalScanned: Int,
    buySignalsCount: Int,
    onTimeframeSelected: (Timeframe) -> Unit,
    onGroupSelected: (String) -> Unit,
    onToggleFreshBuyOnly: () -> Unit,
    onToggleSuperBullOnly: () -> Unit,
    onTogglePositiveOnly: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRefreshScan: () -> Unit,
    onStockClick: (StockItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterBar by remember { mutableStateOf(false) }

    // Filter results by search query
    val filteredResults = scanResults.filter { item ->
        searchQuery.isBlank() ||
                item.stock.ticker.contains(searchQuery.trim(), ignoreCase = true) ||
                item.stock.name.contains(searchQuery.trim(), ignoreCase = true)
    }

    val groups = listOf(
        "Tümü",
        "BIST 1-4",
        "BIST 5-8",
        "BIST 9-12",
        "BIST 13-16",
        "Kripto",
        "Endeksler"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RadarBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadarPulseAnimation(
                            isScanning = isScanning,
                            color = RadarCyan,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ilkhanate Radar",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SignalGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "MOST AL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SignalGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Empires VI • BIST & Kripto Radarı",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterBar = !showFilterBar },
                        modifier = Modifier.testTag("toggle_filters_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Filtreler",
                            tint = if (filterFreshBuyOnly || filterSuperBullOnly || filterPositiveOnly) RadarCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = onRefreshScan,
                        modifier = Modifier.testTag("scan_refresh_btn")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                color = RadarCyan,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Taramayı Yenile",
                                tint = RadarCyan
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RadarSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Timeframe Selector: 5dk, 15dk, 1s, 4s, 1g, 1h
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                TimeframeSelector(
                    selectedTimeframe = selectedTimeframe,
                    onTimeframeSelected = onTimeframeSelected
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarSurface)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = "Hisse, Kripto veya Endeks ara (örn: THYAO, SASA, DOGE)...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Ara",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Temizle",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = RadarSurfaceVariant,
                        unfocusedContainerColor = RadarSurfaceVariant,
                        focusedBorderColor = RadarCyan,
                        unfocusedBorderColor = RadarBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Group Filter Horizontal Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groups.forEach { grp ->
                    val isSelected = grp == selectedGroupFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGroupSelected(grp) },
                        label = {
                            Text(
                                text = grp,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = RadarSurfaceVariant,
                            labelColor = TextSecondary,
                            selectedContainerColor = RadarCyan.copy(alpha = 0.2f),
                            selectedLabelColor = RadarCyan
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) RadarCyan else RadarBorder
                        ),
                        modifier = Modifier.testTag("group_chip_$grp")
                    )
                }
            }

            // Expandable Filter Options (Yeni Kesişim, Süper Mum, Pozitif Değişim)
            AnimatedVisibility(visible = showFilterBar) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RadarSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fresh Buy toggle
                    FilterChip(
                        selected = filterFreshBuyOnly,
                        onClick = onToggleFreshBuyOnly,
                        leadingIcon = {
                            if (filterFreshBuyOnly) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        },
                        label = { Text("YENİ AL Kesişimi", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SignalGreen.copy(alpha = 0.25f),
                            selectedLabelColor = SignalGreen
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterFreshBuyOnly,
                            borderColor = if (filterFreshBuyOnly) SignalGreen else RadarBorder
                        )
                    )

                    // Super Bull toggle
                    FilterChip(
                        selected = filterSuperBullOnly,
                        onClick = onToggleSuperBullOnly,
                        leadingIcon = {
                            if (filterSuperBullOnly) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        },
                        label = { Text("Süper Mum", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuperBullColor.copy(alpha = 0.25f),
                            selectedLabelColor = SuperBullColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterSuperBullOnly,
                            borderColor = if (filterSuperBullOnly) SuperBullColor else RadarBorder
                        )
                    )

                    // Positive change toggle
                    FilterChip(
                        selected = filterPositiveOnly,
                        onClick = onTogglePositiveOnly,
                        leadingIcon = {
                            if (filterPositiveOnly) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        },
                        label = { Text("Pozitif %", fontSize = 11.sp) }
                    )
                }
            }

            // Statistics Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalScanned Hisse/Varlık • ${filteredResults.size} AL Sinyali",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isScanning) RadarCyan else SignalGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isScanning) "Taranıyor..." else "Tarama Tamamlandı",
                        fontSize = 11.sp,
                        color = if (isScanning) RadarCyan else SignalGreen
                    )
                }
            }

            // Results List
            if (filteredResults.isEmpty() && !isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Radar,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Seçili kriterlerde AL sinyali bulunamadı",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Filtreleri gevşetebilir veya periyodu değiştirebilirsiniz.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("scan_results_list"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredResults, key = { it.stock.ticker }) { item ->
                        StockCard(
                            result = item,
                            isFavorite = favorites.contains(item.stock.ticker),
                            onCardClick = { onStockClick(item.stock) },
                            onToggleFavorite = { onToggleFavorite(item.stock.ticker) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
