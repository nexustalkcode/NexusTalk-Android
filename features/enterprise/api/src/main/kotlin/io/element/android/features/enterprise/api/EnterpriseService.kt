/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.api

import androidx.compose.ui.graphics.Color
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.Flow

/**
 * 企业服务接口
 *
 * 定义企业级功能的服务接口，包括：
 * - 企业版构建识别
 * - 用户企业身份验证
 * - 默认服务器列表管理
 * - 服务器连接策略
 * - 品牌颜色定制
 * - 语义颜色配置
 * - 推送网关设置
 * - 问题报告 URL 配置
 *
 * 该接口用于支持企业环境的定制化配置和功能控制。
 *
 * @see DefaultEnterpriseService 默认实现
 */
interface EnterpriseService {
    /** 是否为企业版构建 */
    val isEnterpriseBuild: Boolean

    /**
     * 检查用户是否为企业用户
     *
     * @param sessionId 会话 ID
     * @return 是否为企业用户
     */
    suspend fun isEnterpriseUser(sessionId: SessionId): Boolean

    /**
     * 获取默认 homeserver 列表
     *
     * @return 默认 homeserver URL 列表
     */
    fun defaultHomeserverList(): List<String>

    /**
     * 检查是否允许连接到指定 homeserver
     *
     * @param homeserverUrl homeserver URL
     * @return 是否允许连接
     */
    suspend fun isAllowedToConnectToHomeserver(homeserverUrl: String): Boolean

    /**
     * 覆盖品牌颜色
     *
     * 设置指定会话或全局的品牌颜色。
     *
     * @param sessionId 会话 ID，null 表示设置全局品牌颜色
     * @param brandColor 十六进制格式的颜色值（#RRGGBBAA 或 #RRGGBB），null 表示重置为默认值
     */
    suspend fun overrideBrandColor(sessionId: SessionId?, brandColor: String?)

    /**
     * 品牌颜色流
     *
     * @param sessionId 会话 ID，null 表示获取全局品牌颜色
     * @return 品牌颜色流
     */
    fun brandColorsFlow(sessionId: SessionId?): Flow<Color?>

    /**
     * 语义颜色流
     *
     * @param sessionId 会话 ID，null 表示获取全局语义颜色
     * @return 语义颜色流
     */
    fun semanticColorsFlow(sessionId: SessionId?): Flow<SemanticColorsLightDark>

    /**
     * 获取 Firebase 推送网关 URL
     *
     * @return Firebase 推送网关 URL，如果未配置则返回 null
     */
    fun firebasePushGateway(): String?

    /**
     * 获取 UnifiedPush 默认推送网关 URL
     *
     * @return UnifiedPush 默认推送网关 URL，如果未配置则返回 null
     */
    fun unifiedPushDefaultPushGateway(): String?

    /**
     * 问题报告 URL 流
     *
     * @param sessionId 会话 ID，null 表示获取全局问题报告 URL
     * @return 问题报告 URL 流
     */
    fun bugReportUrlFlow(sessionId: SessionId?): Flow<BugReportUrl>

    /**
     * 伴对象
     */
    companion object {
        /** 任意账户提供者标识 */
        const val ANY_ACCOUNT_PROVIDER = "*"
    }
}

/**
 * 检查是否可以连接到任意 homeserver
 *
 * 扩展函数，判断当前配置是否允许连接到任意 homeserver。
 *
 * @return 是否可以连接任意 homeserver
 */
fun EnterpriseService.canConnectToAnyHomeserver(): Boolean {
    return defaultHomeserverList().let {
        it.isEmpty() || it.contains(EnterpriseService.ANY_ACCOUNT_PROVIDER)
    }
}
