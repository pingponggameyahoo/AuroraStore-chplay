/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider

@Composable
fun PlayFeaturedAppCard(
    app: App,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onInstallClick: () -> Unit = onClick
) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(dimensionResource(R.dimen.play_featured_card_corner))
    val heroHeight = dimensionResource(R.dimen.play_featured_hero_height)
    val badgeLabel = app.promoBadgeLabel()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.play_store_card_surface)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(app.heroArtworkUrl())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = dimensionResource(R.dimen.play_featured_card_corner),
                                topEnd = dimensionResource(R.dimen.play_featured_card_corner)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.45f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.72f)
                            )
                        )
                )
                if (!badgeLabel.isNullOrBlank()) {
                    Text(
                        text = badgeLabel,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.spacing_medium))
                            .clip(RoundedCornerShape(50))
                            .background(colorResource(R.color.play_store_featured_badge))
                            .padding(
                                horizontal = dimensionResource(R.dimen.spacing_small),
                                vertical = dimensionResource(R.dimen.spacing_xsmall)
                            ),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorResource(R.color.play_store_featured_badge_text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = app.displayName,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(dimensionResource(R.dimen.spacing_medium)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacing_medium)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_medium)
                )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(app.iconArtwork.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredSize(dimensionResource(R.dimen.play_compact_app_icon))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_medium)))
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = app.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.developerMetaLine(),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(R.color.play_store_text_secondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = onInstallClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.play_nav_selected)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.action_install),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = stringResource(R.string.play_in_app_purchases),
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.spacing_medium),
                        end = dimensionResource(R.dimen.spacing_medium),
                        bottom = dimensionResource(R.dimen.spacing_medium)
                    ),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.play_store_text_secondary)
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun PlayFeaturedAppCardPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    PlayFeaturedAppCard(
        app = app,
        modifier = Modifier.padding(16.dp)
    )
}
