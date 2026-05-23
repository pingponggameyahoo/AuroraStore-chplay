/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R

private val PLAY_AGE_BADGE_REGEX = Regex("""(\d+\+)""")

/**
 * Horizontal stats row on the app details screen (Google Play style).
 */
@Composable
fun PlayStoreDetailsInfoBar(
    app: App,
    modifier: Modifier = Modifier,
    onRatingInfoClick: () -> Unit = {},
    onContentRatingInfoClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val stats = remember(app) { buildPlayDetailsStats(app, context) }
    if (stats.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.spacing_large)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(
            items = stats,
            key = { _, stat -> stat.id }
        ) { index, stat ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.spacing_small))
                            .height(dimensionResource(R.dimen.play_details_info_divider_height)),
                        thickness = 1.dp,
                        color = colorResource(R.color.play_details_info_divider)
                    )
                }
                PlayDetailsStatCell(
                    stat = stat,
                    onRatingInfoClick = onRatingInfoClick,
                    onContentRatingInfoClick = onContentRatingInfoClick
                )
            }
        }
    }
}

@Composable
private fun PlayDetailsSubtitleRow(
    text: String,
    showInfoIcon: Boolean,
    onInfoClick: (() -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.play_details_secondary_text),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (showInfoIcon) {
            Icon(
                painter = painterResource(R.drawable.ic_play_info_outline),
                contentDescription = stringResource(R.string.play_details_info_icon_cd),
                modifier = Modifier
                    .requiredSize(12.dp)
                    .then(
                        if (onInfoClick != null) {
                            Modifier.clickable(onClick = onInfoClick)
                        } else {
                            Modifier
                        }
                    ),
                tint = colorResource(R.color.play_details_info_icon)
            )
        }
    }
}

@Composable
private fun PlayDetailsStatCell(
    stat: PlayDetailsStat,
    onRatingInfoClick: () -> Unit,
    onContentRatingInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(min = dimensionResource(R.dimen.play_details_info_stat_min_width))
            .padding(horizontal = dimensionResource(R.dimen.play_details_info_stat_horizontal_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier.height(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val primary = stat.primary) {
                is PlayDetailsPrimary.Rating -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = primary.value,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.play_details_primary_text)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_play_star_filled),
                            contentDescription = null,
                            modifier = Modifier.requiredSize(16.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                is PlayDetailsPrimary.AgeRating -> {
                    val artworkUrl = primary.artworkUrl
                    if (!artworkUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = artworkUrl,
                            contentDescription = primary.subtitle,
                            modifier = Modifier.requiredSize(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .requiredSize(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = colorResource(R.color.play_details_primary_text),
                                    shape = RoundedCornerShape(2.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = primary.badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = colorResource(R.color.play_details_primary_text)
                            )
                        }
                    }
                }

                is PlayDetailsPrimary.DownloadSize -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_details_download),
                        contentDescription = primary.sizeLabel,
                        modifier = Modifier.requiredSize(22.dp),
                        tint = colorResource(R.color.play_details_secondary_text)
                    )
                }

                is PlayDetailsPrimary.Installs -> {
                    Text(
                        text = primary.headline,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.play_details_primary_text),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        val subtitle = stat.subtitle
        if (!subtitle.isNullOrBlank()) {
            PlayDetailsSubtitleRow(
                text = subtitle,
                showInfoIcon = stat.showInfoIcon,
                onInfoClick = when (stat.id) {
                    PlayDetailsStatId.RATING -> onRatingInfoClick
                    PlayDetailsStatId.AGE -> onContentRatingInfoClick
                    else -> null
                }
            )
        }
    }
}

private enum class PlayDetailsStatId {
    RATING,
    AGE,
    SIZE,
    INSTALLS
}

private sealed interface PlayDetailsPrimary {
    data class Rating(val value: String) : PlayDetailsPrimary
    data class AgeRating(val badge: String, val artworkUrl: String?, val subtitle: String) :
        PlayDetailsPrimary

    data class DownloadSize(val sizeLabel: String) : PlayDetailsPrimary
    data class Installs(val headline: String) : PlayDetailsPrimary
}

private data class PlayDetailsStat(
    val id: PlayDetailsStatId,
    val primary: PlayDetailsPrimary,
    val subtitle: String?,
    val showInfoIcon: Boolean = false
)

private fun buildPlayDetailsStats(app: App, context: android.content.Context): List<PlayDetailsStat> {
    return buildList {
        app.playDetailsRatingValue()?.let { ratingValue ->
            val reviewSubtitle = app.playReviewCountSubtitle(context)
            add(
                PlayDetailsStat(
                    id = PlayDetailsStatId.RATING,
                    primary = PlayDetailsPrimary.Rating(ratingValue),
                    subtitle = reviewSubtitle,
                    showInfoIcon = reviewSubtitle != null
                )
            )
        }

        val ageSubtitle = app.playAgeRatingSubtitle(context)
        if (ageSubtitle.isNotBlank() || app.contentRating.artwork.url.isNotBlank()) {
            add(
                PlayDetailsStat(
                    id = PlayDetailsStatId.AGE,
                    primary = PlayDetailsPrimary.AgeRating(
                        badge = app.playAgeRatingBadge(),
                        artworkUrl = app.contentRating.artwork.url.takeIf { it.isNotBlank() },
                        subtitle = ageSubtitle
                    ),
                    subtitle = ageSubtitle,
                    showInfoIcon = true
                )
            )
        }

        val sizeLabel = app.playFileSizeLabel()
        if (sizeLabel.isNotBlank()) {
            add(
                PlayDetailsStat(
                    id = PlayDetailsStatId.SIZE,
                    primary = PlayDetailsPrimary.DownloadSize(sizeLabel),
                    subtitle = sizeLabel
                )
            )
        }

        app.playInstallsHeadlineWithPrefix(context)?.let { installsHeadline ->
            add(
                PlayDetailsStat(
                    id = PlayDetailsStatId.INSTALLS,
                    primary = PlayDetailsPrimary.Installs(installsHeadline),
                    subtitle = context.getString(R.string.play_details_downloads_label),
                    showInfoIcon = false
                )
            )
        }
    }
}

internal fun App.playDetailsRatingValue(): String? = playRatingDisplay()

internal fun App.playAgeRatingBadge(): String {
    PLAY_AGE_BADGE_REGEX.find(contentRating.title)?.value?.let { return it }
    val digits = Regex("""(\d+)""").find(contentRating.title)?.value
    if (!digits.isNullOrBlank()) return "$digits+"
    return when {
        contentRating.title.contains("18", ignoreCase = true) -> "18+"
        contentRating.title.contains("12", ignoreCase = true) -> "12+"
        contentRating.title.contains("7", ignoreCase = true) -> "7+"
        contentRating.title.contains("3", ignoreCase = true) -> "3+"
        else -> "?"
    }
}

internal fun App.playAgeRatingSubtitle(context: android.content.Context): String =
    contentRating.title.trim().takeIf { it.isNotBlank() }
        ?: contentRating.recommendation.trim().takeIf { it.isNotBlank() }
        ?: context.getString(R.string.play_details_age_fallback)

