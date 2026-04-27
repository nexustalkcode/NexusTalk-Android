/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.media.AudioManager
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IncomingCallAlertPolicyTest {
    @Test
    fun `shouldPlayIncomingCallSound returns true when ringer mode is normal and ring volume is above zero`() {
        assertThat(
            shouldPlayIncomingCallSound(
                ringerMode = AudioManager.RINGER_MODE_NORMAL,
                ringVolume = 3,
            )
        ).isTrue()
    }

    @Test
    fun `shouldPlayIncomingCallSound returns false when ringer mode is silent`() {
        assertThat(
            shouldPlayIncomingCallSound(
                ringerMode = AudioManager.RINGER_MODE_SILENT,
                ringVolume = 3,
            )
        ).isFalse()
    }

    @Test
    fun `shouldPlayIncomingCallSound returns false when ringer mode is vibrate`() {
        assertThat(
            shouldPlayIncomingCallSound(
                ringerMode = AudioManager.RINGER_MODE_VIBRATE,
                ringVolume = 3,
            )
        ).isFalse()
    }

    @Test
    fun `shouldPlayIncomingCallSound returns false when ring volume is zero`() {
        assertThat(
            shouldPlayIncomingCallSound(
                ringerMode = AudioManager.RINGER_MODE_NORMAL,
                ringVolume = 0,
            )
        ).isFalse()
    }
}
