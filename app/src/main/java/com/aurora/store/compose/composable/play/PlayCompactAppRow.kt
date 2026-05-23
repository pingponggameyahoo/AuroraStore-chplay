/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
import com.aurora.gplayapi.data.models.Tag
import com.aurora.store.R
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider

@Composable
fun PlayCompactAppRow(
    app: App,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val genreLine = app.playGenreTagsLine()
    val rating = app.playRatingDisplay()
    val fileSize = app.playFileSizeLabel()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.spacing_xsmall)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
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
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = app.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (genreLine.isNotBlank()) {
                Text(
                    text = genreLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.play_store_text_secondary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (rating != null) {
                    PlayRatingBadge(rating = rating)
                }
                if (fileSize != null) {
                    Text(
                        text = fileSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(R.color.play_store_text_secondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun PlayCompactAppRowPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    PlayCompactAppRow(
        app = app.copy(
            tags = listOf(
                Tag(name = "Xã hội"),
                Tag(name = "Xây dựng mối quan hệ")
            ),
            labeledRating = "4.1",
            size = 60L * 1024 * 1024
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
