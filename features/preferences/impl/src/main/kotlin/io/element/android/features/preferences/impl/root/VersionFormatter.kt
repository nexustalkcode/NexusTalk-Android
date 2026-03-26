/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.root

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.toolbox.api.strings.StringProvider

/**
 * 版本格式化器接口
 */
interface VersionFormatter {
    /**
     * 获取格式化的版本字符串
     *
     * @return 格式化的版本信息
     */
    fun get(): String
}

/**
 * 默认版本格式化器实现
 *
 * 根据构建信息生成格式化的版本字符串。对于非 main 分支的构建，
 * 还会显示分支名称和提交版本号。
 *
 * @property stringProvider 字符串提供者
 * @property buildMeta 构建元数据
 */
@ContributesBinding(AppScope::class)
class DefaultVersionFormatter(
    private val stringProvider: StringProvider,
    private val buildMeta: BuildMeta,
) : VersionFormatter {
    override fun get(): String {
        val base = stringProvider.getString(
            CommonStrings.settings_version_number,
            buildMeta.versionName,
            buildMeta.versionCode.toString()
        )
        return if (buildMeta.gitBranchName == "main") {
            base
        } else {
            // In case of a build not from main, we display the branch name and the revision
            "$base\n${buildMeta.gitBranchName} (${buildMeta.gitRevision})"
        }
    }
}
