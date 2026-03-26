/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.getBestName

@Composable
fun MatrixUserHeader(
    matrixUser: MatrixUser?,
    modifier: Modifier = Modifier,
    onQrCodeClick: (() -> Unit)? = null,
    showChevron: Boolean = false,
    avatarSize: Dp? = null,
    // TODO handle click on this item, to let the user be able to update their profile.
    // onClick: () -> Unit,
) {
    if (matrixUser == null) {
        MatrixUserHeaderPlaceholder(modifier = modifier, avatarSize = avatarSize)
    } else {
        MatrixUserHeaderContent(
            matrixUser = matrixUser,
            modifier = modifier,
            onQrCodeClick = onQrCodeClick,
            showChevron = showChevron,
            avatarSize = avatarSize,
            // onClick = onClick
        )
    }
}

@Composable
private fun MatrixUserHeaderContent(
    matrixUser: MatrixUser,
    modifier: Modifier = Modifier,
    onQrCodeClick: (() -> Unit)? = null,
    showChevron: Boolean = false,
    avatarSize: Dp? = null,
    // onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            // .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            modifier = Modifier
                .padding(vertical = 12.dp),
            avatarData = matrixUser.getAvatarData(size = AvatarSize.UserPreference),
            avatarType = AvatarType.User,
            forcedAvatarSize = avatarSize,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                modifier = Modifier.clipToBounds(),
                text = matrixUser.getBestName(),
                maxLines = 1,
                style = ElementTheme.typography.fontHeadingSmMedium,
                overflow = TextOverflow.Ellipsis,
                color = ElementTheme.colors.textPrimary,
            )
            // Id
            if (matrixUser.displayName.isNullOrEmpty().not()) {
                Text(
                    text = matrixUser.userId.value,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (onQrCodeClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onQrCodeClick),
                imageVector = CompoundIcons.QrCode(),
                contentDescription = "二维码",
                tint = ElementTheme.colors.iconSecondary,
            )
        }
        if (showChevron) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = CompoundIcons.ChevronRight(),
                contentDescription = null,
                tint = ElementTheme.colors.iconSecondary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun MatrixUserHeaderPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) = ElementPreview {
    MatrixUserHeader(matrixUser)
}
