/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/** Scroll offset (px) after which the Play logo / notify / avatar row hides (CH Play behavior). */
private const val HEADER_COLLAPSE_SCROLL_PX = 24

@Composable
fun rememberPlayStoreHeaderHidden(listState: LazyListState): State<Boolean> =
    remember(listState) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > HEADER_COLLAPSE_SCROLL_PX
        }
    }
