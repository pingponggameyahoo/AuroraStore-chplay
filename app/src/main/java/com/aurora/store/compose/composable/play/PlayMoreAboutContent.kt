/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.play

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.gplayapi.data.models.App
import com.aurora.store.R
import com.aurora.store.compose.composable.app.AnimatedAppIcon

@Composable
fun PlayMoreAboutToolbarTitle(app: App, modifier: Modifier = Modifier) {
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedAppIcon(
            modifier = Modifier.size(40.dp),
            iconUrl = app.iconArtwork.url,
            inProgress = false,
            progress = 0f
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = app.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = primaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.play_more_subtitle),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlayMoreAboutSections(
    app: App,
    onSeePermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dividerColor = colorResource(R.color.play_details_chip_border)
    val horizontalPadding = dimensionResource(R.dimen.play_more_section_horizontal_padding)
    val infoRows = remember(app) { app.playMoreInfoRows(context) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (app.changes.isNotBlank()) {
            PlayMoreAboutWhatsNew(
                changelog = app.changes,
                horizontalPadding = horizontalPadding
            )
            PlayMoreSectionDivider(dividerColor)
        }

        PlayMoreAboutOtherInfo(
            app = app,
            horizontalPadding = horizontalPadding
        )
        PlayMoreSectionDivider(dividerColor)

        PlayMoreAboutInfoTable(
            title = if (app.isPlayGame()) {
                stringResource(R.string.play_more_game_info)
            } else {
                stringResource(R.string.play_more_app_info)
            },
            rows = infoRows,
            permissionsAction = onSeePermissions,
            horizontalPadding = horizontalPadding
        )
        PlayMoreSectionDivider(dividerColor)

        PlayMoreAboutDeviceCompatibility(
            app = app,
            horizontalPadding = horizontalPadding
        )
    }
}

@Composable
private fun PlayMoreAboutWhatsNew(changelog: String, horizontalPadding: androidx.compose.ui.unit.Dp) {
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val linkColor = colorResource(R.color.play_nav_selected)
    val sectionTop = dimensionResource(R.dimen.play_more_section_title_top)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(top = sectionTop, bottom = dimensionResource(R.dimen.spacing_large))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.play_more_whats_new),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(linkColor)
            )
        }
        Text(
            text = AnnotatedString.fromHtml(htmlString = changelog),
            modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_medium)),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = secondaryText
        )
    }
}

@Composable
private fun PlayMoreAboutOtherInfo(app: App, horizontalPadding: androidx.compose.ui.unit.Dp) {
    val context = LocalContext.current
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val linkColor = colorResource(R.color.play_nav_selected)
    val sectionTop = dimensionResource(R.dimen.play_more_section_title_top)
    val ageBadge = app.playAgeRatingBadge()
    val ageSubtitle = app.playAgeRatingSubtitle(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(top = sectionTop, bottom = dimensionResource(R.dimen.spacing_large))
    ) {
        Text(
            text = stringResource(R.string.play_more_other_info),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText
        )
        Row(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_large)),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.play_details_chip_border),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ageBadge,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = ageSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = primaryText
                )
                Text(
                    text = stringResource(R.string.play_data_safety_learn_more),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = linkColor
                )
            }
        }
    }
}

@Composable
private fun PlayMoreAboutInfoTable(
    title: String,
    rows: List<PlayMoreInfoRow>,
    permissionsAction: () -> Unit,
    horizontalPadding: androidx.compose.ui.unit.Dp
) {
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val linkColor = colorResource(R.color.play_nav_selected)
    val rowPadding = dimensionResource(R.dimen.play_more_row_vertical_padding)
    val sectionTop = dimensionResource(R.dimen.play_more_section_title_top)
    val seeMore = stringResource(R.string.play_more_see_more)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = sectionTop, bottom = dimensionResource(R.dimen.spacing_large))
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = horizontalPadding),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText
        )
        rows.forEach { row ->
            PlayMoreInfoRowItem(
                label = row.label,
                value = row.value,
                valueColor = primaryText,
                labelColor = secondaryText,
                rowPadding = rowPadding,
                horizontalPadding = horizontalPadding
            )
        }
        PlayMoreInfoRowItem(
            label = stringResource(R.string.play_more_permissions),
            value = seeMore,
            valueColor = linkColor,
            labelColor = secondaryText,
            rowPadding = rowPadding,
            horizontalPadding = horizontalPadding,
            onValueClick = permissionsAction
        )
    }
}

@Composable
private fun PlayMoreInfoRowItem(
    label: String,
    value: String,
    valueColor: Color,
    labelColor: Color,
    rowPadding: androidx.compose.ui.unit.Dp,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    onValueClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onValueClick != null) {
                    Modifier.clickable(onClick = onValueClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding, vertical = rowPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = labelColor
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = valueColor,
            fontWeight = if (onValueClick != null) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PlayMoreAboutDeviceCompatibility(
    app: App,
    horizontalPadding: androidx.compose.ui.unit.Dp
) {
    val primaryText = colorResource(R.color.play_details_primary_text)
    val secondaryText = colorResource(R.color.play_details_secondary_text)
    val rowPadding = dimensionResource(R.dimen.play_more_row_vertical_padding)
    val sectionTop = dimensionResource(R.dimen.play_more_section_title_top)
    var expanded by remember { mutableStateOf(true) }

    val deviceName = remember {
        listOfNotNull(Build.MANUFACTURER, Build.MODEL)
            .joinToString(" ")
            .trim()
            .ifBlank { Build.MODEL }
    }
    val version = app.versionName
    val downloadSize = app.playFileSizeLabel()
    val requiredOs = app.playMoreAndroidRequirement()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = sectionTop, bottom = dimensionResource(R.dimen.spacing_xlarge))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = rowPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.play_more_device_compatibility),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = secondaryText
            )
            Icon(
                painter = painterResource(R.drawable.ic_play_info_outline),
                contentDescription = stringResource(R.string.play_details_info_icon_cd),
                modifier = Modifier.size(18.dp),
                tint = secondaryText
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = horizontalPadding, vertical = rowPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.play_more_device_this_device, deviceName),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = primaryText
            )
            Icon(
                painter = painterResource(
                    if (expanded) R.drawable.ic_keyboard_arrow_up else R.drawable.ic_keyboard_arrow_down
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = secondaryText
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                PlayMoreInfoRowItem(
                    label = stringResource(R.string.play_more_compatibility),
                    value = stringResource(R.string.play_more_compatible),
                    valueColor = primaryText,
                    labelColor = secondaryText,
                    rowPadding = rowPadding,
                    horizontalPadding = horizontalPadding
                )
                if (version.isNotBlank()) {
                    PlayMoreInfoRowItem(
                        label = stringResource(R.string.play_more_version),
                        value = version,
                        valueColor = primaryText,
                        labelColor = secondaryText,
                        rowPadding = rowPadding,
                        horizontalPadding = horizontalPadding
                    )
                }
                if (downloadSize.isNotBlank()) {
                    PlayMoreInfoRowItem(
                        label = stringResource(R.string.play_more_download_size),
                        value = downloadSize,
                        valueColor = primaryText,
                        labelColor = secondaryText,
                        rowPadding = rowPadding,
                        horizontalPadding = horizontalPadding
                    )
                }
                requiredOs?.let { os ->
                    PlayMoreInfoRowItem(
                        label = stringResource(R.string.play_more_required_os),
                        value = os,
                        valueColor = primaryText,
                        labelColor = secondaryText,
                        rowPadding = rowPadding,
                        horizontalPadding = horizontalPadding
                    )
                }
            }
        }
    }
}

@Composable
fun PlayMoreSectionDivider(color: Color) {
    HorizontalDivider(color = color)
}
