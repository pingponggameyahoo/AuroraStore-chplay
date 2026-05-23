/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import com.aurora.gplayapi.data.models.App
import com.aurora.store.util.CommonUtil
import java.util.Locale

private val PLAY_GENRE_ENUM_REGEX = Regex("^[A-Z][A-Z0-9_]{2,}$")

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

/** Line 2: localized genre tags from Play (e.g. "Xã hội • Xây dựng mối quan hệ"). */
internal fun App.playGenreTagsLine(): String {
    val tagLabels = buildList {
        tags.forEach { tag ->
            tag.name.trim().takeIf { it.isNotBlank() && !it.isPlayGenreEnum() }?.let { add(it) }
        }
        chips.forEach { chip ->
            chip.title.trim().takeIf { it.isNotBlank() && !it.isPlayGenreEnum() }?.let { add(it) }
        }
    }.distinct().take(3)

    if (tagLabels.isNotEmpty()) {
        return tagLabels.joinToString(separator = " • ")
    }

    val category = categoryName.trim()
    return when {
        category.isBlank() -> ""
        category.isPlayGenreEnum() -> category.humanizePlayGenreEnum()
        else -> category
    }
}

/** Line 3: formatted rating for display (Play uses comma decimal in vi). */
internal fun App.playRatingDisplay(): String? {
    val rating = labeledRating.trim()
    if (rating.isBlank() || rating == "0.0" || rating == "0") return null
    return rating.replace('.', ',')
}

internal fun App.playFileSizeLabel(): String? {
    val downloadSize = size
    if (downloadSize <= 0L) return null
    return CommonUtil.addSiPrefix(downloadSize)
}

internal fun App.developerMetaLine(): String = buildList {
    developerName.takeIf { it.isNotBlank() }?.let { add(it) }
    playRatingDisplay()?.let { add("$it★") }
}.joinToString(separator = "  •  ")

private fun String.isPlayGenreEnum(): Boolean =
    PLAY_GENRE_ENUM_REGEX.matches(this) && contains('_')

private fun String.humanizePlayGenreEnum(): String {
    if (!isPlayGenreEnum()) return this
    return removePrefix("GAME_")
        .removePrefix("APPLICATION_")
        .removePrefix("FAMILY_")
        .split('_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { segment ->
            segment.lowercase(Locale.getDefault()).replaceFirstChar { char ->
                char.titlecase(Locale.getDefault())
            }
        }
}
