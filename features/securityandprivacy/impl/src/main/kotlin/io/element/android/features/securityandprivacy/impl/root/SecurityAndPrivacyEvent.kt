/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.root

/**
 * 安全与隐私事件密封接口
 *
 * 定义安全与隐私设置页面中可能发生的用户交互事件。
 *
 * @see SecurityAndPrivacyState 页面状态
 */
sealed interface SecurityAndPrivacyEvent {
    /** 打开编辑房间地址页面 */
    data object EditRoomAddress : SecurityAndPrivacyEvent

    /** 打开管理授权空间页面 */
    data object ManageAuthorizedSpaces : SecurityAndPrivacyEvent

    /** 保存设置 */
    data object Save : SecurityAndPrivacyEvent

    /** 退出设置页面 */
    data object Exit : SecurityAndPrivacyEvent

    /** 关闭退出确认对话框 */
    data object DismissExitConfirmation : SecurityAndPrivacyEvent

    /**
     * 变更房间访问权限
     * @property roomAccess 新的房间访问权限设置
     */
    data class ChangeRoomAccess(val roomAccess: SecurityAndPrivacyRoomAccess) : SecurityAndPrivacyEvent

    /** 特殊操作：选择"空间成员"访问权限 */
    data object SelectSpaceMemberAccess : SecurityAndPrivacyEvent

    /** 特殊操作：选择"通过空间成员邀请"访问权限 */
    data object SelectAskToJoinWithSpaceMembersAccess : SecurityAndPrivacyEvent

    /** 切换加密状态 */
    data object ToggleEncryptionState : SecurityAndPrivacyEvent

    /** 取消启用加密 */
    data object CancelEnableEncryption : SecurityAndPrivacyEvent

    /** 确认启用加密 */
    data object ConfirmEnableEncryption : SecurityAndPrivacyEvent

    /**
     * 变更历史可见性
     * @property historyVisibility 新的历史可见性设置
     */
    data class ChangeHistoryVisibility(val historyVisibility: SecurityAndPrivacyHistoryVisibility) : SecurityAndPrivacyEvent

    /** 切换房间在目录中的可见性 */
    data object ToggleRoomVisibility : SecurityAndPrivacyEvent

    /** 关闭保存错误提示 */
    data object DismissSaveError : SecurityAndPrivacyEvent
}
