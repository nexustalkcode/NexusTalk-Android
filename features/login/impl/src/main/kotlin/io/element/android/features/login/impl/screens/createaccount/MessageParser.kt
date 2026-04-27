/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.createaccount

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.libraries.androidutils.json.JsonProvider
import io.element.android.libraries.matrix.api.auth.external.ExternalSession

/**
 * 解析注册页面通过消息通道回传的结果。
 */
interface MessageParser {
    /**
     * 解析消息并返回 [ExternalSession]。
     *
     * @param message 注册页回传的原始消息。
     * @throws Throwable 当消息内容非法或缺少关键字段时抛出异常。
     */
    fun parse(message: String): ExternalSession
}

@ContributesBinding(AppScope::class)
/**
 * [MessageParser] 的默认实现。
 */
class DefaultMessageParser(
    private val accountProviderDataSource: AccountProviderDataSource,
    private val json: JsonProvider,
) : MessageParser {
    /**
     * 把注册页 JSON 响应解析成可导入的外部会话。
     */
    override fun parse(message: String): ExternalSession {
        val response = json().decodeFromString(MobileRegistrationResponse.serializer(), message)
        val userId = response.userId ?: error("No user ID in response")
        val homeServer = response.homeServer ?: accountProviderDataSource.flow.value.url
        val accessToken = response.accessToken ?: error("No access token in response")
        val deviceId = response.deviceId ?: error("No device ID in response")
        return ExternalSession(
            userId = userId,
            homeserverUrl = homeServer,
            accessToken = accessToken,
            deviceId = deviceId,
            refreshToken = null,
        )
    }
}
