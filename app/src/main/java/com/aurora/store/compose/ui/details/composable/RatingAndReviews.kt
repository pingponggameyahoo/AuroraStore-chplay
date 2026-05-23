/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.details.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.Rating
import com.aurora.gplayapi.data.models.Review
import com.aurora.store.R
import com.aurora.store.compose.composable.play.PlayStoreRatingsReviewsSection
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Composable to display reviews of the app, supposed to be used as a part
 * of the Column with proper vertical arrangement spacing in the AppDetailsScreen.
 * @param rating Rating of the app
 * @param featuredReviews Featured app reviews
 * @param onNavigateToDetailsReview Callback when the user navigates
 * @param windowAdaptiveInfo Adaptive window information
 */
@Composable
fun RatingAndReviews(
    rating: Rating,
    featuredReviews: List<Review> = emptyList(),
    onNavigateToDetailsReview: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    PlayStoreRatingsReviewsSection(
        rating = rating,
        reviews = featuredReviews,
        onSeeAllReviews = onNavigateToDetailsReview
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun RatingAndReviewsPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    val reviews = List(3) {
        Review(
            userName = "Dân Trần",
            timeStamp = System.currentTimeMillis(),
            rating = 5,
            comment = LoremIpsum(40).values.first()
        )
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        RatingAndReviews(rating = app.rating, featuredReviews = reviews)
    }
}
