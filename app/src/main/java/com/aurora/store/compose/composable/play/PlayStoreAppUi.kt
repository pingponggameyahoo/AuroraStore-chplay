/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import com.aurora.gplayapi.data.models.App
import com.aurora.store.util.CommonUtil

internal fun App.heroArtworkUrl(): String =
    coverArtwork.url.takeIf { it.isNotBlank() }
        ?: screenshots.firstOrNull()?.url?.takeIf { it.isNotBlank() }
        ?: iconArtwork.url

internal fun App.promoBadgeLabel(): String? {
    val badge = displayBadges.firstOrNull()
    return badge?.textMajor?.takeIf { it.isNotBlank() }
        ?: badge?.textMinor?.takeIf { it.isNotBlank() }
        ?: chips.firstOrNull()?.title?.takeIf { it.isNotBlank() }
}

internal fun App.compactSubtitleLine(): String = buildList {
    categoryName.takeIf { it.isNotBlank() }?.let { add(it) }
    if (labeledRating.isNotBlank()) add("${labeledRating}★")
    val downloadSize = this@compactSubtitleLine.size
    if (downloadSize > 0L) add(CommonUtil.addSiPrefix(downloadSize))
}.joinToString(separator = " • ")

internal fun App.developerMetaLine(): String = buildList {
    developerName.takeIf { it.isNotBlank() }?.let { add(it) }
    if (labeledRating.isNotBlank()) add("${labeledRating}★")
}.joinToString(separator = "  •  ")
