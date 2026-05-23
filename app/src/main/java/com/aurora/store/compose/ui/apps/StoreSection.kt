/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.apps

import androidx.annotation.StringRes
import com.aurora.gplayapi.data.models.Category
import com.aurora.gplayapi.helpers.contracts.StreamContract
import com.aurora.gplayapi.helpers.contracts.TopChartsContract
import com.aurora.store.R

enum class StoreSection(val pageType: Int) {
    APPS(0),
    GAMES(1),
    BOOKS(2);

    val streamCategory: StreamContract.Category
        get() = when (this) {
            GAMES -> StreamContract.Category.GAME
            APPS, BOOKS -> StreamContract.Category.APPLICATION
        }

    /** Category id sent to Play top-charts API. */
    val topChartCategory: String
        get() = when (this) {
            GAMES -> TopChartsContract.Type.GAME.value
            APPS -> TopChartsContract.Type.APPLICATION.value
            BOOKS -> "BOOK"
        }

    val topChartType: TopChartsContract.Type
        get() = when (this) {
            GAMES -> TopChartsContract.Type.GAME
            else -> TopChartsContract.Type.APPLICATION
        }

    val categoryBrowseType: Category.Type
        get() = when (this) {
            GAMES -> Category.Type.GAME
            APPS -> Category.Type.APPLICATION
            BOOKS -> Category.Type.APPLICATION
        }

    val tabs: List<StoreTab>
        get() = when (this) {
            GAMES -> listOf(
                StoreTab.ForYou,
                StoreTab.TopCharts,
                StoreTab.Kids,
                StoreTab.Paid,
                StoreTab.Types
            )
            APPS -> listOf(
                StoreTab.ForYou,
                StoreTab.TopCharts,
                StoreTab.Kids,
                StoreTab.Types
            )
            BOOKS -> listOf(
                StoreTab.Ebooks,
                StoreTab.Audiobooks,
                StoreTab.Genres,
                StoreTab.Bestsellers,
                StoreTab.NewReleases,
                StoreTab.PopularFree
            )
        }

    companion object {
        fun fromPageType(pageType: Int): StoreSection =
            entries.find { it.pageType == pageType } ?: APPS
    }
}

sealed class StoreTab(@StringRes val titleRes: Int) {
    data object ForYou : StoreTab(R.string.tab_for_you)
    data object TopCharts : StoreTab(R.string.tab_top_charts)
    data object Kids : StoreTab(R.string.tab_kids)
    data object Paid : StoreTab(R.string.tab_paid_premium)
    data object Types : StoreTab(R.string.tab_types)

    data object Ebooks : StoreTab(R.string.tab_ebooks)
    data object Audiobooks : StoreTab(R.string.tab_audiobooks)
    data object Genres : StoreTab(R.string.tab_genres)
    data object Bestsellers : StoreTab(R.string.tab_bestsellers)
    data object NewReleases : StoreTab(R.string.tab_new_releases)
    data object PopularFree : StoreTab(R.string.tab_popular_free)
}

fun StoreTab.streamType(section: StoreSection): StreamContract.Type = when (this) {
    StoreTab.Paid -> StreamContract.Type.PREMIUM_GAMES
    StoreTab.Ebooks -> StreamContract.Type.HOME
    StoreTab.Audiobooks -> StreamContract.Type.EDITOR_CHOICE
    else -> StreamContract.Type.HOME
}

fun StoreTab.bookChartId(): String? = when (this) {
    StoreTab.Bestsellers -> "topgrossing"
    StoreTab.NewReleases -> "topselling_new_free"
    StoreTab.PopularFree -> "topselling_free"
    StoreTab.Ebooks -> "topselling_paid"
    StoreTab.Audiobooks -> "topselling_paid"
    else -> null
}
