/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 依赖项许可证数据类
 *
 * 表示一个开源依赖项的许可证信息，包含组 ID、构件 ID、版本、许可证列表等。
 * 使用 Kotlin 序列化进行数据转换，支持 Parcelable 传递。
 *
 * @property groupId 依赖项的组 ID
 * @property artifactId 依赖项的构件 ID
 * @property version 依赖项版本
 * @property licenses 许可证列表
 * @property unknownLicenses 未知许可证列表
 * @property name 依赖项显示名称
 * @property scm 软件配置管理信息（源码仓库）
 * @see License 许可证信息数据类
 * @see Scm 源码仓库信息数据类
 */
@Serializable
@Parcelize
data class DependencyLicenseItem(
    val groupId: String,
    val artifactId: String,
    val version: String,
    @SerialName("spdxLicenses")
    val licenses: List<License>?,
    val unknownLicenses: List<License>?,
    val name: String?,
    val scm: Scm?,
) : Parcelable {
    /** 安全名称，如果 name 为空或 "null" 则使用 groupId:artifactId */
    @IgnoredOnParcel
    val safeName = name?.takeIf { name -> name != "null" } ?: "$groupId:$artifactId"
}

/**
 * 许可证信息数据类
 *
 * 表示单个许可证的信息，包含标识符、名称和 URL。
 *
 * @property identifier 许可证标识符（如 Apache-2.0）
 * @property name 许可证名称
 * @property licenseUrl 许可证 URL
 */
@Serializable
@Parcelize
data class License(
    val identifier: String?,
    val name: String?,
    val url: String?,
) : Parcelable

/**
 * 源码仓库信息数据类
 *
 * 表示依赖项的源码仓库信息。
 *
 * @property url 仓库 URL
 */
@Serializable
@Parcelize
data class Scm(
    val url: String,
) : Parcelable
