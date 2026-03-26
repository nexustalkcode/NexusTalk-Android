/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import kotlinx.parcelize.Parcelize

/**
 * 安全备份功能入口点接口
 *
 * 定义了安全备份功能的入口接口，负责创建和管理安全备份流程的节点。
 */
interface SecureBackupEntryPoint : FeatureEntryPoint {
    /**
     * 初始目标密封接口
     *
     * 定义安全备份流程的各个初始页面目标。
     */
    sealed interface InitialTarget : Parcelable {
        /** 根页面 */
        @Parcelize
        data object Root : InitialTarget

        /** 设置恢复页面 */
        @Parcelize
        data object SetUpRecovery : InitialTarget

        /** 输入恢复密钥页面 */
        @Parcelize
        data object EnterRecoveryKey : InitialTarget

        /** 重置身份页面 */
        @Parcelize
        data object ResetIdentity : InitialTarget
    }

    /**
     * 输入参数数据类
     *
     * @property initialElement 初始目标
     */
    data class Params(val initialElement: InitialTarget) : NodeInputs

    /**
     * 创建一个安全备份节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @param callback 回调接口
     * @return Node 安全备份节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 安全备份流程回调接口
     */
    interface Callback : Plugin {
        /** 完成回调 */
        fun onDone()
    }
}
