/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.store.MainViewModel
import com.aurora.store.R
import com.aurora.store.compose.composable.PlayStoreTopBar
import com.aurora.store.compose.composition.LocalNetworkStatus
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.ui.apps.AppsGamesScreen
import com.aurora.store.compose.ui.apps.StoreSection
import com.aurora.store.compose.ui.commons.MoreSheet
import com.aurora.store.compose.ui.commons.NetworkScreen
import com.aurora.store.compose.ui.installed.InstalledScreen
import com.aurora.store.compose.ui.search.SearchScreen
import com.aurora.store.compose.ui.sheets.AppUpdateSheet
import com.aurora.store.data.model.NetworkStatus
import com.aurora.store.data.room.update.Update
import kotlinx.coroutines.launch

private const val PAGER_PAGE_COUNT = 5

/** Busts old saveable pager keys that restored Search (page 2) after cold start. */
private const val MAIN_PAGER_SAVEABLE_KEY = "aurora_main_bottom_nav_pager_v5"

/** Maps settings default-tab preference (games / apps / updates) to [MainPagerPage] index. */
internal fun defaultTabPreferenceToPagerPage(preferenceIndex: Int): Int = when (preferenceIndex) {
    0 -> MainPagerPage.GAMES.ordinal
    1 -> MainPagerPage.APPS.ordinal
    2 -> MainPagerPage.UPDATES.ordinal
    else -> MainPagerPage.GAMES.ordinal
}

private enum class MainPagerPage {
    GAMES,
    APPS,
    SEARCH,
    UPDATES,
    YOU
}

private enum class MainNavItem(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val selectedIconRes: Int,
    val pagerPage: Int,
    val showUpdateBadge: Boolean = false
) {
    GAMES(
        labelRes = R.string.title_games,
        iconRes = R.drawable.ic_play_nav_games,
        selectedIconRes = R.drawable.ic_play_nav_games_selected,
        pagerPage = 0
    ),
    APPS(
        labelRes = R.string.title_apps,
        iconRes = R.drawable.ic_play_nav_apps,
        selectedIconRes = R.drawable.ic_play_nav_apps_selected,
        pagerPage = 1
    ),
    SEARCH(
        labelRes = R.string.action_search,
        iconRes = R.drawable.ic_play_nav_search,
        selectedIconRes = R.drawable.ic_play_nav_search,
        pagerPage = 2
    ),
    UPDATES(
        labelRes = R.string.title_nav_books,
        iconRes = R.drawable.ic_play_nav_books,
        selectedIconRes = R.drawable.ic_play_nav_books_selected,
        pagerPage = 3,
        showUpdateBadge = true
    ),
    YOU(
        labelRes = R.string.title_nav_you,
        iconRes = R.drawable.ic_play_nav_you,
        selectedIconRes = R.drawable.ic_play_nav_you_selected,
        pagerPage = 4
    )
}

@Composable
fun MainScreen(
    initialTab: Int = MainPagerPage.GAMES.ordinal,
    mainViewModel: MainViewModel = hiltViewModel(),
    onNavigateTo: (Destination) -> Unit = {}
) {
    key(MAIN_PAGER_SAVEABLE_KEY, initialTab) {
        MainScreenBody(
            initialTab = initialTab,
            mainViewModel = mainViewModel,
            onNavigateTo = onNavigateTo
        )
    }
}

@Composable
private fun MainScreenBody(
    initialTab: Int,
    mainViewModel: MainViewModel,
    onNavigateTo: (Destination) -> Unit
) {
    val networkStatus = LocalNetworkStatus.current
    val updates by mainViewModel.updateHelper.updates.collectAsStateWithLifecycle(
        initialValue = null
    )
    val updateCount = updates?.size ?: 0

    val coroutineScope = rememberCoroutineScope()
    val resolvedInitialTab = initialTab.coerceIn(0, PAGER_PAGE_COUNT - 1)
    val pagerState = rememberPagerState(
        initialPage = resolvedInitialTab,
        pageCount = { PAGER_PAGE_COUNT }
    )

    // Saveable can apply the previous session page (Search) one frame after open — fix after layout.
    LaunchedEffect(resolvedInitialTab) {
        withFrameNanos {}
        withFrameNanos {}
        if (pagerState.currentPage != resolvedInitialTab) {
            pagerState.scrollToPage(resolvedInitialTab)
        }
    }

    var showMoreSheet by remember { mutableStateOf(false) }
    var appUpdateTarget by remember { mutableStateOf<Update?>(null) }

    val onSurface = MaterialTheme.colorScheme.onSurface
    val navBarColors = NavigationBarItemDefaults.colors(
        selectedIconColor = colorResource(R.color.play_nav_selected),
        selectedTextColor = colorResource(R.color.play_nav_selected),
        indicatorColor = colorResource(R.color.play_nav_indicator),
        unselectedIconColor = onSurface,
        unselectedTextColor = onSurface
    )

    fun handleNavigation(destination: Destination) {
        when (destination) {
            is Destination.AppUpdate -> appUpdateTarget = destination.update
            else -> onNavigateTo(destination)
        }
    }

    if (networkStatus == NetworkStatus.UNAVAILABLE) {
        NetworkScreen()
        return
    }

    if (showMoreSheet) {
        MoreSheet(
            onDismiss = { showMoreSheet = false },
            onNavigateTo = { destination ->
                showMoreSheet = false
                onNavigateTo(destination)
            }
        )
    }

    appUpdateTarget?.let { app ->
        AppUpdateSheet(
            update = app,
            onDismiss = { appUpdateTarget = null },
            onNavigateTo = { destination ->
                appUpdateTarget = null
                onNavigateTo(destination)
            }
        )
    }

    val currentPage = MainPagerPage.entries[pagerState.currentPage]
    val showMainTopBar = currentPage == MainPagerPage.GAMES ||
        currentPage == MainPagerPage.APPS ||
        currentPage == MainPagerPage.UPDATES

    Scaffold(
        topBar = {
            if (showMainTopBar) {
                PlayStoreTopBar(
                    onNotificationClick = { onNavigateTo(Destination.Updates) },
                    onAvatarClick = { showMoreSheet = true }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.play_nav_bar_background)
            ) {
                MainNavItem.entries.forEach { item ->
                    val selected = pagerState.currentPage == item.pagerPage
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(item.pagerPage)
                            }
                        },
                        icon = {
                            val iconPainter = painterResource(
                                if (selected) item.selectedIconRes else item.iconRes
                            )
                            if (item.showUpdateBadge && updateCount > 0) {
                                BadgedBox(badge = { Badge { Text("$updateCount") } }) {
                                    Icon(
                                        painter = iconPainter,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                Icon(
                                    painter = iconPainter,
                                    contentDescription = null
                                )
                            }
                        },
                        label = { Text(stringResource(item.labelRes)) },
                        colors = navBarColors
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                beyondViewportPageCount = PAGER_PAGE_COUNT - 1,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (MainPagerPage.entries[page]) {
                    MainPagerPage.GAMES -> AppsGamesScreen(
                        section = StoreSection.GAMES,
                        onNavigateTo = ::handleNavigation
                    )
                    MainPagerPage.APPS -> AppsGamesScreen(
                        section = StoreSection.APPS,
                        onNavigateTo = onNavigateTo
                    )
                    MainPagerPage.SEARCH -> SearchScreen(
                        embeddedInMain = true,
                        onNavigateTo = onNavigateTo
                    )
                    MainPagerPage.UPDATES -> AppsGamesScreen(
                        section = StoreSection.BOOKS,
                        onNavigateTo = onNavigateTo
                    )
                    MainPagerPage.YOU -> InstalledScreen(
                        onNavigateTo = onNavigateTo
                    )
                }
            }
        }
    }
}
