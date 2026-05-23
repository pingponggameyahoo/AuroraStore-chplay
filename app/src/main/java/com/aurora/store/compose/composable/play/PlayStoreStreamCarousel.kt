/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.StreamBundle
import com.aurora.gplayapi.data.models.StreamCluster
import com.aurora.store.R
import com.aurora.store.compose.composable.Placeholder
import com.aurora.store.compose.composable.ShimmerCarouselSection
import kotlinx.coroutines.flow.distinctUntilChanged

private const val LOAD_MORE_THRESHOLD = 2
private const val TRIPLE_ROW_PAGE_SIZE = 3

@Composable
fun PlayStoreStreamCarousel(
    modifier: Modifier = Modifier,
    streamBundle: StreamBundle?,
    lazyListState: LazyListState = rememberLazyListState(),
    onHeaderClick: (StreamCluster) -> Unit = {},
    onAppClick: (App) -> Unit = {},
    onClusterScrolled: (StreamCluster) -> Unit = {},
    onScrolledToEnd: () -> Unit = {}
) {
    val background = colorResource(R.color.play_store_content_background)
    val bundleLoaded = streamBundle != null

    LaunchedEffect(lazyListState, bundleLoaded) {
        snapshotFlow {
            val last = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = lazyListState.layoutInfo.totalItemsCount
            last >= total - LOAD_MORE_THRESHOLD
        }.distinctUntilChanged().collect { reachedEnd ->
            if (reachedEnd && bundleLoaded) onScrolledToEnd()
        }
    }

    if (streamBundle == null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(background),
            state = lazyListState,
            contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            item { PlayFeaturedShimmer() }
            items(2) { ShimmerCarouselSection() }
        }
        return
    }

    val clusters = streamBundle.streamClusters.values.filter { cluster ->
        cluster.clusterAppList.isNotEmpty() && cluster.clusterTitle.isNotBlank()
    }

    if (clusters.isEmpty()) {
        Placeholder(
            modifier = modifier.background(background),
            painter = painterResource(R.drawable.ic_apps),
            message = stringResource(R.string.no_apps_available)
        )
        return
    }

    val featuredCluster = clusters.first()
    val tripleClusters = clusters.drop(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.spacing_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.play_section_spacing))
    ) {
        item(key = "featured_${featuredCluster.id}") {
            PlayFeaturedCarousel(
                apps = featuredCluster.clusterAppList,
                onAppClick = onAppClick
            )
        }

        tripleClusters.forEach { cluster ->
            item(key = "triple_header_${cluster.id}") {
                PlayClusterSectionHeader(
                    title = cluster.clusterTitle,
                    subtitle = cluster.clusterSubtitle.takeIf { it.isNotBlank() },
                    onMenuClick = if (cluster.clusterBrowseUrl.isNotBlank()) {
                        { onHeaderClick(cluster) }
                    } else {
                        null
                    }
                )
            }
            item(key = "triple_row_${cluster.id}") {
                PlayTripleColumnCarousel(
                    cluster = cluster,
                    onAppClick = onAppClick,
                    onClusterScrolled = onClusterScrolled
                )
            }
        }

        if (streamBundle.hasNext()) {
            item(key = "shimmer_footer") {
                ShimmerCarouselSection()
            }
        }
    }
}

@Composable
private fun PlayFeaturedCarousel(
    apps: List<App>,
    onAppClick: (App) -> Unit
) {
    val configuration = LocalConfiguration.current
    val horizontalInset = dimensionResource(R.dimen.play_featured_card_horizontal_inset)
    val cardSpacing = dimensionResource(R.dimen.play_featured_card_spacing)
    val cardWidth = (configuration.screenWidthDp.dp - horizontalInset * 2)

    LazyRow(
        contentPadding = PaddingValues(horizontal = horizontalInset),
        horizontalArrangement = Arrangement.spacedBy(cardSpacing)
    ) {
        items(
            items = apps,
            key = { it.packageName }
        ) { app ->
            PlayFeaturedAppCard(
                app = app,
                modifier = Modifier.width(cardWidth),
                onClick = { onAppClick(app) },
                onInstallClick = { onAppClick(app) }
            )
        }
    }
}

@Composable
private fun PlayTripleColumnCarousel(
    cluster: StreamCluster,
    onAppClick: (App) -> Unit,
    onClusterScrolled: (StreamCluster) -> Unit
) {
    val pages = remember(cluster.clusterAppList) {
        cluster.clusterAppList.chunked(TRIPLE_ROW_PAGE_SIZE)
    }
    val rowState = rememberLazyListState()
    val reachedEnd by remember {
        derivedStateOf {
            val last = rowState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = rowState.layoutInfo.totalItemsCount
            last >= total - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd && cluster.hasNext()) onClusterScrolled(cluster)
    }

    val columnWidth = dimensionResource(R.dimen.play_triple_column_width)
    val horizontalInset = dimensionResource(R.dimen.play_featured_card_horizontal_inset)

    LazyRow(
        state = rowState,
        contentPadding = PaddingValues(horizontal = horizontalInset),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        itemsIndexed(
            items = pages,
            key = { index, _ -> "${cluster.id}_page_$index" }
        ) { _, pageApps ->
            Card(
                modifier = Modifier.width(columnWidth),
                shape = RoundedCornerShape(dimensionResource(R.dimen.play_featured_card_corner)),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.play_store_card_surface)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.spacing_medium),
                        vertical = dimensionResource(R.dimen.spacing_small)
                    )
                ) {
                    pageApps.forEach { app ->
                        PlayCompactAppRow(
                            app = app,
                            onClick = { onAppClick(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayClusterSectionHeader(
    title: String,
    subtitle: String?,
    onMenuClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.play_featured_card_horizontal_inset),
                end = dimensionResource(R.dimen.spacing_small),
                top = dimensionResource(R.dimen.spacing_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.play_store_text_secondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (onMenuClick != null) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert),
                    contentDescription = null,
                    tint = colorResource(R.color.play_store_text_secondary)
                )
            }
        }
    }
}

@Composable
private fun PlayFeaturedShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.play_featured_card_horizontal_inset)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.play_featured_card_corner)),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.play_store_card_surface)
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            ShimmerCarouselSection()
        }
    }
}
