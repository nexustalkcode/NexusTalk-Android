/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.auth.HomeServerLoginCompatibilityChecker
import io.element.android.libraries.matrix.impl.ClientBuilderProvider
import io.element.android.libraries.matrix.impl.certificates.UserCertificatesProvider
import timber.log.Timber

/**
 * Rust SDK 实现的 Homeserver 登录兼容性检查器
 *
 * 该类是 [HomeServerLoginCompatibilityChecker] 接口的 Rust 实现，用于检查给定的
 * Homeserver URL 是否支持 Element X Android 应用的登录方式。
 *
 * 主要功能：
 * - 验证 Homeserver 是否可达
 * - 检查 Homeserver 是否支持密码登录
 * - 检查 Homeserver 是否支持 OIDC (OpenID Connect) 登录
 *
 * 如果 Homeserver 至少支持其中一种登录方式，则认为兼容。
 *
 * @property clientBuilderProvider 客户端构建器提供者，用于创建 Matrix 客户端
 * @property userCertificatesProvider 用户证书提供者，用于添加自定义根证书
 *
 * @see HomeServerLoginCompatibilityChecker 登录兼容性检查器接口
 */
@ContributesBinding(AppScope::class)
class RustHomeServerLoginCompatibilityChecker(
    private val clientBuilderProvider: ClientBuilderProvider,
    private val userCertificatesProvider: UserCertificatesProvider,
) : HomeServerLoginCompatibilityChecker {

    /**
     * 检查指定 Homeserver URL 是否兼容
     *
     * 该方法会：
     * 1. 使用给定的 URL 创建临时 Matrix 客户端
     * 2. 查询 Homeserver 支持的登录方式
     * 3. 判断是否至少支持密码登录或 OIDC 登录之一
     *
     * 注意：创建的客户端使用内存存储，不会持久化任何数据。
     *
     * @param url Homeserver 的 URL 地址（可以是域名或完整 URL）
     * @return Result<Boolean> 成功时返回是否兼容：
     *         - true: Homeserver 支持密码登录或 OIDC 登录
     *         - false: Homeserver 不支持任何登录方式
     *         失败时返回包含错误信息的 Result
     */
    override suspend fun check(url: String): Result<Boolean> = runCatchingExceptions {
        clientBuilderProvider.provide()
            .inMemoryStore()                              // 使用内存存储，不持久化
            .serverNameOrHomeserverUrl(url)               // 设置 Homeserver URL
            .addRootCertificates(userCertificatesProvider.provides()) // 添加自定义根证书
            .build()
            .use {
                it.homeserverLoginDetails()
            }
            .use {
                // 输出调试日志，记录 Homeserver 支持的登录方式
                Timber.d("Homeserver $url | OIDC: ${it.supportsOidcLogin()} | Password: ${it.supportsPasswordLogin()} | SSO: ${it.supportsSsoLogin()}")
                // 只要支持 OIDC 或密码登录之一，即认为兼容
                it.supportsOidcLogin() || it.supportsPasswordLogin()
            }
    }
}
