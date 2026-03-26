/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package io.element.android.features.linknewdevice.impl.screens.qrcode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.linknewdevice.impl.R
import io.element.android.libraries.designsystem.atomic.organisms.NumberedListOrganism
import io.element.android.libraries.designsystem.atomic.pages.FlowStepPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.LocalBuildMeta
import io.element.android.libraries.designsystem.utils.annotatedTextWithBold
import io.element.android.libraries.qrcode.QrCodeImage
import kotlinx.collections.immutable.persistentListOf

/**
 * QrCode display screen:
 * https://www.figma.com/design/pDlJZGBsri47FNTXMnEdXB/Compound-Android-Templates?node-id=2027-23617
 */
@Composable
fun ShowQrCodeView(
    data: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 读取应用名，用于动态文案
    val appName = LocalBuildMeta.current.applicationName
    FlowStepPage(
        onBackClick = onBackClick,
        title = stringResource(R.string.screen_link_new_device_mobile_title, appName),
        iconStyle = BigIcon.Style.Default(CompoundIcons.TakePhotoSolid()),
        modifier = modifier,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QrCodeImage(
                data = data,
                // 固定尺寸二维码，保证可扫性
                size = DpSize(220.dp,220.dp),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(32.dp))
            NumberedListOrganism(
                modifier = Modifier.fillMaxSize(),
                items = persistentListOf(
                    // 步骤 1：在桌面端操作
                    AnnotatedString(stringResource(R.string.screen_link_new_device_mobile_step1, appName)),
                    // 步骤 2：强调可点击的动作文案
                    annotatedTextWithBold(
                        text = stringResource(
                            id = R.string.screen_link_new_device_mobile_step2,
                            stringResource(R.string.screen_link_new_device_mobile_step2_action),
                        ),
                        boldText = stringResource(R.string.screen_link_new_device_mobile_step2_action)
                    ),
                    // 步骤 3：完成提示
                    AnnotatedString(stringResource(R.string.screen_link_new_device_mobile_step3)),
                )
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ShowQrCodeViewPreview() = ElementPreview {
    ShowQrCodeView(
        data = "DATA",
        onBackClick = { },
    )
}
