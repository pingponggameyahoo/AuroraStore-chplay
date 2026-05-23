/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.data.model.AppState
import com.aurora.store.util.PackageUtil
import androidx.compose.ui.platform.LocalContext

@Composable
fun PlayStoreDetailsInstallActions(
    app: App,
    state: AppState,
    onInstall: () -> Unit,
    onCancelDownload: () -> Unit = {},
    onOpen: () -> Unit = {}
) {
    val context = LocalContext.current
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)

    AnimatedContent(
        targetState = state,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "PlayInstallButton"
    ) { currentState ->
        val (label, enabled, onClick) = when (currentState) {
            is AppState.Queued,
            is AppState.Purchasing,
            is AppState.Downloading -> Triple(
                stringResource(R.string.action_open),
                false,
                onCancelDownload
            )

            is AppState.Verifying,
            is AppState.Installing -> Triple(
                stringResource(R.string.action_installing),
                false,
                {}
            )

            is AppState.Updatable -> Triple(
                stringResource(R.string.action_update),
                true,
                onInstall
            )

            is AppState.Installed -> {
                val canOpen = PackageUtil.getLaunchIntent(context, app.packageName) != null
                Triple(stringResource(R.string.action_open), canOpen, onOpen)
            }

            is AppState.Archived -> Triple(
                stringResource(R.string.action_unarchive),
                true,
                onInstall
            )

            else -> {
                val primaryLabel = if (app.isFree) {
                    stringResource(R.string.action_install)
                } else {
                    app.price
                }
                Triple(primaryLabel, true, onInstall)
            }
        }

        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = dimensionResource(R.dimen.spacing_small),
                    bottom = dimensionResource(R.dimen.spacing_large)
                )
                .height(dimensionResource(R.dimen.play_details_install_button_height)),
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.play_details_install_button_corner)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.play_nav_selected),
                contentColor = Color.White,
                disabledContainerColor = colorResource(R.color.play_nav_selected).copy(alpha = 0.38f),
                disabledContentColor = Color.White.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlayDetailsSectionHeader(
    title: String,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)

    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = dimensionResource(R.dimen.spacing_large),
                bottom = dimensionResource(R.dimen.spacing_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.play_details_primary_text),
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.spacing_small))
        )
        PlayDetailsNavigateButton(onClick = onNavigate)
    }
}

@Composable
private fun PlayDetailsNavigateButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val buttonWidth = dimensionResource(R.dimen.play_details_nav_button_width)
    val buttonHeight = dimensionResource(R.dimen.play_details_nav_button_height)
    val iconSize = dimensionResource(R.dimen.play_details_nav_icon_size)
    val pillShape = RoundedCornerShape(percent = 50)

    Box(
        modifier = Modifier
            .width(buttonWidth)
            .height(buttonHeight)
            .clip(pillShape)
            .background(colorResource(R.color.play_details_nav_button_background))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = buttonHeight / 2
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play_details_nav_forward),
            contentDescription = stringResource(R.string.play_details_see_more_cd),
            modifier = Modifier.size(iconSize),
            tint = colorResource(R.color.play_details_primary_text)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayStoreDetailsAboutSection(
    app: App,
    onSeeMore: () -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val titleRes = if (app.isPlayGame()) {
        R.string.play_details_about_game
    } else {
        R.string.play_details_about_app
    }
    val description = app.shortDescription.trim().ifBlank { app.description.trim() }
    val genreChips = app.playDetailsGenreChips()

    Column(modifier = Modifier.fillMaxWidth()) {
        PlayDetailsSectionHeader(
            title = stringResource(titleRes),
            onNavigate = onSeeMore
        )

        if (description.isNotBlank()) {
            Text(
                text = description,
                modifier = Modifier.padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = dimensionResource(R.dimen.spacing_medium)
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(R.color.play_details_secondary_text),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (genreChips.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = dimensionResource(R.dimen.spacing_large)
                ),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                genreChips.forEach { chipLabel ->
                    PlayDetailsGenreChip(label = chipLabel)
                }
            }
        }
    }
}

@Composable
private fun PlayDetailsGenreChip(label: String) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.play_details_chip_border),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_small))
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            color = colorResource(R.color.play_details_primary_text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlayStoreDetailsDataSafetySection(onSeeMore: () -> Unit) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)

    Column(modifier = Modifier.fillMaxWidth()) {
        PlayDetailsSectionHeader(
            title = stringResource(R.string.play_details_data_safety_title),
            onNavigate = onSeeMore
        )
        Text(
            text = stringResource(R.string.play_details_data_safety_intro),
            modifier = Modifier.padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = dimensionResource(R.dimen.spacing_large)
            ),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorResource(R.color.play_details_secondary_text)
        )
        PlayDataSafetySummaryCard(onSeeDetails = onSeeMore)
    }
}

@Composable
private fun PlayDataSafetySummaryCard(onSeeDetails: () -> Unit) {
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val cardCorner = dimensionResource(R.dimen.play_details_data_safety_card_corner)
    val learnMore = stringResource(R.string.play_data_safety_learn_more)
    val iconTint = colorResource(R.color.play_details_secondary_text)
    val titleColor = colorResource(R.color.play_details_primary_text)
    val subtitleColor = colorResource(R.color.play_details_secondary_text)
    val linkColor = colorResource(R.color.play_nav_selected)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = dimensionResource(R.dimen.spacing_xlarge))
            .border(
                width = 1.dp,
                color = colorResource(R.color.play_details_chip_border),
                shape = RoundedCornerShape(cardCorner)
            )
            .padding(dimensionResource(R.dimen.spacing_large))
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            PlayDataSafetyCardRow(
                iconRes = R.drawable.ic_play_data_safety_share,
                title = stringResource(R.string.play_data_safety_no_share_title),
                iconTint = iconTint,
                titleColor = titleColor
            ) {
                PlayDataSafetyLearnMoreSubtitle(
                    fullText = stringResource(R.string.play_data_safety_share_subtitle, learnMore),
                    learnMore = learnMore,
                    color = subtitleColor
                )
            }
            PlayDataSafetyCardRow(
                iconRes = R.drawable.ic_play_data_safety_cloud_off,
                title = stringResource(R.string.play_data_safety_no_collect_title),
                iconTint = iconTint,
                titleColor = titleColor
            ) {
                PlayDataSafetyLearnMoreSubtitle(
                    fullText = stringResource(R.string.play_data_safety_collect_subtitle, learnMore),
                    learnMore = learnMore,
                    color = subtitleColor
                )
            }
            PlayDataSafetyCardRow(
                iconRes = R.drawable.ic_play_lock_outline,
                title = stringResource(R.string.play_data_safety_encrypted_title),
                iconTint = iconTint,
                titleColor = titleColor
            ) {
                Text(
                    text = stringResource(R.string.play_data_safety_encrypted_body),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = subtitleColor
                )
            }
            PlayDataSafetyCardRow(
                iconRes = R.drawable.ic_play_data_safety_no_delete,
                title = stringResource(R.string.play_data_safety_deletion_title),
                iconTint = iconTint,
                titleColor = titleColor
            )
        }
        Text(
            text = stringResource(R.string.play_data_safety_see_details),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.spacing_large))
                .clickable(onClick = onSeeDetails),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = linkColor
        )
    }
}

@Composable
private fun PlayDataSafetyCardRow(
    @DrawableRes iconRes: Int,
    title: String,
    iconTint: Color,
    titleColor: Color,
    subtitle: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 1.dp),
            tint = iconTint
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = titleColor
            )
            subtitle?.invoke()
        }
    }
}

@Composable
private fun PlayDataSafetyLearnMoreSubtitle(
    fullText: String,
    learnMore: String,
    color: Color
) {
    val annotated = buildAnnotatedString {
        val start = fullText.indexOf(learnMore)
        if (start >= 0) {
            append(fullText.substring(0, start))
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(learnMore)
            }
            append(fullText.substring(start + learnMore.length))
        } else {
            append(fullText)
        }
    }
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = color
    )
}
