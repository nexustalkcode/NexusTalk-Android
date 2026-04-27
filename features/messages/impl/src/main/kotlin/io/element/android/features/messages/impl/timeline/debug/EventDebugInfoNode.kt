/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.debug

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo

@ContributesNode(RoomScope::class)
@AssistedInject
/**
 * 事件调试信息页面节点。
 *
 * 用于展示某个时间线事件的原始 JSON 和解析后模型，方便调试。
 */
class EventDebugInfoNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : Node(buildContext, plugins = plugins) {
    /**
     * 调试信息页面输入。
     */
    data class Inputs(
        val eventId: EventId?,
        val timelineItemDebugInfo: TimelineItemDebugInfo,
    ) : NodeInputs

    private val inputs = inputs<Inputs>()

    /**
     * 关闭当前调试信息页。
     */
    private fun onBackClick() {
        navigateUp()
    }

    /**
     * 渲染事件调试信息页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) = with(inputs) {
        EventDebugInfoView(
            eventId = eventId,
            model = timelineItemDebugInfo.model,
            originalJson = timelineItemDebugInfo.originalJson,
            latestEditedJson = timelineItemDebugInfo.latestEditedJson,
            onBackClick = ::onBackClick
        )
    }
}
