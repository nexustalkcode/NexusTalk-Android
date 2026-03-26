/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.theme.ForcedDarkElementTheme
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.mediaviewer.api.local.LocalMediaRenderer

/**
 * 附件预览节点
 *
 * 用于显示附件预览界面的 Appyx Node。
 * 负责创建和管理附件预览功能的视图和业务逻辑。
 *
 * 主要功能：
 * - 显示附件（图片/视频）的预览界面
 * - 提供媒体优化选择器
 * - 处理附件发送流程
 *
 * @property buildContext 构建上下文，包含节点构建所需的信息
 * @property plugins 插件列表，用于扩展节点功能
 * @property presenterFactory 附件预览Presenter工厂，用于创建Presenter实例
 * @property localMediaRenderer 本地媒体渲染器，用于渲染媒体内容
 * @property sessionId 会话ID，用于标识当前用户
 * @property enterpriseService 企业服务，用于获取企业主题色
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class AttachmentsPreviewNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: AttachmentsPreviewPresenter.Factory,
    private val localMediaRenderer: LocalMediaRenderer,
    private val sessionId: SessionId,
    private val enterpriseService: EnterpriseService,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入数据类
     *
     * 定义附件预览节点所需的输入参数
     *
     * @property attachment 要预览的附件对象
     * @property timelineMode 时间线模式，决定媒体发送的目标
     * @property inReplyToEventId 回复的消息事件ID（如果是对某条消息的回复）
     */
    data class Inputs(
        val attachment: Attachment,
        val timelineMode: Timeline.Mode,
        val inReplyToEventId: EventId?,
    ) : NodeInputs

    /**
     * 获取节点输入数据
     */
    private val inputs: Inputs = inputs()

    /**
     * 完成监听器
     *
     * 当预览流程完成（发送成功或取消）时调用，用于导航回上一页面
     */
    private val onDoneListener = OnDoneListener {
        navigateUp()
    }

    /**
     * 创建Presenter实例
     *
     * 使用工厂方法创建附件预览Presenter，并传入所需参数
     */
    private val presenter = presenterFactory.create(
        attachment = inputs.attachment,
        timelineMode = inputs.timelineMode,
        onDoneListener = onDoneListener,
        inReplyToEventId = inputs.inReplyToEventId,
    )

    /**
     * 创建视图
     *
     * Compose Composable函数，用于渲染附件预览界面。
     * 使用强制深色主题来确保预览界面的一致性显示。
     *
     * @param modifier 视图修饰符，用于配置布局和样式
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 获取企业主题色
        val colors by remember {
            enterpriseService.semanticColorsFlow(sessionId = sessionId)
        }.collectAsState(SemanticColorsLightDark.default)
        // 强制使用深色主题
        ForcedDarkElementTheme(
            colors = colors,
        ) {
            // 获取Presenter生成的状态
            val state = presenter.present()
            // 渲染附件预览视图
            AttachmentsPreviewView(
                state = state,
                localMediaRenderer = localMediaRenderer,
                modifier = modifier
            )
        }
    }
}
