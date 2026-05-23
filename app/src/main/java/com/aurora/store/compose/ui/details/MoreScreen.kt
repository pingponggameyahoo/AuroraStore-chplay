/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.extensions.adaptiveNavigationIcon
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.compose.composable.Info
import com.aurora.store.compose.composable.ScrollHint
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.composable.app.AppListItem
import com.aurora.store.compose.composable.play.PlayMoreAboutSections
import com.aurora.store.compose.composable.play.PlayMoreAboutToolbarTitle
import com.aurora.store.compose.composable.play.PlayMoreSectionDivider
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.viewmodel.details.AppDetailsViewModel
import com.aurora.store.viewmodel.details.MoreViewModel

@Composable
fun MoreScreen(
    packageName: String,
    onNavigateTo: (Destination) -> Unit,
    onSeePermissions: () -> Unit = {},
    appDetailsViewModel: AppDetailsViewModel = hiltViewModel(key = packageName),
    moreViewModel: MoreViewModel = hiltViewModel(
        key = "$packageName/more",
        creationCallback = { factory: MoreViewModel.Factory ->
            factory.create(appDetailsViewModel.app.value!!.dependencies.dependentPackages)
        }
    )
) {
    val app by appDetailsViewModel.app.collectAsStateWithLifecycle()
    val dependencies by moreViewModel.dependentApps.collectAsStateWithLifecycle()

    ScreenContent(
        app = app!!,
        dependencies = dependencies,
        onNavigateTo = onNavigateTo,
        onSeePermissions = onSeePermissions
    )
}

@Composable
private fun ScreenContent(
    app: App,
    dependencies: List<App>? = null,
    onNavigateTo: (Destination) -> Unit = {},
    onSeePermissions: () -> Unit = {},
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()
) {
    val dividerColor = colorResource(R.color.play_details_chip_border)
    val horizontalPadding = dimensionResource(R.dimen.play_more_section_horizontal_padding)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.White,
        topBar = {
            TopAppBar(
                titleContent = { PlayMoreAboutToolbarTitle(app = app) },
                navigationIcon = windowAdaptiveInfo.adaptiveNavigationIcon
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                item {
                    PlayMoreSectionDivider(color = dividerColor)
                }

                item {
                    Text(
                        text = stringResource(R.string.details_description),
                        modifier = Modifier
                            .padding(horizontal = horizontalPadding)
                            .padding(
                                top = dimensionResource(R.dimen.spacing_large),
                                bottom = dimensionResource(R.dimen.spacing_small)
                            ),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                }

                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = horizontalPadding)
                            .padding(bottom = dimensionResource(R.dimen.spacing_large)),
                        text = AnnotatedString.fromHtml(htmlString = app.description),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = secondaryText
                    )
                }

                //if (dependencies != null) {
                //    item {
                //        PlayMoreSectionDivider(color = dividerColor)
                //    }
                //    item {
                //        AppDependencies(
                //            dependencies = dependencies,
                //            onNavigateTo = onNavigateTo
                //        )
                //    }
                //}

                item {
                    PlayMoreSectionDivider(color = dividerColor)
                }

                item {
                    PlayMoreAboutSections(
                        app = app,
                        onSeePermissions = onSeePermissions
                    )
                }
            }
            ScrollHint(
                listState = listState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Composable to show dependencies of an app
 */
@Composable
private fun AppDependencies(dependencies: List<App>, onNavigateTo: (Destination) -> Unit) {
    Text(
        text = stringResource(R.string.details_dependencies),
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.play_more_section_horizontal_padding))
            .padding(
                top = dimensionResource(R.dimen.play_more_section_title_top),
                bottom = dimensionResource(R.dimen.spacing_small)
            ),
        style = MaterialTheme.typography.titleMedium,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(R.color.play_details_primary_text)
    )
    if (dependencies.isEmpty()) {
        Info(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_medium)),
            title = AnnotatedString(text = stringResource(R.string.details_no_dependencies))
        )
    } else {
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(items = dependencies, key = { item -> item.id }) { dependency ->
                AppListItem(
                    app = dependency,
                    onClick = { onNavigateTo(Destination.AppDetails(dependency.packageName)) }
                )
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview
@Composable
private fun MoreScreenPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    ScreenContent(app = app)
}
