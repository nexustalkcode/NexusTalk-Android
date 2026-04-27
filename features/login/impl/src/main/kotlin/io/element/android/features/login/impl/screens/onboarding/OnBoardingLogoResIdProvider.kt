/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import android.annotation.SuppressLint
import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.annotations.ApplicationContext

/**
 * 提供引导页 Logo 资源 ID 的接口。
 */
fun interface OnBoardingLogoResIdProvider {
    /**
     * 返回当前应用可用的引导页 logo 资源 ID；若未提供则返回 `null`。
     */
    fun get(): Int?
}

@ContributesBinding(AppScope::class)
/**
 * 默认的引导页 Logo 资源提供器。
 */
class DefaultOnBoardingLogoResIdProvider(
    @ApplicationContext private val context: Context,
) : OnBoardingLogoResIdProvider {
    @SuppressLint("DiscouragedApi")
    /**
     * 通过约定名称 `onboarding_logo` 动态查找资源。
     */
    override fun get(): Int? {
        val resId = context.resources
            .getIdentifier("onboarding_logo", "drawable", context.packageName)
            .takeIf { it != 0 }
        return resId
    }
}
