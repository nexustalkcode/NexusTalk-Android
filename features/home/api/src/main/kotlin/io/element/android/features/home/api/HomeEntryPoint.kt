/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 首页功能入口点接口
 *
 * 定义首页功能的接口契约，负责创建和管理首页节点。
 * 该入口点用于在应用中嵌入首页，展示房间列表和快捷操作。
 *
 * @property Callback 回调接口，处理房间导航和设置事件
 * @see HomeEntryPoint.Callback 回调接口
 */
interface HomeEntryPoint : FeatureEntryPoint {
    /**
     * 首页回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到指定房间
         *
         * @param roomId 房间 ID
         * @param joinedRoom 已加入的房间信息，可为 null
         */
        fun navigateToRoom(roomId: RoomId, joinedRoom: JoinedRoom?)
        /** 导航到开始新聊天房间 */
        fun navigateToStartChat()
        /** 导航到创建房间 */
        fun navigateToCreateRoom()
        /** 导航到创建空间 */
        fun navigateToCreateSpace()
        /** 导航到设置页面 */
        fun navigateToSettings()
        /** 导航到编辑个人资料页面 */
        fun navigateToUserProfile(matrixUser: MatrixUser)
        /** 导航到我的二维码页面 */
        fun navigateToUserQrCode(matrixUser: MatrixUser)
        /** 导航到通知设置页面 */
        fun navigateToNotificationSettings()
        /** 导航到锁屏设置页面 */
        fun navigateToLockScreenSettings()
        /** 导航到高级设置页面 */
        fun navigateToAdvancedSettings()
        fun navigateToAbout()
        /** 导航到被拉黑用户页面 */
        fun navigateToBlockedUsers()
        /** 导航到登出流程页面 */
        fun navigateToSignOut()
        /** 导航到设置恢复 */
        fun navigateToSetUpRecovery()
        /** 导航到输入恢复密钥 */
        fun navigateToEnterRecoveryKey()
        /** 导航到房间设置 */
        fun navigateToRoomSettings(roomId: RoomId)
        /** 导航到问题报告 */
        fun navigateToBugReport()

        /** 导航到扫码页面 */
        fun navigateToScanQrCode()
    }

    /**
     * 创建首页节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return 创建的节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node
}
