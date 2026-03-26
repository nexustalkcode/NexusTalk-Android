/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2021-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.about

import androidx.annotation.StringRes
import io.element.android.features.preferences.impl.BuildConfig
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** 版权信息 URL */
private const val COPYRIGHT_URL = BuildConfig.URL_COPYRIGHT
/** 使用政策 URL */
private const val USE_POLICY_URL = BuildConfig.URL_ACCEPTABLE_USE
/** 隐私政策 URL */
private const val PRIVACY_URL = BuildConfig.URL_PRIVACY

/**
 * Element 法律信息密封类
 *
 * 表示应用程序中显示的各种法律信息链接，包括版权声明、使用政策和隐私政策。
 *
 * @property titleRes 标题字符串资源 ID
 * @property url 法律信息页面的 URL 地址
 */
sealed class ElementLegal(
    @StringRes val titleRes: Int,
    val url: String,
) {
    /** 版权声明 */
    data object Copyright : ElementLegal(CommonStrings.common_copyright, COPYRIGHT_URL)
    /** 可接受使用政策 */
    data object AcceptableUsePolicy : ElementLegal(CommonStrings.common_acceptable_use_policy, USE_POLICY_URL)
    /** 隐私政策 */
    data object PrivacyPolicy : ElementLegal(CommonStrings.common_privacy_policy, PRIVACY_URL)
}

/**
 * 获取所有法律信息列表
 *
 * @return 包含所有 Element 法律信息的不可变列表
 */
fun getAllLegals(): ImmutableList<ElementLegal> {
    return persistentListOf(
        ElementLegal.Copyright,
        ElementLegal.AcceptableUsePolicy,
        ElementLegal.PrivacyPolicy,
    )
}
