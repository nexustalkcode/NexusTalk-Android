/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.media.AudioManager

/**
 * 统一封装全屏来电页是否允许播放铃声的判断。
 *
 * 这里显式跟随系统铃声模式与铃声音量：
 * 1. 只有在普通响铃模式下才允许播放；
 * 2. 铃声音量为 0 时，即使模式仍是普通响铃，也视为不应播放。
 */
internal fun shouldPlayIncomingCallSound(
    ringerMode: Int,
    ringVolume: Int,
): Boolean {
    return ringerMode == AudioManager.RINGER_MODE_NORMAL && ringVolume > 0
}
