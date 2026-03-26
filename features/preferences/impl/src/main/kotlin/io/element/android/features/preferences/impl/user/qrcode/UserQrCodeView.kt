/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.qrcode

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import kotlinx.coroutines.launch
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserProvider
import io.element.android.libraries.ui.strings.CommonStrings
import android.graphics.Bitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQrCodeView(
    state: UserQrCodeState,
    onBackClick: () -> Unit,
    onShareQrCode: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(
        enabled = true,
        onBack = onBackClick,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(CommonStrings.common_qr_code)
                    )
                },
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(0.6f))

            QrCodeShareCard(
                matrixUser = state.matrixUser,
                modifier = Modifier
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Share Button
            GradientButton(
                text = stringResource(CommonStrings.common_share_qr_code),
                onClick = {
                    coroutineScope.launch {
                        val imageBitmap = graphicsLayer.toImageBitmap()
                        onShareQrCode(imageBitmap.asAndroidBitmap())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                size = ButtonSize.Large,
                cornerRadius = 23.dp,
            )

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@PreviewsDayNight
@Composable
internal fun UserQrCodeViewPreview(
    @PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser
) = ElementPreview {
    UserQrCodeView(
        state = UserQrCodeState(
            matrixUser = matrixUser,
            eventSink = {}
        ),
        onBackClick = {},
        onShareQrCode = {}
    )
}
