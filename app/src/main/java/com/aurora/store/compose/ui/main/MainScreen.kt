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
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.extensions.requiresObbDir
import com.aurora.store.MainViewModel
import com.aurora.store.R
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.composition.LocalNetworkStatus
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.ui.apps.AppsGamesScreen
import com.aurora.store.compose.ui.commons.MoreSheet
import com.aurora.store.compose.ui.commons.NetworkScreen
import com.aurora.store.compose.ui.sheets.AppUpdateSheet
import com.aurora.store.compose.ui.updates.UpdatesScreen
import com.aurora.store.data.model.NetworkStatus
import com.aurora.store.data.model.PermissionType
import com.aurora.store.data.providers.PermissionProvider.Companion.isGranted
import com.aurora.store.data.room.update.Update
import com.aurora.store.viewmodel.all.UpdatesViewModel
import kotlinx.coroutines.launch

private const val PAGER_PAGE_COUNT = 3

private enum class MainPagerPage {
    GAMES,
    APPS,
    UPDATES
}

private enum class MainNavItem(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val pagerPage: Int? = null,
    val destination: Destination? = null,
    val showUpdateBadge: Boolean = false
) {
    GAMES(
        labelRes = R.string.title_games,
        iconRes = R.drawable.ic_games,
        pagerPage = 0
    ),
    APPS(
        labelRes = R.string.title_apps,
        iconRes = R.drawable.ic_apps,
        pagerPage = 1
    ),
    SEARCH(
        labelRes = R.string.action_search,
        iconRes = R.drawable.ic_round_search,
        destination = Destination.Search
    ),
    UPDATES(
        labelRes = R.string.title_nav_books,
        iconRes = R.drawable.ic_nav_book,
        pagerPage = 2,
        showUpdateBadge = true
    ),
    YOU(
        labelRes = R.string.title_nav_you,
        iconRes = R.drawable.ic_account,
        destination = Destination.Installed
    )
}

@Composable
fun MainScreen(
    initialTab: Int = 1,
    mainViewModel: MainViewModel = hiltViewModel(),
    updatesViewModel: UpdatesViewModel = hiltViewModel(),
    onNavigateTo: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val networkStatus = LocalNetworkStatus.current
    val updates by mainViewModel.updateHelper.updates.collectAsStateWithLifecycle(
        initialValue = null
    )
    val updateCount = updates?.size ?: 0

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialTab.coerceIn(0, PAGER_PAGE_COUNT - 1)
    ) {
        PAGER_PAGE_COUNT
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(
                    when (MainPagerPage.entries[pagerState.currentPage]) {
                        MainPagerPage.GAMES -> R.string.title_games
                        MainPagerPage.APPS -> R.string.title_apps
                        MainPagerPage.UPDATES -> R.string.title_updates
                    }
                ),
                actions = {
                    IconButton(onClick = { onNavigateTo(Destination.Downloads) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_download_manager),
                            contentDescription = stringResource(R.string.title_download_manager)
                        )
                    }
                    IconButton(onClick = { showMoreSheet = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_account),
                            contentDescription = stringResource(R.string.title_more)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.play_nav_bar_background)
            ) {
                MainNavItem.entries.forEach { item ->
                    val selected = item.pagerPage?.let { pagerState.currentPage == it } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            when {
                                item.pagerPage != null -> {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(item.pagerPage)
                                    }
                                }
                                item.destination != null -> onNavigateTo(item.destination)
                            }
                        },
                        icon = {
                            if (item.showUpdateBadge && updateCount > 0) {
                                BadgedBox(badge = { Badge { Text("$updateCount") } }) {
                                    Icon(
                                        painter = painterResource(item.iconRes),
                                        contentDescription = null
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(item.iconRes),
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
                        pageType = 1,
                        onNavigateTo = ::handleNavigation
                    )
                    MainPagerPage.APPS -> AppsGamesScreen(
                        pageType = 0,
                        onNavigateTo = onNavigateTo
                    )
                    MainPagerPage.UPDATES -> UpdatesScreen(
                        viewModel = updatesViewModel,
                        onNavigateTo = ::handleNavigation,
                        onRequestUpdate = { update ->
                            if (update.fileList.requiresObbDir() &&
                                !isGranted(context, PermissionType.STORAGE_MANAGER)
                            ) {
                                onNavigateTo(
                                    Destination.PermissionRationale(
                                        setOf(PermissionType.STORAGE_MANAGER)
                                    )
                                )
                            } else {
                                updatesViewModel.download(update)
                            }
                        },
                        onRequestUpdateAll = { selectedUpdates ->
                            val needsObb = selectedUpdates.any {
                                it.fileList.requiresObbDir()
                            }
                            if (needsObb && !isGranted(context, PermissionType.STORAGE_MANAGER)) {
                                onNavigateTo(
                                    Destination.PermissionRationale(
                                        setOf(PermissionType.STORAGE_MANAGER)
                                    )
                                )
                            } else {
                                updatesViewModel.downloadAll(selectedUpdates)
                            }
                        },
                        onCancelUpdate = { packageName ->
                            updatesViewModel.cancelDownload(packageName)
                        },
                        onCancelAll = { updatesViewModel.cancelAll() }
                    )
                }
            }
        }
    }
}
