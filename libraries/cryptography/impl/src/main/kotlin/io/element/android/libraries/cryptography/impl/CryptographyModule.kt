/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.cryptography.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import java.security.KeyStore

internal const val ANDROID_KEYSTORE = "AndroidKeyStore"

@ContributesTo(AppScope::class)
@BindingContainer
/**
 * 加密相关依赖提供模块。
 */
object CryptographyModule {
    @Provides
    /**
     * 提供已加载的 AndroidKeyStore 实例。
     */
    fun providesAndroidKeyStore(): KeyStore {
        return KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }
}
