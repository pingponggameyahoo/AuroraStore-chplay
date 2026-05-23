/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.viewmodel.commons.MoreViewModel

/**
 * Google Play–style home top bar: Play logo (start), notification bell and account avatar (end).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayStoreTopBar(
    modifier: Modifier = Modifier,
    viewModel: MoreViewModel = hiltViewModel(),
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val profileUrl = viewModel.authProvider.authData?.userProfile?.artwork?.url

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorResource(R.color.play_tab_container)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.spacing_medium),
                    end = dimensionResource(R.dimen.spacing_small)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_play_store_logo),
                contentDescription = stringResource(R.string.google_play_store_desc),
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.spacing_medium))
                    .requiredSize(dimensionResource(R.dimen.icon_size_default))
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_notification),
                    contentDescription = stringResource(R.string.onboarding_permission_notifications),
                    tint = colorResource(R.color.play_tab_unselected)
                )
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profileUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.manage_account),
                placeholder = painterResource(R.drawable.ic_account),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.spacing_small))
                    .requiredSize(32.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAvatarClick
                    )
                    .semantics { role = Role.Button }
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun PlayStoreTopBarPreview() {
    PlayStoreTopBar()
}
