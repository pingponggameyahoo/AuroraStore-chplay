/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.Review
import com.aurora.store.R
import com.aurora.store.compose.composable.app.AnimatedAppIcon

private val PLAY_REVIEW_FILTERS = listOf(
    Review.Filter.ALL,
    Review.Filter.POSITIVE,
    Review.Filter.CRITICAL,
    Review.Filter.FIVE,
    Review.Filter.FOUR,
    Review.Filter.THREE,
    Review.Filter.TWO,
    Review.Filter.ONE
)

@StringRes
private fun Review.Filter.labelRes(): Int = when (this) {
    Review.Filter.ALL -> R.string.filter_review_all
    Review.Filter.POSITIVE -> R.string.filter_review_positive
    Review.Filter.CRITICAL -> R.string.filter_review_critical
    Review.Filter.FIVE -> R.string.filter_review_five
    Review.Filter.FOUR -> R.string.filter_review_four
    Review.Filter.THREE -> R.string.filter_review_three
    Review.Filter.TWO -> R.string.filter_review_two
    Review.Filter.ONE -> R.string.filter_review_one
    Review.Filter.NEWEST -> R.string.filter_latest
}

@Composable
fun PlayReviewScreenToolbar(app: App, modifier: Modifier = Modifier) {
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val ratingLabel = app.playRatingDisplay()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedAppIcon(
            modifier = Modifier.size(40.dp),
            iconUrl = app.iconArtwork.url,
            inProgress = false,
            progress = 0f
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = app.displayName,
                    modifier = Modifier.weight(1f, fill = false),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!ratingLabel.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = ratingLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_play_star_filled),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.play_review_ratings_for_phone),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PlayReviewFilterChipsRow(
    activeFilter: Review.Filter,
    onFilterSelected: (Review.Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val linkColor = colorResource(R.color.play_nav_selected)
    val selectedBackground = colorResource(R.color.play_nav_indicator)
    val chipBorder = colorResource(R.color.play_details_chip_border)
    val chipShape = RoundedCornerShape(8.dp)

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = PLAY_REVIEW_FILTERS, key = { it.name }) { filter ->
            val selected = activeFilter == filter
            val label = stringResource(filter.labelRes())
            Row(
                modifier = Modifier
                    .clip(chipShape)
                    .border(
                        width = 1.dp,
                        color = if (selected) Color.Transparent else chipBorder,
                        shape = chipShape
                    )
                    .background(if (selected) selectedBackground else Color.White)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selected) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = linkColor
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    color = if (selected) linkColor else primaryText
                )
            }
        }
    }
}

@Composable
fun PlayReviewSortRow(
    activeFilter: Review.Filter,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(activeFilter.labelRes()),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = primaryText
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.play_review_sort_most_relevant),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText
            )
            Icon(
                painter = painterResource(R.drawable.ic_play_sort),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = secondaryText
            )
        }
    }
}
