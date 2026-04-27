/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.api

/**
 * 用户资料页可能触发的交互事件。
 */
sealed interface UserProfileEvents {
    /** 发起或继续私聊。 */
    data object StartDM : UserProfileEvents

    /** 清理“开始私聊”异步状态。 */
    data object ClearStartDMState : UserProfileEvents

    /** 拉黑用户；当 [needsConfirmation] 为 true 时先弹确认框。 */
    data class BlockUser(val needsConfirmation: Boolean = false) : UserProfileEvents

    /** 取消拉黑用户；当 [needsConfirmation] 为 true 时先弹确认框。 */
    data class UnblockUser(val needsConfirmation: Boolean = false) : UserProfileEvents

    /** 清理拉黑/取消拉黑错误状态。 */
    data object ClearBlockUserError : UserProfileEvents

    /** 清理当前确认弹窗状态。 */
    data object ClearConfirmationDialog : UserProfileEvents

    /** 撤回当前验证请求。 */
    data object WithdrawVerification : UserProfileEvents

    /** 复制指定文本到剪贴板。 */
    data class CopyToClipboard(val text: String) : UserProfileEvents
}
