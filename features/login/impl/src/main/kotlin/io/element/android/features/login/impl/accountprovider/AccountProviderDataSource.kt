/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.accountprovider


/**
 * 账户提供者数据源
 *
 * 该类负责管理当前登录所使用的账户提供者（homeserver）的状态管理。
 * 通过 StateFlow 提供响应式的状态流，支持设置URL、重置为默认值等功能。
 * 使用 Metro 库的依赖注入机制，确保在应用生命周期内保持单例。
 */

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.appconfig.AuthenticationConfig
import io.element.android.features.enterprise.api.EnterpriseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@SingleIn(AppScope::class)
@Inject
/**
 * 账户提供者数据源类
 *
 * 职责：
 * 1. 管理当前 homeserver 的状态信息
 * 2. 提供状态变化的响应式接口（StateFlow）
 * 3. 支持修改和重置 homeserver 设置
 *
 * @param enterpriseService 企业服务，用于获取企业配置的默认 homeserver 列表
 */
class AccountProviderDataSource(
    enterpriseService: EnterpriseService,
) {
    /**
     * 默认账户提供者
     *
     * 初始化逻辑：
     * 1. 从企业服务获取配置的 homeserver 列表
     * 2. 过滤掉通用的"任意账户提供者"标识
     * 3. 如果没有企业配置的 homeserver，则使用 matrix.org 作为默认值
     */
    private val defaultAccountProvider = createAccountProvider(
        url = enterpriseService.defaultHomeserverList()
            .firstOrNull { it != EnterpriseService.ANY_ACCOUNT_PROVIDER }
            ?: AuthenticationConfig.MATRIX_ORG_URL
    )

    /**
     * 当前账户提供者状态流
     *
     * 使用 MutableStateFlow 实现可变的内部状态，
     * 对外暴露不可变的 StateFlow 接口以保证数据封装性。
     * StateFlow 会自动向收集者发送最新状态值。
     */
    private val accountProvider: MutableStateFlow<AccountProvider> = MutableStateFlow(defaultAccountProvider)

    /**
     * 账户提供者状态流对外接口
     *
     * 提供只读的 StateFlow 供外部组件观察状态变化，
     * 当账户提供者发生变化时，所有观察者都会收到更新。
     */
    val flow: StateFlow<AccountProvider> = accountProvider.asStateFlow()

    /**
     * 重置账户提供者为默认值
     *
     * 将当前状态重置为应用启动时的默认账户提供者。
     * 常用于用户退出登录后需要恢复默认设置的场景。
     */
    suspend fun reset() {
        accountProvider.emit(defaultAccountProvider)
    }

    /**
     * 通过 URL 设置账户提供者
     *
     * 将给定的 URL 字符串转换为 AccountProvider 对象并设置为当前账户提供者。
     * 该方法会自动将自定义 URL 标记为非公共服务器（isPublic = false）。
     *
     * @param url homeserver 的 URL 地址
     */
    suspend fun setUrl(url: String) {
        setAccountProvider(createAccountProvider(url))
    }

    /**
     * 直接设置账户提供者
     *
     * 将完整的 AccountProvider 对象设置为当前状态。
     * 适用于已经构建好的 AccountProvider 对象或需要精确控制属性的场景。
     *
     * @param data 完整的账户提供者对象
     */
    suspend fun setAccountProvider(data: AccountProvider) {
        accountProvider.emit(data)
    }

    /**
     * 根据 URL 创建账户提供者对象
     *
     * 该私有辅助方法将 URL 字符串转换为 AccountProvider 对象，
     * 并根据 URL 判断服务器类型：
     * - 如果是 matrix.org，则标记为公共服务器和 MatrixOrg 服务器
     * - 其他自定义 URL 标记为非公共服务器
     *
     * @param url homeserver 的 URL 地址
     * @return 配置好的 AccountProvider 对象
     */
    private fun createAccountProvider(url: String): AccountProvider {
        return AccountProvider(
            url = url,
            subtitle = null,
            isPublic = url == AuthenticationConfig.MATRIX_ORG_URL,
            isMatrixOrg = url == AuthenticationConfig.MATRIX_ORG_URL,
        )
    }
}
