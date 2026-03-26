/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.addpeople

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.createroom.impl.R
import io.element.android.features.invitepeople.api.InvitePeopleEvents
import io.element.android.features.invitepeople.api.InvitePeopleState
import io.element.android.features.invitepeople.api.InvitePeopleStateProvider
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 添加人员视图
 *
 * 创建房间流程中"添加人员"步骤的 Compose 视图。
 * 展示邀请人员界面，包含顶部导航栏、内容区域和底部完成按钮。
 *
 * @param state 邀请人员状态，包含搜索结果、选中用户等信息
 * @param onFinish 完成回调，点击完成按钮时调用
 * @param modifier 视图修饰符，用于自定义样式和布局
 * @param invitePeopleView 邀请人员视图的 Composable 表达式，用于渲染搜索和选择用户的界面
 */
@Composable
fun AddPeopleView(
    state: InvitePeopleState,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    invitePeopleView: @Composable () -> Unit,
) {
    HeaderFooterPage(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        topBar = {
            AddPeopleTopBar(onSkipClick = onFinish)
        },
        footer = {
            Button(
                text = stringResource(CommonStrings.action_finish),
                onClick = {
                    state.eventSink(InvitePeopleEvents.SendInvites)
                    onFinish()
                },
                enabled = state.canInvite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        },
        content = invitePeopleView
    )
}

/**
 * 添加人员顶部导航栏
 *
 * 显示在添加人员页面顶部的导航栏，包含标题和跳过按钮。
 *
 * @param onSkipClick 跳过按钮点击回调，点击后跳过添加人员步骤
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPeopleTopBar(
    onSkipClick: () -> Unit,
) {
    TopAppBar(
        titleStr = stringResource(R.string.screen_create_room_add_people_title),
        actions = {
            TextButton(
                text = stringResource(CommonStrings.action_skip),
                onClick = onSkipClick,
            )
        }
    )
}

/**
 * 添加人员视图预览
 *
 * 用于在预览模式下展示添加人员视图的效果。
 *
 * @param state 邀请人员状态，提供预览所需的测试数据
 */
@PreviewsDayNight
@Composable
internal fun AddPeopleViewPreview(@PreviewParameter(InvitePeopleStateProvider::class) state: InvitePeopleState) = ElementPreview {
    AddPeopleView(
        state = state,
        invitePeopleView = {},
        onFinish = {},
    )
}
