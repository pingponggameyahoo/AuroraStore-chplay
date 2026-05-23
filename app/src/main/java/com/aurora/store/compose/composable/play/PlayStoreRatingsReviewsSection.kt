/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aurora.gplayapi.data.models.Rating
import com.aurora.gplayapi.data.models.Review
import com.aurora.store.R
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

private val PLAY_REVIEW_AVATAR_COLORS = listOf(
    0xFF6750A4,
    0xFFE8710A,
    0xFF188038,
    0xFF1A73E8,
    0xFFD93025,
    0xFF9334E6
)

@Composable
fun PlayStoreRatingsReviewsSection(
    rating: Rating,
    reviews: List<Review>,
    onSeeAllReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val starCounts = listOf(
        rating.oneStar,
        rating.twoStar,
        rating.threeStar,
        rating.fourStar,
        rating.fiveStar
    ).map { it.toFloat() }
    val total = starCounts.sum()
    if (total <= 0f) return

    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val linkColor = colorResource(R.color.play_nav_selected)
    val barTrackColor = colorResource(R.color.play_details_chip_border)

    Column(modifier = modifier.fillMaxWidth()) {
        PlayRatingsSectionHeader(
            title = stringResource(R.string.play_details_ratings_title),
            onNavigate = onSeeAllReviews
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(bottom = dimensionResource(R.dimen.spacing_medium)),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(R.string.play_details_ratings_verified),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = secondaryText
            )
            Icon(
                painter = painterResource(R.drawable.ic_play_info_outline),
                contentDescription = stringResource(R.string.play_details_info_icon_cd),
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(16.dp),
                tint = secondaryText
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(bottom = dimensionResource(R.dimen.spacing_xlarge)),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayRatingSummaryColumn(
                average = rating.average,
                reviewCountLabel = rating.abbreviatedLabel.trim().ifBlank {
                    formatPlayCompactCount(starCounts.sum().toLong())
                },
                starSize = dimensionResource(R.dimen.play_rating_summary_star_size),
                primaryText = primaryText,
                secondaryText = secondaryText
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (5 downTo 1).forEach { level ->
                    val count = starCounts[level - 1]
                    PlayRatingDistributionBar(
                        label = level.toString(),
                        fraction = if (total > 0f) count / total else 0f,
                        trackColor = barTrackColor,
                        fillColor = linkColor,
                        labelColor = secondaryText
                    )
                }
            }
        }

        reviews.forEach { review ->
            PlayStoreReviewItem(
                review = review,
                horizontalPadding = horizontalPadding,
                primaryText = primaryText,
                secondaryText = secondaryText
            )
        }
    }
}

@Composable
private fun PlayRatingsSectionHeader(
    title: String,
    onNavigate: () -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val interactionSource = remember { MutableInteractionSource() }
    val buttonWidth = dimensionResource(R.dimen.play_details_nav_button_width)
    val buttonHeight = dimensionResource(R.dimen.play_details_nav_button_height)
    val iconSize = dimensionResource(R.dimen.play_details_nav_icon_size)
    val pillShape = RoundedCornerShape(percent = 50)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = dimensionResource(R.dimen.spacing_large),
                bottom = dimensionResource(R.dimen.spacing_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.play_details_primary_text),
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.spacing_small))
        )
        Box(
            modifier = Modifier
                .width(buttonWidth)
                .height(buttonHeight)
                .clip(pillShape)
                .background(colorResource(R.color.play_details_nav_button_background))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, radius = buttonHeight / 2),
                    onClick = onNavigate
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play_details_nav_forward),
                contentDescription = stringResource(R.string.play_details_see_more_cd),
                modifier = Modifier.size(iconSize),
                tint = colorResource(R.color.play_details_primary_text)
            )
        }
    }
}

@Composable
private fun PlayRatingSummaryColumn(
    average: Float,
    reviewCountLabel: String,
    starSize: androidx.compose.ui.unit.Dp,
    primaryText: Color,
    secondaryText: Color
) {
    val locale = Locale.getDefault()
    val averageText = remember(average, locale) {
        String.format(locale, "%.1f", average).replace('.', ',')
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = averageText,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 56.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
            color = primaryText
        )
        PlayRatingStars(
            rating = average,
            starSize = starSize,
            filledTint = Color.Unspecified,
            emptyTint = Color.Unspecified
        )
        if (reviewCountLabel.isNotBlank()) {
            Text(
                text = reviewCountLabel,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = secondaryText
            )
        }
    }
}

@Composable
fun PlayRatingStars(
    rating: Float,
    starSize: androidx.compose.ui.unit.Dp,
    filledTint: Color = Color.Unspecified,
    emptyTint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 1..5) {
            val fillAmount = (rating - (index - 1)).coerceIn(0f, 1f)
            PlayRatingStar(fillAmount = fillAmount, size = starSize, filledTint = filledTint, emptyTint = emptyTint)
        }
    }
}

@Composable
private fun PlayRatingStar(
    fillAmount: Float,
    size: androidx.compose.ui.unit.Dp,
    filledTint: Color,
    emptyTint: Color
) {
    Box(modifier = Modifier.size(size)) {
        Icon(
            painter = painterResource(R.drawable.ic_play_star_empty),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = emptyTint
        )
        if (fillAmount > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fillAmount)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_star_filled),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = filledTint
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayRatingDistributionBar(
    label: String,
    fraction: Float,
    trackColor: Color,
    fillColor: Color,
    labelColor: Color
) {
    val barHeight = dimensionResource(R.dimen.play_rating_distribution_bar_height)
    val labelWidth = dimensionResource(R.dimen.play_rating_distribution_label_width)
    val barShape = RoundedCornerShape(percent = 50)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(labelWidth),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = labelColor
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(barShape)
                .background(trackColor)
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .clip(barShape)
                        .background(fillColor)
                )
            }
        }
    }
}

@Composable
internal fun PlayStoreReviewItem(
    review: Review,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    primaryText: Color,
    secondaryText: Color
) {
    val context = LocalContext.current
    val avatarSize = dimensionResource(R.dimen.play_review_avatar_size)
    val starSize = dimensionResource(R.dimen.play_rating_review_star_size)
    val chipBorder = colorResource(R.color.play_details_chip_border)
    val avatarLetter = review.userName.firstOrNull()?.uppercaseChar()?.toString().orEmpty().ifBlank { "?" }
    val avatarColor = remember(review.userName) {
        Color(PLAY_REVIEW_AVATAR_COLORS[review.userName.hashCode().absoluteValue % PLAY_REVIEW_AVATAR_COLORS.size])
    }
    val dateText = remember(review.timeStamp) { formatPlayReviewDate(review.timeStamp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding,
                vertical = dimensionResource(R.dimen.spacing_large)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (review.userPhotoUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(review.userPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarLetter,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            Text(
                text = review.userName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = primaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = secondaryText
            )
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlayRatingStars(
                rating = review.rating.toFloat(),
                starSize = starSize
            )
            if (dateText.isNotBlank()) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = secondaryText
                )
            }
        }

        val comment = review.comment.trim().ifBlank { review.title.trim() }
        if (comment.isNotBlank()) {
            Text(
                text = comment,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = primaryText
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.play_review_helpful_question),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = primaryText
            )
            PlayReviewHelpfulChip(
                label = stringResource(R.string.play_review_helpful_yes),
                borderColor = chipBorder,
                textColor = primaryText
            )
            PlayReviewHelpfulChip(
                label = stringResource(R.string.play_review_helpful_no),
                borderColor = chipBorder,
                textColor = primaryText
            )
        }
    }
}

@Composable
private fun PlayReviewHelpfulChip(
    label: String,
    borderColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun formatPlayReviewDate(timestamp: Long): String {
    if (timestamp <= 0L) return String()
    val locale = Locale.getDefault()
    val calendar = Calendar.getInstance(locale).apply { timeInMillis = timestamp }
    return String.format(
        locale,
        "%d/%d/%02d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR) % 100
    )
}
