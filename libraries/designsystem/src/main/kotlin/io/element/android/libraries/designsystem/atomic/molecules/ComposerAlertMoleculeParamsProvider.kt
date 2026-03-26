/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * ComposerAlertMolecule 预览参数提供器
 *
 * 提供 ComposerAlertMolecule 组件在设计预览中使用的各种参数组合。
 * 包含不同警告级别（Default、Info、Critical），
 * 以及带头像和不带头像、显示图标和不显示图标的组合。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.anAvatarData

/**
 * ComposerAlertMolecule 预览参数数据类
 *
 * 定义预览所需的参数组合。
 *
 * @property level ComposerAlertLevel 警告级别
 * @property avatar AvatarData? 可选的头像数据
 * @property showIcon Boolean 是否显示图标
 */
internal data class ComposerAlertMoleculeParams(
    val level: ComposerAlertLevel,
    val avatar: AvatarData? = null,
    val showIcon: Boolean = false,
)

/**
 * ComposerAlertMolecule 预览参数提供器
 *
 * 实现 PreviewParameterProvider 接口，提供预览所需的参数序列。
 */
internal class ComposerAlertMoleculeParamsProvider : PreviewParameterProvider<ComposerAlertMoleculeParams> {
    private val allLevels = sequenceOf(
        ComposerAlertLevel.Default,
        ComposerAlertLevel.Info,
        ComposerAlertLevel.Critical
    )

    override val values: Sequence<ComposerAlertMoleculeParams>
        get() = allLevels.flatMap { level ->
            sequenceOf(
                ComposerAlertMoleculeParams(level = level),
                ComposerAlertMoleculeParams(level = level, avatar = anAvatarData(size = AvatarSize.ComposerAlert)),
                ComposerAlertMoleculeParams(level = level, showIcon = true),
            )
        }
}
