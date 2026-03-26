/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

import kotlin.time.Duration.Companion.minutes

/**
 * 语音消息配置 (Voice Message Configuration)
 *
 * 此对象包含语音消息录制和播放功能相关的配置项。
 * 用于限制语音消息的最大时长等参数。
 */
object VoiceMessageConfig {
    /** 单条语音消息的最大录制时长。超过此时长后，系统将自动停止录音 */
    val maxVoiceMessageDuration = 30.minutes
}
