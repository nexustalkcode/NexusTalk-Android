/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.qrcode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserProvider
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.qrcode.QrCodeImage

/** 分享卡片宽度 */
private val ShareCardWidth = 300.dp
/** 分享卡片高度 */
private val ShareCardHeight = 400.dp

/**
 * 二维码分享卡片组件
 *
 * 用于显示包含用户头像和二维码的分享卡片，可用于截图分享。
 *
 * @param matrixUser Matrix 用户信息
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeShareCard(
    matrixUser: MatrixUser,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(ShareCardWidth, ShareCardHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(ElementTheme.colors.bgCanvasDefault)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar
            Avatar(
                avatarData = matrixUser.getAvatarData(size = AvatarSize.EditProfileDetails),
                avatarType = AvatarType.User,
                modifier = Modifier.size(AvatarSize.EditProfileDetails.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // UserId
            Text(
                text = matrixUser.userId.value,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(0xFF4EF4BE))
            ) {
                QrCodeImage(
                    data = matrixUser.userId.value,
                    size = DpSize(180.dp, 180.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .padding(1.dp)

                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun QrCodeShareCardPreview(
    @PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser
) = ElementPreview {
    QrCodeShareCard(
        matrixUser = matrixUser
    )
}
