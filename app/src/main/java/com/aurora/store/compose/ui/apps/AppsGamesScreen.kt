/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.aurora.gplayapi.data.models.Category
import com.aurora.gplayapi.helpers.contracts.StreamContract
import com.aurora.gplayapi.helpers.contracts.TopChartsContract
import com.aurora.store.R
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.util.Preferences
import com.aurora.store.viewmodel.category.CategoryViewModel
import com.aurora.store.viewmodel.homestream.StreamViewModel
import com.aurora.store.viewmodel.topchart.TopChartViewModel
import kotlinx.coroutines.launch

@Composable
fun AppsGamesScreen(
    section: StoreSection,
    streamViewModel: StreamViewModel = hiltViewModel(key = "stream_${section.pageType}"),
    topChartViewModel: TopChartViewModel = hiltViewModel(key = "topChart_${section.pageType}"),
    categoryViewModel: CategoryViewModel = hiltViewModel(key = "category_${section.pageType}"),
    onNavigateTo: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val isForYouEnabled = Preferences.getBoolean(context, Preferences.PREFERENCE_FOR_YOU)

    val tabs = buildList {
        section.tabs.forEach { tab ->
            if (tab == StoreTab.ForYou && !isForYouEnabled) return@forEach
            add(tab)
        }
    }

    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (section != StoreSection.BOOKS) {
            topChartViewModel.getStreamCluster(
                section.topChartType,
                TopChartsContract.Chart.TOP_SELLING_FREE
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            edgePadding = dimensionResource(R.dimen.spacing_small)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(stringResource(tab.titleRes)) }
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false
        ) { page ->
            when (val tab = tabs[page]) {
                StoreTab.ForYou -> ForYouContent(
                    section = section,
                    viewModel = streamViewModel,
                    streamType = StreamContract.Type.HOME,
                    onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) },
                    onHeaderClick = { onNavigateTo(Destination.StreamBrowse(it)) },
                    onClusterScrolled = { cluster ->
                        streamViewModel.observeCluster(section.streamCategory, cluster)
                    },
                    onScrolledToEnd = {
                        streamViewModel.observe(section.streamCategory, StreamContract.Type.HOME)
                    }
                )

                StoreTab.TopCharts -> TopChartsContent(
                    section = section,
                    viewModel = topChartViewModel,
                    onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) }
                )

                StoreTab.Kids -> CategoriesContent(
                    section = section,
                    categoryType = Category.Type.FAMILY,
                    viewModel = categoryViewModel,
                    onCategoryClick = { onNavigateTo(Destination.CategoryBrowse(it)) }
                )

                StoreTab.Paid -> ForYouContent(
                    section = section,
                    viewModel = streamViewModel,
                    streamType = StreamContract.Type.PREMIUM_GAMES,
                    onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) },
                    onHeaderClick = { onNavigateTo(Destination.StreamBrowse(it)) },
                    onClusterScrolled = { cluster ->
                        streamViewModel.observeCluster(section.streamCategory, cluster)
                    },
                    onScrolledToEnd = {
                        streamViewModel.observe(
                            section.streamCategory,
                            StreamContract.Type.PREMIUM_GAMES
                        )
                    }
                )

                StoreTab.Types -> CategoriesContent(
                    section = section,
                    viewModel = categoryViewModel,
                    onCategoryClick = { onNavigateTo(Destination.CategoryBrowse(it)) }
                )

                StoreTab.Genres -> CategoriesContent(
                    section = section,
                    viewModel = categoryViewModel,
                    onCategoryClick = { onNavigateTo(Destination.CategoryBrowse(it)) }
                )

                StoreTab.Ebooks,
                StoreTab.Audiobooks -> ForYouContent(
                    section = section,
                    viewModel = streamViewModel,
                    streamType = tab.streamType(section),
                    onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) },
                    onHeaderClick = { onNavigateTo(Destination.StreamBrowse(it)) },
                    onClusterScrolled = { cluster ->
                        streamViewModel.observeCluster(section.streamCategory, cluster)
                    },
                    onScrolledToEnd = {
                        streamViewModel.observe(section.streamCategory, tab.streamType(section))
                    }
                )

                StoreTab.Bestsellers,
                StoreTab.NewReleases,
                StoreTab.PopularFree -> SingleChartListContent(
                    section = section,
                    tab = tab,
                    viewModel = topChartViewModel,
                    onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) }
                )
            }
        }
    }
}
