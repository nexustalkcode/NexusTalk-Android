/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.impl

import androidx.compose.ui.graphics.Color
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.enterprise.api.BugReportUrl
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * EnterpriseService 的默认实现（非企业版）
 *
 * 提供企业服务的默认实现，适用于非企业版构建。
 * 所有企业级功能在此实现中返回默认值或基础行为。
 *
 * @see EnterpriseService 企业服务接口
 */
@ContributesBinding(AppScope::class)
class DefaultEnterpriseService : EnterpriseService {
    /** 是否为企业版构建，默认为 false */
    override val isEnterpriseBuild = false

    /**
     * 检查用户是否为企业用户
     *
     * 非企业版始终返回 false
     *
     * @param sessionId 会话 ID（未使用）
     * @return 是否为企业用户，恒为 false
     */
    override suspend fun isEnterpriseUser(sessionId: SessionId) = false

    /**
     * 获取默认 homeserver 列表
     *
     * 非企业版返回空列表
     *
     * @return 空列表
     */
    //override fun defaultHomeserverList(): List<String> = listOf("https://nexustalk.space")
    override fun defaultHomeserverList(): List<String> = emptyList()

    /**
     * 检查是否允许连接到 homeserver
     *
     * 非企业版始终允许连接
     *
     * @param homeserverUrl homeserver URL（未使用）
     * @return 是否允许连接，恒为 true
     */
    override suspend fun isAllowedToConnectToHomeserver(homeserverUrl: String) = true

    /**
     * 覆盖品牌颜色
     *
     * 非企业版中此操作无效果
     *
     * @param sessionId 会话 ID（未使用）
     * @param brandColor 品牌颜色（未使用）
     */
    override suspend fun overrideBrandColor(sessionId: SessionId?, brandColor: String?) = Unit

    /**
     * 获取品牌颜色流
     *
     * 非企业版返回 null 颜色流
     *
     * @param sessionId 会话 ID（未使用）
     * @return 包含 null 的颜色流
     */
    override fun brandColorsFlow(sessionId: SessionId?): Flow<Color?> {
        return flowOf(null)
    }

    /**
     * 获取语义颜色流
     *
     * 非企业版返回默认语义颜色
     *
     * @param sessionId 会话 ID（未使用）
     * @return 默认语义颜色流
     */
    override fun semanticColorsFlow(sessionId: SessionId?): Flow<SemanticColorsLightDark> {
        return flowOf(SemanticColorsLightDark.default)
    }

    /**
     * 获取 Firebase 推送网关 URL
     *
     * 非企业版返回 null
     *
     * @return null
     */
    override fun firebasePushGateway(): String? = null

    /**
     * 获取 UnifiedPush 默认推送网关 URL
     *
     * 非企业版返回 null
     *
     * @return null
     */
    override fun unifiedPushDefaultPushGateway(): String? = null

    /**
     * 获取问题报告 URL 流
     *
     * 非企业版使用默认问题报告 URL
     *
     * @param sessionId 会话 ID（未使用）
     * @return 使用默认 URL 的流
     */
    override fun bugReportUrlFlow(sessionId: SessionId?): Flow<BugReportUrl> {
        return flowOf(BugReportUrl.UseDefault)
    }
}
