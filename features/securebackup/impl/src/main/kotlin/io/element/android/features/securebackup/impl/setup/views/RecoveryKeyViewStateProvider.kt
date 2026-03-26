/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.setup.views

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 恢复密钥视图状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [RecoveryKeyViewState] 示例。
 */
open class RecoveryKeyViewStateProvider : PreviewParameterProvider<RecoveryKeyViewState> {
    /** 预览状态序列 */
    override val values: Sequence<RecoveryKeyViewState>
        get() = sequenceOf(RecoveryKeyUserStory.Setup, RecoveryKeyUserStory.Change, RecoveryKeyUserStory.Enter)
            .flatMap {
                sequenceOf(
                    aRecoveryKeyViewState(recoveryKeyUserStory = it),
                    aRecoveryKeyViewState(recoveryKeyUserStory = it, inProgress = true),
                    aRecoveryKeyViewState(recoveryKeyUserStory = it, formattedRecoveryKey = aFormattedRecoveryKey()),
                    aRecoveryKeyViewState(recoveryKeyUserStory = it, formattedRecoveryKey = aFormattedRecoveryKey(), inProgress = true),
                )
            } + sequenceOf(
            aRecoveryKeyViewState(recoveryKeyUserStory = RecoveryKeyUserStory.Enter, formattedRecoveryKey = aFormattedRecoveryKey().replace(" ", "")),
            aRecoveryKeyViewState(recoveryKeyUserStory = RecoveryKeyUserStory.Enter, formattedRecoveryKey = "This is a passphrase with spaces"),
            aRecoveryKeyViewState(
                recoveryKeyUserStory = RecoveryKeyUserStory.Enter,
                formattedRecoveryKey = aFormattedRecoveryKey().replace(" ", ""),
                displayTextFieldContents = false
            ),
        )
}

/**
 * 创建恢复密钥视图状态的辅助函数
 *
 * @param recoveryKeyUserStory 恢复密钥用户场景
 * @param formattedRecoveryKey 格式化后的恢复密钥
 * @param inProgress 是否正在进行操作
 * @param displayTextFieldContents 是否显示文本字段内容
 * @return 恢复密钥视图状态实例
 */
fun aRecoveryKeyViewState(
    recoveryKeyUserStory: RecoveryKeyUserStory = RecoveryKeyUserStory.Setup,
    formattedRecoveryKey: String? = null,
    inProgress: Boolean = false,
    displayTextFieldContents: Boolean = true,
) = RecoveryKeyViewState(
    recoveryKeyUserStory = recoveryKeyUserStory,
    formattedRecoveryKey = formattedRecoveryKey,
    displayTextFieldContents = displayTextFieldContents,
    inProgress = inProgress,
)

/**
 * 生成格式化恢复密钥的辅助函数
 *
 * @return 格式化的恢复密钥示例字符串
 */
internal fun aFormattedRecoveryKey(): String {
    return "Estm dfyU adhD h8y6 Estm dfyU adhD h8y6 Estm dfyU adhD h8y6"
}
