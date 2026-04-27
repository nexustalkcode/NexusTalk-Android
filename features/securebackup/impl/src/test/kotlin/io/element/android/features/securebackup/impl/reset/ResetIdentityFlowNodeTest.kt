/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResetIdentityFlowNodeTest {
    @Test
    fun `shouldRetryOidcReset returns true for connection aborted errors`() {
        val throwable = IllegalStateException(
            "top level",
            RuntimeException("hyper::Error(Io, Kind(ConnectionAborted))"),
        )

        assertThat(throwable.shouldRetryOidcReset()).isTrue()
    }

    @Test
    fun `shouldRetryOidcReset returns false for non transient errors`() {
        val throwable = IllegalStateException("permanent failure")

        assertThat(throwable.shouldRetryOidcReset()).isFalse()
    }
}
