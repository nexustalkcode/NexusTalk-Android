/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api.acceptdecline

import io.element.android.features.invite.api.InviteData
import io.element.android.libraries.architecture.AsyncAction

/**
 * 确认拒绝邀请数据类
 *
 * 表示用户在拒绝邀请前需要确认的场景。
 * 继承自 AsyncAction.Confirming，用于显示确认对话框。
 *
 * @property inviteData 邀请数据
 * @property blockUser 是否同时封禁发送邀请的用户
 */
data class ConfirmingDeclineInvite(val inviteData: InviteData, val blockUser: Boolean) : AsyncAction.Confirming
