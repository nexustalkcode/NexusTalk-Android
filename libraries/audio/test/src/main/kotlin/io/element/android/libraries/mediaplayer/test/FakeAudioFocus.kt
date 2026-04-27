/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.mediaplayer.test

import io.element.android.libraries.audio.api.AudioFocus
import io.element.android.libraries.audio.api.AudioFocusRequester

class FakeAudioFocus(
    private val requestAudioFocusResult: (AudioFocusRequester, () -> Unit) -> Unit = { _, _ ->
        error("requestAudioFocusResult should be provided in tests")
    },
    private val releaseAudioFocusResult: () -> Unit = {
        error("releaseAudioFocusResult should be provided in tests")
    },
) : AudioFocus {
    override fun requestAudioFocus(
        requester: AudioFocusRequester,
        onFocusLost: () -> Unit,
    ) {
        requestAudioFocusResult(requester, onFocusLost)
    }

    override fun releaseAudioFocus() {
        releaseAudioFocusResult()
    }
}
