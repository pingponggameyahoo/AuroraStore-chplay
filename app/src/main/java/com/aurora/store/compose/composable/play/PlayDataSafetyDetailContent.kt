/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.extensions.browse
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.compose.composable.app.AnimatedAppIcon

@Composable
fun PlayDataSafetyDetailContent(
    app: App,
    privacyPolicyUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenBackground = colorResource(R.color.play_data_safety_screen_background)
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val dividerColor = colorResource(R.color.play_details_chip_border)
    val horizontalPadding = dimensionResource(R.dimen.play_details_section_horizontal_padding)
    val cardCorner = dimensionResource(R.dimen.play_details_data_safety_card_corner)
    val sheetTopCorner = dimensionResource(R.dimen.play_data_safety_sheet_top_corner)
    val learnMore = stringResource(R.string.play_data_safety_learn_more)
    val privacyPolicy = stringResource(R.string.play_data_safety_privacy_policy)
    val openPrivacyPolicy = {
        if (privacyPolicyUrl.isNotBlank()) {
            context.browse(privacyPolicyUrl)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(screenBackground)
    ) {
        PlayDataSafetyDetailAppHeader(
            app = app,
            primaryText = primaryText,
            secondaryText = secondaryText,
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = dimensionResource(R.dimen.spacing_medium)
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(bottom = dimensionResource(R.dimen.spacing_medium))
                .clip(RoundedCornerShape(cardCorner))
                .background(Color.White)
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shield),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 1.dp),
                tint = secondaryText
            )
            Text(
                text = stringResource(R.string.play_data_safety_detail_developer_info),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = sheetTopCorner,
                        topEnd = sheetTopCorner
                    )
                )
                .background(Color.White)
                .padding(bottom = dimensionResource(R.dimen.spacing_xlarge))
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = dimensionResource(R.dimen.spacing_large)
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                Text(
                    text = stringResource(R.string.play_details_data_safety_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
                PlayDataSafetyUnderlinedLinkText(
                    fullText = stringResource(
                        R.string.play_data_safety_detail_summary,
                        learnMore
                    ),
                    linkText = learnMore,
                    color = secondaryText,
                    onLinkClick = openPrivacyPolicy
                )
            }
            HorizontalDivider(color = dividerColor)
            PlayDataSafetyDetailMainSection(
                iconRes = R.drawable.ic_play_data_safety_share,
                title = stringResource(R.string.play_data_safety_no_share_title),
                primaryText = primaryText,
                secondaryText = secondaryText,
                horizontalPadding = horizontalPadding
            ) {
                PlayDataSafetyUnderlinedLinkText(
                    fullText = stringResource(
                        R.string.play_data_safety_detail_no_share_body,
                        learnMore
                    ),
                    linkText = learnMore,
                    color = secondaryText,
                    onLinkClick = openPrivacyPolicy
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
                color = dividerColor
            )
            PlayDataSafetyDetailMainSection(
                iconRes = R.drawable.ic_play_data_safety_cloud_off,
                title = stringResource(R.string.play_data_safety_no_collect_title),
                primaryText = primaryText,
                secondaryText = secondaryText,
                horizontalPadding = horizontalPadding
            ) {
                Text(
                    text = stringResource(R.string.play_data_safety_detail_no_collect_body),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = secondaryText
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
                color = dividerColor
            )
            PlayDataSafetyDetailMainSection(
                iconRes = R.drawable.ic_play_lock_outline,
                title = stringResource(R.string.play_data_safety_security_title),
                primaryText = primaryText,
                secondaryText = secondaryText,
                horizontalPadding = horizontalPadding
            ) {
                PlayDataSafetyDetailSubRow(
                    iconRes = R.drawable.ic_play_lock_outline,
                    title = stringResource(R.string.play_data_safety_encrypted_title),
                    primaryText = primaryText,
                    secondaryText = secondaryText
                ) {
                    Text(
                        text = stringResource(R.string.play_data_safety_encrypted_body),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = secondaryText
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
                color = dividerColor
            )
            PlayDataSafetyDetailMainSection(
                iconRes = R.drawable.ic_play_delete_outline,
                title = stringResource(R.string.play_data_safety_deletion_section_title),
                primaryText = primaryText,
                secondaryText = secondaryText,
                horizontalPadding = horizontalPadding
            ) {
                PlayDataSafetyUnderlinedLinkText(
                    fullText = stringResource(
                        R.string.play_data_safety_deletion_intro,
                        learnMore
                    ),
                    linkText = learnMore,
                    color = secondaryText,
                    onLinkClick = openPrivacyPolicy
                )
                PlayDataSafetyDetailSubRow(
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_large)),
                    iconRes = R.drawable.ic_play_data_safety_no_delete,
                    title = stringResource(R.string.play_data_safety_deletion_no_info_title),
                    primaryText = primaryText,
                    secondaryText = secondaryText
                ) {
                    PlayDataSafetyUnderlinedLinkText(
                        fullText = stringResource(
                            R.string.play_data_safety_deletion_no_info_body,
                            privacyPolicy
                        ),
                        linkText = privacyPolicy,
                        color = secondaryText,
                        onLinkClick = openPrivacyPolicy
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
                color = dividerColor
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_info_outline),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp),
                    tint = secondaryText
                )
                PlayDataSafetyUnderlinedLinkText(
                    fullText = stringResource(
                        R.string.play_data_safety_footer,
                        privacyPolicy
                    ),
                    linkText = privacyPolicy,
                    color = secondaryText,
                    onLinkClick = openPrivacyPolicy
                )
            }
        }
    }
}

@Composable
private fun PlayDataSafetyDetailAppHeader(
    app: App,
    primaryText: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedAppIcon(
            modifier = Modifier.size(48.dp),
            iconUrl = app.iconArtwork.url,
            inProgress = false,
            progress = 0f
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = app.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText,
                maxLines = 2
            )
            Text(
                text = app.developerName,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PlayDataSafetyDetailMainSection(
    @DrawableRes iconRes: Int,
    title: String,
    primaryText: Color,
    secondaryText: Color,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    val iconCircleSize = dimensionResource(R.dimen.play_data_safety_icon_circle_size)
    val iconCircleBackground = colorResource(R.color.play_data_safety_icon_circle_background)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        Box(
            modifier = Modifier
                .size(iconCircleSize)
                .clip(CircleShape)
                .background(iconCircleBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = secondaryText
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText
        )
        content()
    }
}

@Composable
private fun PlayDataSafetyDetailSubRow(
    @DrawableRes iconRes: Int,
    title: String,
    primaryText: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier,
    body: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 1.dp),
            tint = secondaryText
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
                fontWeight = FontWeight.Bold,
                color = primaryText
            )
            body()
        }
    }
}

@Composable
private fun PlayDataSafetyUnderlinedLinkText(
    fullText: String,
    linkText: String,
    color: Color,
    onLinkClick: () -> Unit
) {
    val annotated = buildAnnotatedString {
        val start = fullText.indexOf(linkText)
        if (start >= 0) {
            append(fullText.substring(0, start))
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(linkText)
            }
            append(fullText.substring(start + linkText.length))
        } else {
            append(fullText)
        }
    }
    Text(
        text = annotated,
        modifier = Modifier.clickable(onClick = onLinkClick),
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = color
    )
}
