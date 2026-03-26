/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.voicemessages.timeline

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.RedactedContent
import io.element.android.libraries.mediaplayer.api.MediaPlayer
import kotlinx.coroutines.withContext

/**
 * 已删除语音消息管理器接口
 *
 * 负责处理消息被删除（已撤回）时的语音消息播放状态管理。
 * 当正在播放的语音消息被删除时，自动暂停播放。
 */
interface RedactedVoiceMessageManager {
    suspend fun onEachMatrixTimelineItem(timelineItems: List<MatrixTimelineItem>)
}

/**
 * 默认已删除语音消息管理器实现类
 *
 * 实现 RedactedVoiceMessageManager 接口。
 * 监听时间线项目变化，当发现当前正在播放的语音消息被删除时，自动暂停播放。
 *
 * 使用 @ContributesBinding 注解绑定到 RoomScope。
 *
 * @property dispatchers 协程调度器
 * @property mediaPlayer 媒体播放器
 */
@ContributesBinding(RoomScope::class)
class DefaultRedactedVoiceMessageManager(
    private val dispatchers: CoroutineDispatchers,
    private val mediaPlayer: MediaPlayer,
) : RedactedVoiceMessageManager {
    override suspend fun onEachMatrixTimelineItem(timelineItems: List<MatrixTimelineItem>) {
        withContext(dispatchers.computation) {
            mediaPlayer.state.value.let { playerState ->
                if (playerState.isPlaying && playerState.mediaId != null) {
                    val needsToPausePlayer = timelineItems.any {
                        it is MatrixTimelineItem.Event &&
                            playerState.mediaId == it.eventId?.value &&
                            it.event.content is RedactedContent
                    }
                    if (needsToPausePlayer) {
                        withContext(dispatchers.main) { mediaPlayer.pause() }
                    }
                }
            }
        }
    }
}
