/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl

import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyEntryPoint

/**
 * 安全与隐私模块导航器接口
 *
 * 定义了安全与隐私功能中各页面之间的导航操作。
 * 继承自 Plugin 接口，可作为插件注入到节点中。
 */
interface SecurityAndPrivacyNavigator : Plugin {
    /**
     * 完成安全与隐私设置并退出
     */
    fun onDone()

    /**
     * 打开编辑房间地址页面
     */
    fun openEditRoomAddress()

    /**
     * 关闭编辑房间地址页面
     */
    fun closeEditRoomAddress()

    /**
     * 打开管理授权空间页面
     */
    fun openManageAuthorizedSpaces()

    /**
     * 关闭管理授权空间页面
     */
    fun closeManageAuthorizedSpaces()
}

/**
 * 基于 BackStack 的安全与隐私导航器实现
 *
 * 使用 Appyx 框架的 BackStack 来管理导航目标，实现页面跳转和返回功能。
 *
 * @property callback 回调接口，用于通知外部事件
 * @property backStack 后台堆栈，用于管理导航目标
 * @see SecurityAndPrivacyNavigator 导航器接口
 */
class BackstackSecurityAndPrivacyNavigator(
    /** 回调接口，用于通知外部事件 */
    private val callback: SecurityAndPrivacyEntryPoint.Callback,
    /** 后台堆栈，用于管理导航目标 */
    private val backStack: BackStack<SecurityAndPrivacyFlowNode.NavTarget>
) : SecurityAndPrivacyNavigator {
    override fun onDone() {
        callback.onDone()
    }

    override fun openEditRoomAddress() {
        backStack.push(SecurityAndPrivacyFlowNode.NavTarget.EditRoomAddress)
    }

    override fun closeEditRoomAddress() {
        backStack.pop()
    }

    override fun openManageAuthorizedSpaces() {
        backStack.push(SecurityAndPrivacyFlowNode.NavTarget.ManageAuthorizedSpaces)
    }

    override fun closeManageAuthorizedSpaces() {
        backStack.pop()
    }
}
