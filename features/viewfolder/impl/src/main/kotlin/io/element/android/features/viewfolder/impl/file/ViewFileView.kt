/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.async.AsyncFailure
import io.element.android.libraries.designsystem.components.async.AsyncLoading
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

/**
 * 文件查看视图组件
 *
 * 使用 Jetpack Compose 实现文件查看的用户界面。
 * 展示文本文件内容，支持保存和分享操作。
 *
 * @param state 当前视图状态
 * @param onBackClick 返回按钮点击回调
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFileView(
    state: ViewFileState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                titleStr = state.name,
                actions = {
                    IconButton(
                        onClick = {
                            state.eventSink(ViewFileEvents.Share)
                        },
                    ) {
                        Icon(
                            imageVector = CompoundIcons.ShareAndroid(),
                            contentDescription = stringResource(id = CommonStrings.action_share),
                        )
                    }
                    IconButton(
                        onClick = {
                            state.eventSink(ViewFileEvents.SaveOnDisk)
                        },
                    ) {
                        Icon(
                            imageVector = CompoundIcons.Download(),
                            contentDescription = stringResource(id = CommonStrings.action_save),
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                when (state.lines) {
                    AsyncData.Uninitialized,
                    is AsyncData.Loading -> AsyncLoading()
                    is AsyncData.Success -> FileContent(
                        modifier = Modifier.weight(1f),
                        lines = state.lines.data.toImmutableList(),
                        colorationMode = state.colorationMode,
                    )
                    is AsyncData.Failure -> AsyncFailure(throwable = state.lines.error, onRetry = null)
                }
            }
        }
    )
}

/**
 * 文件查看视图预览组件
 *
 * 用于在 Android Studio 预览中展示文件查看视图 UI。
 *
 * @param state 预览状态
 */
@PreviewsDayNight
@Composable
internal fun ViewFileViewPreview(@PreviewParameter(ViewFileStateProvider::class) state: ViewFileState) = ElementPreview {
    ViewFileView(
        state = state,
        onBackClick = {},
    )
}
