/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.aurora.extensions.adaptiveNavigationIcon
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.compose.composable.ScrollHint
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.composable.play.PlayDataSafetyDetailContent

@Composable
fun DataSafetyScreen(
    app: App,
    privacyPolicyUrl: String,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    val screenBackground = colorResource(R.color.play_data_safety_screen_background)

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = String(),
                navigationIcon = windowAdaptiveInfo.adaptiveNavigationIcon
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    PlayDataSafetyDetailContent(
                        app = app,
                        privacyPolicyUrl = privacyPolicyUrl
                    )
                }
            }
            ScrollHint(
                listState = listState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
