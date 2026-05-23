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
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.extensions.adaptiveNavigationIcon
import com.aurora.gplayapi.data.models.datasafety.Report
import com.aurora.store.R
import com.aurora.store.compose.composable.ScrollHint
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.ui.details.composable.DataSafety

@Composable
fun DataSafetyScreen(
    report: Report?,
    privacyPolicyUrl: String,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.play_details_data_safety_title),
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
            if (report != null) {
                LazyColumn(state = listState) {
                    item {
                        DataSafety(
                            report = report,
                            privacyPolicyUrl = privacyPolicyUrl,
                            showSectionHeader = false
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.play_details_data_safety_intro),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    fontSize = 14.sp
                )
            }
            ScrollHint(
                listState = listState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
