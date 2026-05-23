/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aurora.store.R

private val playTabIndicatorHeight = 3.dp

@Composable
fun PlayStorePrimaryScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit
) {
    val indicatorColor = colorResource(R.color.play_tab_indicator)

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = colorResource(R.color.play_tab_container),
        contentColor = colorResource(R.color.play_tab_unselected),
        edgePadding = dimensionResource(R.dimen.spacing_small),
        divider = {
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(R.color.play_tab_divider)
            )
        },
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = true
                ),
                width = Dp.Unspecified,
                height = playTabIndicatorHeight,
                color = indicatorColor
            )
        },
        tabs = tabs
    )
}

@Composable
fun PlayStoreSecondaryScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit
) {
    val indicatorColor = colorResource(R.color.play_tab_indicator)

    SecondaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = colorResource(R.color.play_tab_container),
        contentColor = colorResource(R.color.play_tab_unselected),
        edgePadding = dimensionResource(R.dimen.spacing_small),
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = true
                ),
                height = playTabIndicatorHeight,
                color = indicatorColor
            )
        },
        tabs = tabs
    )
}

@Composable
fun PlayStoreTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        selectedContentColor = colorResource(R.color.play_tab_selected),
        unselectedContentColor = colorResource(R.color.play_tab_unselected),
        text = text
    )
}
