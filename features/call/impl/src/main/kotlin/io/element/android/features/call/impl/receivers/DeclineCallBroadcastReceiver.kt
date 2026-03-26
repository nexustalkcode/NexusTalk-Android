/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat
import dev.zacsweers.metro.Inject
import io.element.android.features.call.api.CallType
import io.element.android.features.call.impl.di.CallBindings
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 拒绝通话广播接收器
 *
 * 处理用户点击通知"拒绝"按钮时的事件。
 * 当用户从通知栏拒绝来电时，此接收器会被触发，
 * 并调用 ActiveCallManager 挂断该通话。
 *
 * @see BroadcastReceiver 广播接收器基类
 * @see CallNotificationData 通话通知数据
 * @see ActiveCallManager 活动通话管理器
 */
class DeclineCallBroadcastReceiver : BroadcastReceiver() {
    companion object {
        /** 传递通知数据的 Extra 键名 */
        const val EXTRA_NOTIFICATION_DATA = "EXTRA_NOTIFICATION_DATA"
    }

    @Inject
    lateinit var activeCallManager: ActiveCallManager

    @AppCoroutineScope
    @Inject lateinit var appCoroutineScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationData = intent?.let { IntentCompat.getParcelableExtra(it, EXTRA_NOTIFICATION_DATA, CallNotificationData::class.java) }
            ?: return
        context.bindings<CallBindings>().inject(this)
        appCoroutineScope.launch {
            activeCallManager.hangUpCall(
                callType = CallType.RoomCall(
                    sessionId = notificationData.sessionId,
                    roomId = notificationData.roomId,
                ),
                notificationData = notificationData,
            )
        }
    }
}
