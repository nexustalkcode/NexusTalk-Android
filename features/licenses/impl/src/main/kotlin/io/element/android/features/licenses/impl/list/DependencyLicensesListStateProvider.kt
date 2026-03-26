/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.licenses.impl.model.DependencyLicenseItem
import io.element.android.features.licenses.impl.model.License
import io.element.android.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 依赖项许可证列表状态预览参数提供者
 *
 * 提供 DependencyLicensesListState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 * 包含多种状态场景：加载中、失败、成功、过滤状态等。
 *
 * @see DependencyLicensesListState 依赖项许可证列表状态
 */
open class DependencyLicensesListStateProvider : PreviewParameterProvider<DependencyLicensesListState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 DependencyLicensesListState 序列
     */
    override val values: Sequence<DependencyLicensesListState>
        get() = sequenceOf(
            aDependencyLicensesListState(
                licenses = AsyncData.Loading()
            ),
            aDependencyLicensesListState(
                licenses = AsyncData.Failure(Exception("Failed to load licenses"))
            ),
            aDependencyLicensesListState(
                licenses = AsyncData.Success(
                    persistentListOf(
                        aDependencyLicenseItem(),
                        aDependencyLicenseItem(name = null),
                    )
                )
            ),
            aDependencyLicensesListState(
                licenses = AsyncData.Success(
                    persistentListOf(
                        aDependencyLicenseItem(),
                        aDependencyLicenseItem(name = null),
                    )
                ),
                filter = "a filter",
            ),
        )
}

/**
 * 创建示例许可证列表状态
 *
 * @param licenses 许可证异步数据
 * @param filter 过滤文本
 * @return 示例 DependencyLicensesListState 实例
 */
private fun aDependencyLicensesListState(
    licenses: AsyncData<ImmutableList<DependencyLicenseItem>>,
    filter: String = "",
): DependencyLicensesListState {
    return DependencyLicensesListState(
        licenses = licenses,
        filter = filter,
        eventSink = {},
    )
}

/**
 * 创建示例依赖项许可证项目
 *
 * @param name 依赖项名称
 * @return 示例 DependencyLicenseItem 实例
 */
internal fun aDependencyLicenseItem(
    name: String? = "A dependency",
) = DependencyLicenseItem(
    groupId = "org.some.group",
    artifactId = "a-dependency",
    version = "1.0.0",
    name = name,
    licenses = listOf(
        License(
            identifier = "Apache 2.0",
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    unknownLicenses = listOf(),
    scm = null,
)
