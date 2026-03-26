/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.startchat.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.modifiers.cornerBorder
import io.element.android.libraries.designsystem.modifiers.squareSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.permissions.api.PermissionsView
import io.element.android.libraries.qrcode.QrCodeCameraView
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 扫描用户二维码视图
 *
 * @param state 扫描用户二维码状态
 * @param onBackClick 返回点击事件
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanUserQrCodeView(
    state: ScanUserQrCodeState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onBackClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Text(
                text = stringResource(R.string.screen_scan_user_qr_code_title),
                style = ElementTheme.typography.fontHeadingLgBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            // Subtitle
            Text(
                text = stringResource(R.string.screen_scan_user_qr_code_subtitle),
                style = ElementTheme.typography.fontBodyMdRegular,
                textAlign = TextAlign.Center,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            // Camera preview
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val modifier = if (constraints.maxWidth > constraints.maxHeight) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.fillMaxWidth()
                }.then(
                    Modifier
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .squareSize()
                        .cornerBorder(
                            strokeWidth = 4.dp,
                            color = ElementTheme.colors.textPrimary,
                            cornerSizeDp = 42.dp,
                        )
                )
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    QrCodeCameraView(
                        modifier = Modifier.fillMaxSize(),
                        onScanQrCode = { bytes -> state.eventSink(ScanUserQrCodeEvents.QrCodeScanned(String(bytes))) },
                        isScanning = state.scanAction.isLoading(),
                    )
                    if (state.cameraPermissionState.permissionGranted.not()) {
                        Button(
                            text = stringResource(id = CommonStrings.action_continue),
                            onClick = { state.eventSink(ScanUserQrCodeEvents.RequestCameraPermission) },
                        )
                    }
                }
            }

            // Status/Error display
            ScanStatus(
                state = state,
                onTryAgain = { state.eventSink(ScanUserQrCodeEvents.TryAgain) },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    PermissionsView(state = state.cameraPermissionState)
}

@Composable
private fun ColumnScope.ScanStatus(
    state: ScanUserQrCodeState,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (val action = state.scanAction) {
            is AsyncAction.Failure -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = CompoundIcons.ErrorSolid(),
                        tint = ElementTheme.colors.iconCriticalPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = action.error.message ?: stringResource(CommonStrings.error_unknown),
                        textAlign = TextAlign.Center,
                        color = ElementTheme.colors.textCriticalPrimary,
                        style = ElementTheme.typography.fontBodyMdMedium,
                    )
                }
                Button(
                    text = stringResource(id = CommonStrings.action_try_again),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    onClick = onTryAgain,
                )
            }
            is AsyncAction.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = CompoundIcons.Verified(),
                        tint = ElementTheme.colors.iconSuccessPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.screen_scan_user_qr_code_success),
                        textAlign = TextAlign.Center,
                        style = ElementTheme.typography.fontBodyMdMedium,
                    )
                }
            }
            AsyncAction.Loading,
            AsyncAction.Uninitialized,
            is AsyncAction.Confirming -> {
                // Show nothing when loading or uninitialized
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ScanUserQrCodeViewPreview() = ElementPreview {
    ScanUserQrCodeView(
        state = ScanUserQrCodeState(),
        onBackClick = {},
    )
}
