/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.viewmodel.commons.MoreViewModel

/**
 * Google Play–style home top bar: Play logo (start), notification bell and account avatar (end).
 * Logo inset matches [PlayStorePrimaryScrollableTabRow] so it lines up with the first tab label.
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
    val logoSize = dimensionResource(R.dimen.play_top_bar_logo_size)
    val actionIconSize = dimensionResource(R.dimen.play_top_bar_action_icon_size)
    val avatarSize = dimensionResource(R.dimen.play_top_bar_avatar_size)
    val contentInset = dimensionResource(R.dimen.play_home_content_inset)
    val actionsEndInset = dimensionResource(R.dimen.play_top_bar_actions_end_inset)
    val logoNudgeDown = dimensionResource(R.dimen.play_top_bar_logo_nudge_down)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorResource(R.color.play_tab_container)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.play_top_bar_content_height))
                .padding(start = contentInset),
            verticalAlignment = Alignment.Bottom
        ) {
            Image(
                painter = painterResource(R.drawable.ic_play_store_logo),
                contentDescription = stringResource(R.string.google_play_store_desc),
                modifier = Modifier
                    .offset(y = logoNudgeDown)
                    .padding(bottom = dimensionResource(R.dimen.spacing_xsmall))
                    .requiredSize(logoSize)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(end = actionsEndInset)
                    .offset(y = logoNudgeDown)
                    .padding(bottom = dimensionResource(R.dimen.spacing_xsmall)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_xsmall)
                )
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.requiredSize(actionIconSize + dimensionResource(R.dimen.spacing_medium)),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = colorResource(R.color.play_tab_unselected)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_notification),
                        contentDescription = stringResource(
                            R.string.onboarding_permission_notifications
                        ),
                        modifier = Modifier.requiredSize(actionIconSize)
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
                        .requiredSize(avatarSize)
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
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun PlayStoreTopBarPreview() {
    PlayStoreTopBar()
}
