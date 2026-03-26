/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.parcelize.Parcelize

/**
 * 首选项功能入口点接口
 *
 * 定义了应用首选项设置的入口接口，负责创建和管理首选项设置流程的节点。
 */
interface PreferencesEntryPoint : FeatureEntryPoint {
    /**
     * 初始目标密封接口
     *
     * 定义首选项流程的各个初始页面目标。
     */
    sealed interface InitialTarget : Parcelable {
        /** 根页面 */
        @Parcelize
        data object Root : InitialTarget

        /** 通知设置页面 */
        @Parcelize
        data object NotificationSettings : InitialTarget

        /** 锁屏设置页面 */
        @Parcelize
        data object LockScreenSettings : InitialTarget

        /** 高级设置页面 */
        @Parcelize
        data object AdvancedSettings : InitialTarget

        @Parcelize
        data object About : InitialTarget

        /** 被屏蔽用户页面 */
        @Parcelize
        data object BlockedUsers : InitialTarget

        /** 退出登录页面 */
        @Parcelize
        data object SignOut : InitialTarget

        /** 编辑个人资料页面 */
        @Parcelize
        data class UserProfile(val matrixUser: MatrixUser) : InitialTarget

        /** 我的二维码页面 */
        @Parcelize
        data class UserQrCode(val matrixUser: MatrixUser) : InitialTarget

        /** 通知故障排除页面 */
        @Parcelize
        data object NotificationTroubleshoot : InitialTarget
    }

    /**
     * 输入参数数据类
     *
     * @property initialElement 初始目标
     */
    data class Params(val initialElement: InitialTarget) : NodeInputs

    /**
     * 创建一个首选项节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @param callback 回调接口
     * @return Node 首选项节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 首选项流程回调接口
     */
    interface Callback : Plugin {
        /** 导航到添加账户 */
        fun navigateToAddAccount()
        /** 导航到链接新设备 */
        fun navigateToLinkNewDevice()
        /** 导航到问题报告 */
        fun navigateToBugReport()
        /** 导航到安全备份 */
        fun navigateToSecureBackup()
        /**
         * 导航到房间通知设置
         *
         * @param roomId 房间 ID
         */
        fun navigateToRoomNotificationSettings(roomId: RoomId)
        /**
         * 导航到指定事件
         *
         * @param roomId 房间 ID
         * @param eventId 事件 ID
         */
        fun navigateToEvent(roomId: RoomId, eventId: EventId)
    }
}
