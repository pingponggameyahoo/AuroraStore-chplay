/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.aurora.gplayapi.data.models.Category
import com.aurora.store.CategoryStash
import com.aurora.store.compose.composable.CategoryItem
import com.aurora.store.compose.composable.ShimmerCategoryRow
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.data.model.ViewState
import com.aurora.store.viewmodel.category.CategoryViewModel

@Composable
internal fun CategoriesContent(
    section: StoreSection,
    categoryType: Category.Type = section.categoryBrowseType,
    viewModel: CategoryViewModel,
    contentListState: LazyListState = rememberLazyListState(),
    onCategoryClick: (Category) -> Unit
) {
    val state by viewModel.liveData.observeAsState()

    LaunchedEffect(categoryType) {
        viewModel.getCategoryList(categoryType)
    }

    @Suppress("UNCHECKED_CAST")
    val categories = (state as? ViewState.Success<*>)?.data as? CategoryStash
    val list = categories?.get(categoryType)

    CategoriesBody(
        list = list,
        listState = contentListState,
        onCategoryClick = onCategoryClick
    )
}

@Composable
private fun CategoriesBody(
    list: List<Category>?,
    listState: LazyListState,
    onCategoryClick: (Category) -> Unit = {}
) {
    if (list.isNullOrEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(10) { ShimmerCategoryRow() }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(count = list.size, key = { list[it].title }) { index ->
                CategoryItem(
                    category = list[index],
                    onClick = { onCategoryClick(list[index]) }
                )
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun CategoriesBodyLoadingPreview() {
    CategoriesBody(list = null, listState = rememberLazyListState())
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun CategoriesBodyLoadedPreview() {
    val categories = listOf(
        "Art & Design",
        "Auto & Vehicles",
        "Beauty",
        "Books & Reference",
        "Business",
        "Comics",
        "Communication",
        "Dating",
        "Education",
        "Entertainment"
    ).map { Category(title = it, imageUrl = "") }
    CategoriesBody(list = categories, listState = rememberLazyListState())
}
