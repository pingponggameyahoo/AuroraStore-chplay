/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aurora.extensions.adaptiveNavigationIcon
import com.aurora.extensions.emptyPagingItems
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.Review
import com.aurora.store.R
import com.aurora.store.compose.composable.ContainedLoadingIndicator
import com.aurora.store.compose.composable.Placeholder
import com.aurora.store.compose.composable.ScrollHint
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.composable.play.PlayReviewFilterChipsRow
import com.aurora.store.compose.composable.play.PlayReviewScreenToolbar
import com.aurora.store.compose.composable.play.PlayReviewSortRow
import com.aurora.store.compose.composable.play.PlayStoreReviewItem
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.viewmodel.details.AppDetailsViewModel
import com.aurora.store.viewmodel.details.ReviewViewModel
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ReviewScreen(
    packageName: String,
    appDetailsViewModel: AppDetailsViewModel = hiltViewModel(key = packageName),
    reviewViewModel: ReviewViewModel = hiltViewModel(
        key = "$packageName/review",
        creationCallback = { factory: ReviewViewModel.Factory ->
            factory.create(appDetailsViewModel.app.value!!.packageName)
        }
    ),
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    val app by appDetailsViewModel.app.collectAsStateWithLifecycle()
    val reviews = reviewViewModel.reviews.collectAsLazyPagingItems()

    ScreenContent(
        app = app!!,
        reviews = reviews,
        onFilter = { filter -> reviewViewModel.fetchReviews(filter) },
        windowAdaptiveInfo = windowAdaptiveInfo
    )
}

@Composable
private fun ScreenContent(
    app: App,
    reviews: LazyPagingItems<Review> = emptyPagingItems(),
    onFilter: (filter: Review.Filter) -> Unit = {},
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    var activeFilter by rememberSaveable { mutableStateOf(Review.Filter.ALL) }
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    titleContent = { PlayReviewScreenToolbar(app = app) },
                    navigationIcon = windowAdaptiveInfo.adaptiveNavigationIcon
                )
                PlayReviewFilterChipsRow(
                    activeFilter = activeFilter,
                    onFilterSelected = { filter ->
                        activeFilter = filter
                        onFilter(filter)
                    }
                )
                PlayReviewSortRow(activeFilter = activeFilter)
            }
        }
    ) { paddingValues ->
        when (reviews.loadState.refresh) {
            is LoadState.Loading -> {
                ContainedLoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is LoadState.Error -> {
                Placeholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    painter = painterResource(R.drawable.ic_disclaimer),
                    message = stringResource(R.string.error)
                )
            }

            else -> {
                val listState = rememberLazyListState()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState
                    ) {
                        items(
                            count = reviews.itemCount,
                            key = reviews.itemKey { it.commentId }
                        ) { index ->
                            reviews[index]?.let { review ->
                                PlayStoreReviewItem(
                                    review = review,
                                    horizontalPadding = horizontalPadding,
                                    primaryText = primaryText,
                                    secondaryText = secondaryText
                                )
                            }
                        }
                    }
                    ScrollHint(
                        listState = listState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview
@Composable
private fun ReviewScreenPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    val review = Review(
        userName = "Dân Trần",
        timeStamp = System.currentTimeMillis(),
        rating = 5,
        comment = "Bản cập nhật này với hình ảnh có bản quyền."
    )
    val reviews = List(5) { review.copy(commentId = Random.nextInt().toString()) }
    val reviewsFlow = MutableStateFlow(PagingData.from(reviews)).collectAsLazyPagingItems()

    ScreenContent(app = app, reviews = reviewsFlow)
}
