/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.spaces

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.announcement.impl.R
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.organisms.InfoListItem
import io.element.android.libraries.designsystem.atomic.organisms.InfoListOrganism
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.persistentListOf

/**
 * 空间公告视图
 *
 * 渲染空间功能的引导介绍页面，展示新功能的特性和使用方法。
 * 包含标题、副标题、功能列表说明以及继续按钮。
 * 使用 Figma 设计稿作为参考实现。
 *
 * @param state 空间公告状态，包含事件处理函数
 * @param modifier 修饰符，用于控制布局和行为
 * @see SpaceAnnouncementState 空间公告状态
 * @see <a href="https://www.figma.com/design/kcnHxunG1LDWXsJhaNuiHz/ER-145--Spaces-on-Element-X?node-id=4593-40181">Figma 设计稿</a>
 */
@Composable
fun SpaceAnnouncementView(
    state: SpaceAnnouncementState,
    modifier: Modifier = Modifier,
) {
    val eventSink = state.eventSink

    fun onContinue() {
        eventSink(SpaceAnnouncementEvents.Continue)
    }

    BackHandler(onBack = ::onContinue)
    HeaderFooterPage(
        modifier = modifier,
        isScrollable = true,
        contentPadding = PaddingValues(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
        header = {
            SpaceAnnouncementHeader()
        },
        content = {
            SpaceAnnouncementContent(
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        },
        footer = {
            SpaceAnnouncementFooter(
                onContinue = ::onContinue,
            )
        }
    )
}

/**
 * 空间公告页面头部组件
 *
 * 渲染公告页面的标题区域，包含图标、标题和副标题。
 * 使用 IconTitleSubtitleMolecule 分子组件实现，支持显示 Beta 标签。
 *
 * @param modifier 修饰符，用于控制布局
 * @see io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
 */
@Composable
private fun SpaceAnnouncementHeader(
    modifier: Modifier = Modifier,
) {
    IconTitleSubtitleMolecule(
        modifier = modifier.padding(top = 16.dp, bottom = 16.dp),
        title = stringResource(id = R.string.screen_space_announcement_title),
        showBetaLabel = true,
        subTitle = stringResource(id = R.string.screen_space_announcement_subtitle),
        iconStyle = BigIcon.Style.Default(
            vectorIcon = CompoundIcons.SpaceSolid(),
            usePrimaryTint = true,
        ),
    )
}

/**
 * 空间公告内容组件
 *
 * 渲染公告页面的主体内容区域，包含功能列表和提示说明。
 * 使用 InfoListOrganism 展示空间功能的五个核心特性：
 * 1. 可见性控制
 * 2. 邮件通知
 * 3. 搜索功能
 * 4. 探索功能
 * 5. 离开功能
 * 底部包含一条注意事项说明文字。
 *
 * @param modifier 修饰符，用于控制布局
 * @see io.element.android.libraries.designsystem.atomic.organisms.InfoListOrganism
 */
@Composable
private fun SpaceAnnouncementContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        InfoListOrganism(
            modifier = Modifier.fillMaxWidth(),
            items = persistentListOf(
                InfoListItem(
                    message = stringResource(id = R.string.screen_space_announcement_item1),
                    iconVector = CompoundIcons.VisibilityOn(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_space_announcement_item2),
                    iconVector = CompoundIcons.Email(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_space_announcement_item3),
                    iconVector = CompoundIcons.Search(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_space_announcement_item4),
                    iconVector = CompoundIcons.Explore(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_space_announcement_item5),
                    iconVector = CompoundIcons.Leave(),
                ),
            ),
            textStyle = ElementTheme.typography.fontBodyLgMedium,
            iconTint = ElementTheme.colors.iconSecondary,
            iconSize = 24.dp
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            text = stringResource(id = R.string.screen_space_announcement_notice),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 空间公告底部组件
 *
 * 渲染公告页面的底部区域，包含继续按钮。
 * 用户点击继续按钮后，会关闭公告页面并标记为已阅读。
 *
 * @param onContinue 继续按钮点击回调函数
 * @see io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
 * @see io.element.android.libraries.designsystem.theme.components.Button 按钮组件
 */
@Composable
private fun SpaceAnnouncementFooter(
    /** 继续按钮点击回调函数 */
    onContinue: () -> Unit,
) {
    ButtonColumnMolecule(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Button(
            text = stringResource(id = CommonStrings.action_continue),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * 空间公告视图预览
 *
 * 使用 PreviewsDayNight 注解提供日夜两种主题的预览效果。
 * 通过 SpaceAnnouncementStateProvider 提供示例状态数据。
 *
 * @param state 空间公告状态，由 PreviewParameterProvider 自动注入
 * @see SpaceAnnouncementStateProvider 预览参数提供器
 * @see io.element.android.libraries.designsystem.preview.PreviewsDayNight 日夜预览注解
 */
@PreviewsDayNight
@Composable
internal fun SpaceAnnouncementViewPreview(@PreviewParameter(SpaceAnnouncementStateProvider::class) state: SpaceAnnouncementState) = ElementPreview {
    SpaceAnnouncementView(
        state = state,
    )
}
