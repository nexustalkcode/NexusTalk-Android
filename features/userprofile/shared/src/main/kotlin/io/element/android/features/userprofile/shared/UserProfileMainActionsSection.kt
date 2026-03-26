/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.button.MainActionButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun UserProfileMainActionsSection(
    isCurrentUser: Boolean,
    canCall: Boolean,
    onShareUser: () -> Unit,
    onStartDM: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (!isCurrentUser) {
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
            ) {
                MainActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    title = stringResource(CommonStrings.action_message),
                    imageVector = CompoundIcons.Chat(),
                    onClick = onStartDM,
                    iconTint = ElementTheme.colors.textSecondary
                )
            }
        }
        if (canCall) {
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
            ) {
                MainActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    title = stringResource(CommonStrings.action_call),
                    imageVector = CompoundIcons.VideoCall(),
                    onClick = onCall,
                    iconTint = ElementTheme.colors.textSecondary
                )
            }
        }
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
        ) {
            MainActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                title = stringResource(CommonStrings.action_share),
                imageVector = CompoundIcons.ShareAndroid(),
                onClick = onShareUser,
                iconTint = ElementTheme.colors.textSecondary
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun UserProfileMainActionsSectionPreview() = ElementPreview {
    UserProfileMainActionsSection(
        isCurrentUser = false,
        canCall = true,
        onShareUser = {},
        onStartDM = {},
        onCall = {},
        modifier = Modifier.padding(vertical = 16.dp),
    )
}
