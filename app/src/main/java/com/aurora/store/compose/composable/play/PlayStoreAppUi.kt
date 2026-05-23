/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import android.content.Context
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.util.CommonUtil
import java.text.NumberFormat
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

/** Line 3: file size as "60 MB" / "1,3 GB"; placeholder 50 MB–2 GB when unknown. */
internal fun App.playFileSizeLabel(): String {
    val bytes = if (size > 0L) size else playPlaceholderSizeBytes()
    return formatPlayFileSize(bytes)
}

private fun App.playPlaceholderSizeBytes(): Long {
    val minBytes = 50L * 1024 * 1024
    val maxBytes = 2L * 1024 * 1024 * 1024
    val span = maxBytes - minBytes
    val seed = (packageName.hashCode().toLong() and 0xFFFFFFFFL)
    return minBytes + (seed % span)
}

private fun formatPlayFileSize(bytes: Long): String {
    val mb = bytes.toDouble() / (1024.0 * 1024.0)
    return if (mb < 1024.0) {
        "${formatPlaySizeNumber(mb)} MB"
    } else {
        "${formatPlaySizeNumber(mb / 1024.0)} GB"
    }
}

private fun formatPlaySizeNumber(value: Double): String {
    val locale = Locale.getDefault()
    return if (value >= 100 || value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(locale, "%.1f", value).replace('.', ',')
    }
}

internal fun App.developerMetaLine(): String = buildList {
    developerName.takeIf { it.isNotBlank() }?.let { add(it) }
    playRatingDisplay()?.let { add("$it★") }
}.joinToString(separator = "  •  ")

/** Ads / IAP line under developer on the details header (Play Store). */
internal fun App.playDetailsMetaTagLine(context: Context): String? {
    val tags = playDetailsMetaTags(context)
    return tags.takeIf { it.isNotEmpty() }?.joinToString(separator = " • ")
}

internal fun App.playDetailsMetaTags(context: Context): List<String> = buildList {
    if (containsAds) {
        add(context.getString(R.string.details_contains_ads))
    }
    if (hasInAppPurchases()) {
        add(context.getString(R.string.play_in_app_purchases))
    }
}

internal fun App.hasInAppPurchases(): Boolean {
    val keywords = listOf(
        "in-app",
        "in app",
        "inapp",
        "iap",
        "mua hàng",
        "achat intégré",
        "compras no aplicativo"
    )
    val texts = buildList {
        chips.forEach { add(it.title) }
        infoBadges.forEach {
            add(it.textMajor)
            add(it.textMinor)
            add(it.textDescription.orEmpty())
        }
        displayBadges.forEach {
            add(it.textMajor)
            add(it.textMinor)
        }
        addAll(appInfo.appInfoMap.values)
    }
    return texts.any { text ->
        val normalized = text.lowercase(Locale.getDefault())
        keywords.any { normalized.contains(it) }
    }
}

/**
 * Compact count for Play UI (e.g. vi: 349131 → "349 N"; en: "349K").
 * Prefer [com.aurora.gplayapi.data.models.Rating.abbreviatedLabel] when supplied by Play.
 */
internal fun formatPlayCompactCount(count: Long, locale: Locale = Locale.getDefault()): String {
    if (count < 1_000L) {
        return NumberFormat.getIntegerInstance(locale).format(count)
    }
    return when (locale.language) {
        "vi" -> formatPlayCompactCountVi(count)
        else -> CommonUtil.addDiPrefix(count)?.trim().orEmpty().ifBlank { count.toString() }
    }
}

private fun formatPlayCompactCountVi(count: Long): String = when {
    count >= 1_000_000_000L -> {
        val billions = count / 1_000_000_000.0
        "${formatPlayCompactViNumber(billions)} Tỷ"
    }

    count >= 1_000_000L -> {
        val millions = count / 1_000_000.0
        "${formatPlayCompactViNumber(millions)} Tr"
    }

    else -> {
        val thousands = count / 1_000L
        "$thousands N"
    }
}

private fun formatPlayCompactViNumber(value: Double): String =
    if (value >= 100 || value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale("vi", "VN"), "%.1f", value).replace('.', ',')
    }

internal fun App.playReviewCountCompact(): String? {
    rating.abbreviatedLabel.trim().takeIf { it.isNotBlank() }?.let { return it }
    val total = playTotalReviewCount()
    if (total <= 0L) return null
    return formatPlayCompactCount(total)
}

internal fun App.playReviewCountSubtitle(context: Context): String? {
    val compact = playReviewCountCompact() ?: return null
    return context.getString(R.string.play_details_review_count, compact)
}

internal fun App.playTotalReviewCount(): Long = with(rating) {
    oneStar + twoStar + threeStar + fourStar + fiveStar
}

internal fun App.playInstallsHeadline(): String? {
    downloadString.trim().takeIf { it.isNotBlank() }?.let { return it }
    if (installs <= 1L) return null
    return formatPlayCompactCount(installs)
}

internal fun App.playInstallsHeadlineWithPrefix(context: Context): String? {
    val headline = playInstallsHeadline() ?: return null
    if (headline.hasPlayInstallsMorePrefix()) return headline
    return context.getString(R.string.play_details_installs_more_than, headline)
}

private fun String.hasPlayInstallsMorePrefix(): Boolean {
    val trimmed = trim()
    return trimmed.startsWith("Hơn ", ignoreCase = true) ||
        trimmed.startsWith("More than ", ignoreCase = true)
}

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
