/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.audio.api

/**
 * 请求音频焦点的业务场景。
 */
enum class AudioFocusRequester {
    ElementCall,
    VoiceMessage,
    MediaViewer,
}

/**
 * 音频焦点管理接口。
 */
interface AudioFocus {
    /**
     * 为指定业务场景请求音频焦点。
     *
     * @param requester 请求音频焦点的业务场景。
     * @param onFocusLost 当音频焦点丢失时触发的回调。
     */
    fun requestAudioFocus(
        requester: AudioFocusRequester,
        onFocusLost: () -> Unit,
    )

    /**
     * 释放当前持有的音频焦点。
     */
    fun releaseAudioFocus()
}
