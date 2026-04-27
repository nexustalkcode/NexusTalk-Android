/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.PendingIntentCompat
import io.element.android.features.call.api.CallType
import io.element.android.features.call.impl.DefaultElementCallEntryPoint
import io.element.android.features.call.impl.ui.ElementCallActivity

/**
 * 意图提供者工具对象
 *
 * 提供创建通话相关 Intent 和 PendingIntent 的工具函数。
 * 用于启动 ElementCallActivity 和创建通知中的待处理意图。
 *
 * @see Intent Android 意图
 * @see PendingIntent 待处理意图
 * @see ElementCallActivity 通话主界面 Activity
 */
internal object IntentProvider {
    /**
     * 创建启动通话界面的 Intent
     *
     * @param context Android 上下文
     * @param callType 通话类型
     * @return 启动 ElementCallActivity 的 Intent
     */
    fun createIntent(context: Context, callType: CallType): Intent = Intent(context, ElementCallActivity::class.java).apply {
        putExtra(DefaultElementCallEntryPoint.EXTRA_CALL_TYPE, callType)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
    }

    /**
     * 获取通话界面的 PendingIntent
     *
     * 用于在通知中点击时启动通话界面。
     *
     * @param context Android 上下文
     * @param callType 通话类型
     * @return PendingIntent 待处理意图
     */
    fun getPendingIntent(
        context: Context,
        callType: CallType,
        requestCode: Int = DefaultElementCallEntryPoint.REQUEST_CODE,
    ): PendingIntent {
        return PendingIntentCompat.getActivity(
            context,
            requestCode,
            createIntent(context, callType),
            PendingIntent.FLAG_CANCEL_CURRENT,
            false
        )!!
    }
}
