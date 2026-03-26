/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.permissions

import io.element.android.libraries.architecture.Presenter

/**
 * 权限Presenter接口
 *
 * 定义了处理权限请求和状态管理的Presenter。
 *
 * @param PermissionsState 权限状态类型
 */
interface PermissionsPresenter : Presenter<PermissionsState> {
    /**
     * 权限Presenter工厂接口
     *
     * 用于创建权限Presenter实例。
     */
    fun interface Factory {
        /**
         * 创建权限Presenter实例
         *
         * @param permissions 需要请求的权限列表
         * @return PermissionsPresenter 权限Presenter实例
         */
        fun create(permissions: List<String>): PermissionsPresenter
    }
}
