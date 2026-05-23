/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.aurora.store.R

/** Width of the indicator pill relative to the selected tab label width. */
private const val playTabIndicatorWidthFraction = 0.9f

private val playTabIndicatorHeight = 3.dp
private val playTabIndicatorTopRadius = 3.dp

private val playTabIndicatorShape = RoundedCornerShape(
    topStart = playTabIndicatorTopRadius,
    topEnd = playTabIndicatorTopRadius,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@Composable
private fun TabIndicatorScope.PlayStoreTabIndicator(
    selectedTabIndex: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .tabIndicatorOffset(
                selectedTabIndex = selectedTabIndex,
                matchContentSize = true
            )
            .height(playTabIndicatorHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(playTabIndicatorWidthFraction)
                .fillMaxHeight()
                .background(color = color, shape = playTabIndicatorShape)
        )
    }
}

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
        edgePadding = dimensionResource(R.dimen.play_home_content_inset),
        divider = {
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(R.color.play_tab_divider)
            )
        },
        indicator = {
            PlayStoreTabIndicator(
                selectedTabIndex = selectedTabIndex,
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
        edgePadding = dimensionResource(R.dimen.play_home_content_inset),
        divider = {
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(R.color.play_tab_divider)
            )
        },
        indicator = {
            PlayStoreTabIndicator(
                selectedTabIndex = selectedTabIndex,
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
