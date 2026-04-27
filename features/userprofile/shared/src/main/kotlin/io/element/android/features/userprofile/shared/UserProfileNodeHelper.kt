/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.shared

import android.content.Context
import io.element.android.appconfig.MatrixConfiguration
import io.element.android.libraries.androidutils.R
import io.element.android.libraries.androidutils.system.startSharePlainTextIntent
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkBuilder
import io.element.android.libraries.matrix.api.permalink.normalizeMatrixPermalinkBaseUrl
import io.element.android.libraries.ui.strings.CommonStrings
import timber.log.Timber

/**
 * 用户资料流程的共享辅助类。
 *
 * 主要负责封装分享用户 permalink 的逻辑，以及对外暴露统一的导航回调接口。
 */
class UserProfileNodeHelper(
    private val userId: UserId,
) {
    /**
     * 用户资料相关的导航回调接口。
     */
    interface Callback : NodeInputs {
        fun navigateToAvatarPreview(username: String, avatarUrl: String)
        fun navigateToRoom(roomId: RoomId)
        fun startCall(dmRoomId: RoomId)
        fun startVerifyUserFlow(userId: UserId)
    }

    /**
     * 分享当前用户的 permalink。
     *
     * @param context 当前上下文。
     * @param permalinkBuilder 用于构建用户 permalink 的工具。
     */
    fun onShareUser(
        context: Context,
        permalinkBuilder: PermalinkBuilder,
    ) {
        val permalinkResult = permalinkBuilder.permalinkForUser(userId)
        permalinkResult.onSuccess { permalink ->
            /* 分享用户时只替换 permalink 的基础域名，外发链接统一落到 nexustalk.space。 */
            val normalizedPermalink = permalink.normalizeMatrixPermalinkBaseUrl(
                targetBaseUrl = MatrixConfiguration.MATRIX_TO_PERMALINK_BASE_URL,
            )
            context.startSharePlainTextIntent(
                activityResultLauncher = null,
                chooserTitle = context.getString(CommonStrings.action_share),
                text = normalizedPermalink,
                noActivityFoundMessage = context.getString(R.string.error_no_compatible_app_found)
            )
        }.onFailure {
            Timber.e(it)
        }
    }
}
