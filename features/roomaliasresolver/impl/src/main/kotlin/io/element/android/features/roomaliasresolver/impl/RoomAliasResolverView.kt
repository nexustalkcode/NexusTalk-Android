/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.atomic.atoms.RoomPreviewSubtitleAtom
import io.element.android.libraries.designsystem.atomic.organisms.RoomPreviewOrganism
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.dialogs.RetryDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 房间别名解析器视图
 *
 * 负责渲染房间别名解析功能的用户界面。
 * 使用 Jetpack Compose 构建，包含加载状态、解析结果和错误处理等UI。
 *
 * @param state 解析器状态数据
 * @param onBackClick 返回按钮点击回调
 * @param onSuccess 解析成功回调，传入解析结果
 * @param modifier 视图修饰符
 *
 * @see RoomAliasResolverState 状态数据
 * @see RoomAliasResolverPresenter 业务逻辑
 */
@Composable
fun RoomAliasResolverView(
    state: RoomAliasResolverState,
    onBackClick: () -> Unit,
    onSuccess: (ResolvedRoomAlias) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        // 主页面内容
        HeaderFooterPage(
            containerColor = Color.Transparent,
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 32.dp
            ),
            topBar = {
                // 顶部导航栏
                RoomAliasResolverTopBar(onBackClick = onBackClick)
            },
            content = {
                // 解析内容区域
                RoomAliasResolverContent(roomAlias = state.roomAlias, isLoading = state.resolveState.isLoading())
            },
        )
        // 解析结果处理视图
        ResolvedRoomAliasView(
            resolvedRoomAlias = state.resolveState,
            onSuccess = onSuccess,
            onRetry = { state.eventSink(RoomAliasResolverEvents.Retry) },
            onDismissError = {
                state.eventSink(RoomAliasResolverEvents.DismissError)
                onBackClick()
            }
        )
    }
}

/**
 * 解析结果视图处理组件
 *
 * 根据解析状态（成功/失败）显示相应的 UI。
 * 成功时触发回调，失败时显示错误对话框。
 *
 * @param resolvedRoomAlias 解析状态数据
 * @param onSuccess 解析成功回调
 * @param onRetry 重试回调
 * @param onDismissError 关闭错误对话框回调
 */
@Composable
private fun ResolvedRoomAliasView(
    resolvedRoomAlias: AsyncData<ResolvedRoomAlias>,
    onSuccess: (ResolvedRoomAlias) -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
) {
    when (resolvedRoomAlias) {
        // 解析成功
        is AsyncData.Success -> {
            val latestOnSuccess by rememberUpdatedState(onSuccess)
            LaunchedEffect(Unit) {
                latestOnSuccess(resolvedRoomAlias.data)
            }
        }
        // 解析失败
        is AsyncData.Failure -> {
            // 判断是否为未知别名错误
            if (resolvedRoomAlias.error is RoomAliasResolverFailures.UnknownAlias) {
                // 显示未知别名错误对话框
                ErrorDialog(
                    title = stringResource(id = R.string.screen_join_room_loading_alert_title),
                    content = stringResource(id = R.string.screen_room_alias_resolver_resolve_alias_failure),
                    onSubmit = onDismissError
                )
            } else {
                // 显示网络或服务器错误对话框（可重试）
                RetryDialog(
                    title = stringResource(id = R.string.screen_join_room_loading_alert_title),
                    content = stringResource(id = CommonStrings.error_network_or_server_issue),
                    onRetry = onRetry,
                    onDismiss = onDismissError
                )
            }
        }
        // 其他状态（加载中或未初始化），不显示任何内容
        else -> Unit
    }
}

/**
 * 解析内容区域组件
 *
 * 显示房间预览信息，包含头像占位符和房间别名。
 * 加载状态时显示加载指示器。
 *
 * @param roomAlias 房间别名
 * @param isLoading 是否处于加载状态
 * @param modifier 视图修饰符
 */
@Composable
private fun RoomAliasResolverContent(
    roomAlias: RoomAlias,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    RoomPreviewOrganism(
        modifier = modifier,
        avatar = {
            // 头像占位符
            PlaceholderAtom(width = AvatarSize.RoomPreviewHeader.dp, height = AvatarSize.RoomPreviewHeader.dp)
        },
        title = {
            // 房间别名作为标题显示
            RoomPreviewSubtitleAtom(roomAlias.value)
        },
        subtitle = {
            // 加载状态显示加载指示器
            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
        }
    )
}

/**
 * 顶部导航栏组件
 *
 * 包含返回按钮的顶部应用栏。
 *
 * @param onBackClick 返回按钮点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomAliasResolverTopBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            // 返回按钮
            BackButton(onClick = onBackClick)
        },
        title = {},
    )
}

/**
 * 视图预览组合函数
 *
 * 用于在预览模式下显示 RoomAliasResolverView 的不同状态。
 *
 * @param state 预览参数，由 RoomAliasResolverStateProvider 提供
 */
@PreviewsDayNight
@Composable
internal fun RoomAliasResolverViewPreview(@PreviewParameter(RoomAliasResolverStateProvider::class) state: RoomAliasResolverState) = ElementPreview {
    RoomAliasResolverView(
        state = state,
        onSuccess = { },
        onBackClick = { }
    )
}
