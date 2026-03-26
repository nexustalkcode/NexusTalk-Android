/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.utils

import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.core.meta.BuildType
import io.element.android.libraries.ui.utils.MultipleTapToUnlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 开发者设置显示提供者
 *
 * 负责管理开发者设置页面的显示权限。对于非发布版本构建，默认显示开发者设置；
 * 对于发布版本，需要连续点击版本号 7 次才能解锁开发者设置。
 *
 * @property buildMeta 构建元数据
 */
@Inject
class ShowDeveloperSettingsProvider(
    buildMeta: BuildMeta,
) {
    companion object {
        /** 解锁开发者设置需要的点击次数 */
        const val DEVELOPER_SETTINGS_COUNTER = 7
    }

    private val multipleTapToUnlock = MultipleTapToUnlock(DEVELOPER_SETTINGS_COUNTER)
    private val isDeveloperBuild = buildMeta.buildType != BuildType.RELEASE

    private val _showDeveloperSettings = MutableStateFlow(isDeveloperBuild)
    /** 是否显示开发者设置的 Flow */
    val showDeveloperSettings: StateFlow<Boolean> = _showDeveloperSettings

    /**
     * 解锁开发者设置
     *
     * @param scope 协程作用域
     */
    fun unlockDeveloperSettings(scope: CoroutineScope) {
        if (multipleTapToUnlock.unlock(scope)) {
            _showDeveloperSettings.value = true
        }
    }
}
