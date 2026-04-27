/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.permalink

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PermalinkExtensionsTest {
    private val targetBaseUrl = "https://nexustalk.space/#/"

    @Test
    fun `normalizeMatrixPermalinkBaseUrl rewrites user permalink to configured base url`() {
        val permalink = "https://matrix.to/#/@maomaoaiai:nexustalk.space"

        val result = permalink.normalizeMatrixPermalinkBaseUrl(targetBaseUrl)

        assertThat(result).isEqualTo("https://nexustalk.space/#/@maomaoaiai:nexustalk.space")
    }

    @Test
    fun `normalizeMatrixPermalinkBaseUrl rewrites room permalink and preserves via parameter`() {
        val permalink = "https://matrix.to/#/!JCryywdetsDAFFhtcT:nexustalk.space?via=nexustalk.space"

        val result = permalink.normalizeMatrixPermalinkBaseUrl(targetBaseUrl)

        assertThat(result).isEqualTo("https://nexustalk.space/#/!JCryywdetsDAFFhtcT:nexustalk.space?via=nexustalk.space")
    }

    @Test
    fun `normalizeMatrixPermalinkBaseUrl keeps non matrix-to urls unchanged`() {
        val permalink = "https://example.com/#/@maomaoaiai:nexustalk.space"

        val result = permalink.normalizeMatrixPermalinkBaseUrl(targetBaseUrl)

        assertThat(result).isEqualTo(permalink)
    }
}
