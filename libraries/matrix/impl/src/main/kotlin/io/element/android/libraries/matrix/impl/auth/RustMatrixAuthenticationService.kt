/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.mapFailure
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.auth.AuthenticationException
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.auth.MatrixHomeServerDetails
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.matrix.api.auth.OidcPrompt
import io.element.android.libraries.matrix.api.auth.SessionRestorationException
import io.element.android.libraries.matrix.api.auth.external.ExternalSession
import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData
import io.element.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.verification.SessionVerifiedStatus
import io.element.android.libraries.matrix.impl.ClientBuilderSlidingSync
import io.element.android.libraries.matrix.impl.RustMatrixClientFactory
import io.element.android.libraries.matrix.impl.auth.qrlogin.QrErrorMapper
import io.element.android.libraries.matrix.impl.auth.qrlogin.SdkQrCodeLoginData
import io.element.android.libraries.matrix.impl.auth.qrlogin.toStep
import io.element.android.libraries.matrix.impl.exception.mapClientException
import io.element.android.libraries.matrix.impl.keys.PassphraseGenerator
import io.element.android.libraries.matrix.impl.mapper.toSessionData
import io.element.android.libraries.matrix.impl.paths.SessionPaths
import io.element.android.libraries.matrix.impl.paths.SessionPathsFactory
import io.element.android.libraries.matrix.impl.toSession
import io.element.android.libraries.sessionstorage.api.LoginType
import io.element.android.libraries.sessionstorage.api.SessionStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.matrix.rustcomponents.sdk.Client
import org.matrix.rustcomponents.sdk.ClientBuilder
import org.matrix.rustcomponents.sdk.HumanQrLoginException
import org.matrix.rustcomponents.sdk.QrCodeData
import org.matrix.rustcomponents.sdk.QrCodeDecodeException
import org.matrix.rustcomponents.sdk.QrLoginProgress
import org.matrix.rustcomponents.sdk.QrLoginProgressListener
import timber.log.Timber
import uniffi.matrix_sdk.OAuthAuthorizationData
import kotlin.time.Duration.Companion.seconds

/**
 * Rust Matrix SDK 认证服务实现类
 *
 * 该类是 [MatrixAuthenticationService] 接口的 Rust 实现，负责管理与 Matrix Homeserver
 * 的认证交互。主要功能包括：
 *
 * 1. **会话恢复**：从本地存储恢复已登录的会话
 * 2. **密码登录**：使用用户名和密码登录
 * 3. **OIDC 登录**：使用 OpenID Connect 进行无密码登录
 * 4. **二维码登录**：通过扫描二维码快速登录
 * 5. **外部会话导入**：从其他认证方式导入会话
 *
 * 使用 @ContributesBinding 和 @SingleIn 注解将此类绑定到 AppScope，
 * 确保整个应用只有一个认证服务实例。
 *
 * @property sessionPathsFactory 会话路径工厂，用于创建和管理会话数据存储路径
 * @property coroutineDispatchers 协程调度器，用于在正确的线程执行操作
 * @property sessionStore 会话存储，用于持久化用户会话数据
 * @property rustMatrixClientFactory Rust Matrix 客户端工厂
 * @property passphraseGenerator 密码生成器，用于加密会话数据
 * @property oidcConfigurationProvider OIDC 配置提供者
 *
 * @see MatrixAuthenticationService 认证服务接口
 * @see <a href="https://matrix.org/docs/guides/authentication">Matrix 认证指南</a>
 */
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RustMatrixAuthenticationService(
    private val sessionPathsFactory: SessionPathsFactory,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val sessionStore: SessionStore,
    private val rustMatrixClientFactory: RustMatrixClientFactory,
    private val passphraseGenerator: PassphraseGenerator,
    private val oidcConfigurationProvider: OidcConfigurationProvider,
) : MatrixAuthenticationService {

    // 用于加密新会话数据库的密码短语。已有会话会使用存储在 SessionData 中的密码短语。
    private val pendingPassphrase = getDatabasePassphrase()

    // 需要保留当前会话路径的引用，以便最终删除它。
    // 理想情况下可以从 Client 获取 sessionPath 以避免这样做。
    private var sessionPaths: SessionPaths? = null

    // 当前活动的客户端，用于登录流程
    private var currentClient: Client? = null

    // 新 Matrix 客户端的观察者列表
    private val newMatrixClientObservers = mutableListOf<(MatrixClient) -> Unit>()

    /**
     * 注册新客户端观察者
     *
     * 当创建新的 Matrix 客户端时（例如登录成功后），所有注册的观察者都会被调用。
     * 这允许其他组件（如消息服务）及时获取并处理新客户端。
     *
     * @param lambda 接收 MatrixClient 的回调函数
     */
    override fun listenToNewMatrixClients(lambda: (MatrixClient) -> Unit) {
        newMatrixClientObservers.add(lambda)
    }

    /**
     * 轮换会话路径
     *
     * 删除旧的会话路径（如果存在），并创建新的会话路径。
     * 这在设置新的 Homeserver 或开始新的登录流程时调用。
     *
     * @return 新的会话路径对象
     */
    private fun rotateSessionPath(): SessionPaths {
        sessionPaths?.deleteRecursively()
        return sessionPathsFactory.create()
            .also { sessionPaths = it }
    }

    /**
     * 恢复已存在的会话
     *
     * 根据会话 ID 从本地存储恢复之前登录的会话。
     * 如果会话的访问令牌仍然有效，则重建 Matrix 客户端。
     *
     * @param sessionId 要恢复的会话 ID
     * @return Result<MatrixClient> 成功时返回重建的客户端，失败时返回错误
     *
     * @throws SessionRestorationException.MissingSession 会话不存在
     * @throws SessionRestorationException.InvalidToken 访问令牌无效
     */
    override suspend fun restoreSession(sessionId: SessionId): Result<MatrixClient> = withContext(coroutineDispatchers.io) {
        runCatchingExceptions {
            val sessionData = sessionStore.getSession(sessionId.value)
            if (sessionData != null) {
                if (sessionData.isTokenValid) {
                    // Use the sessionData.passphrase, which can be null for a previously created session
                    if (sessionData.passphrase == null) {
                        Timber.w("Restoring a session without a passphrase")
                    } else {
                        Timber.w("Restoring a session with a passphrase")
                    }
                    rustMatrixClientFactory.create(sessionData)
                } else {
                    throw SessionRestorationException.InvalidToken()
                }
            } else {
                throw SessionRestorationException.MissingSession(sessionId)
            }
        }.mapFailure { failure ->
            failure.mapClientException()
        }
    }

    /**
     * 生成数据库加密密码短语
     *
     * 为新会话生成一个密码短语，用于加密本地数据库。
     * 如果返回 null，则表示不使用密码短语加密。
     *
     * @return 密码短语字符串，或 null
     */
    private fun getDatabasePassphrase(): String? {
        val passphrase = passphraseGenerator.generatePassphrase()
        if (passphrase != null) {
            Timber.w("New sessions will be encrypted with a passphrase")
        }
        return passphrase
    }

    /**
     * 设置 Homeserver
     *
     * 配置并连接到指定的 Homeserver，获取其支持的登录方式等信息。
     *
     * @param homeserver Homeserver 的 URL 或域名
     * @return Result<MatrixHomeServerDetails> 成功时返回 Homeserver 详情，失败时返回错误
     */
    override suspend fun setHomeserver(homeserver: String): Result<MatrixHomeServerDetails> =
        withContext(coroutineDispatchers.io) {
            val emptySessionPath = rotateSessionPath()
            runCatchingExceptions {
                val client = makeClient(sessionPaths = emptySessionPath) {
                    serverNameOrHomeserverUrl(homeserver)
                }

                currentClient = client
                client.homeserverLoginDetails().map()
            }.onFailure {
                clear()
            }.mapFailure { failure ->
                Timber.e(failure, "Failed to set homeserver to $homeserver")
                failure.mapAuthenticationException()
            }
        }

    /**
     * 使用密码登录
     *
     * 使用用户名和密码向 Homeserver 进行身份验证。
     *
     * @param username 用户名（通常是 Matrix ID，如 @user:matrix.org）
     * @param password 密码
     * @return Result<SessionId> 成功时返回新会话的 ID，失败时返回错误
     */
    override suspend fun login(username: String, password: String): Result<SessionId> =
        withContext(coroutineDispatchers.io) {
            runCatchingExceptions {
                val client = currentClient ?: error("You need to call `setHomeserver()` first")
                val currentSessionPaths = sessionPaths ?: error("You need to call `setHomeserver()` first")
                client.login(username, password, "Element X Android", null)
                // Ensure that the user is not already logged in with the same account
                ensureNotAlreadyLoggedIn(client)
                val sessionData = client.session()
                    .toSessionData(
                        isTokenValid = true,
                        loginType = LoginType.PASSWORD,
                        passphrase = pendingPassphrase,
                        sessionPaths = currentSessionPaths,
                    )
                val matrixClient = rustMatrixClientFactory.create(client)
                newMatrixClientObservers.forEach { it.invoke(matrixClient) }
                sessionStore.addSession(sessionData)

                // Clean up the strong reference held here since it's no longer necessary
                currentClient = null

                SessionId(sessionData.userId)
            }.mapFailure { failure ->
                Timber.e(failure, "Failed to login")
                failure.mapAuthenticationException()
            }
        }

    /**
     * 导入外部创建的会话
     *
     * 从外部认证流程（如 SSO）获取的会话数据中恢复会话。
     *
     * @param externalSession 外部会话数据
     * @return Result<SessionId> 成功时返回会话 ID，失败时返回错误
     */
    override suspend fun importCreatedSession(externalSession: ExternalSession): Result<SessionId> =
        withContext(coroutineDispatchers.io) {
            runCatchingExceptions {
                val client = currentClient ?: error("You need to call `setHomeserver()` first")
                val currentSessionPaths = sessionPaths ?: error("You need to call `setHomeserver()` first")
                val sessionData = externalSession.toSessionData(
                    isTokenValid = true,
                    loginType = LoginType.PASSWORD,
                    passphrase = pendingPassphrase,
                    sessionPaths = currentSessionPaths,
                )

                // We restore the client using the just retrieved session data
                client.restoreSession(sessionData.toSession())
                val matrixClient = rustMatrixClientFactory.create(client)

                // We wait for the verification state to be known
                matrixClient.waitForKnownVerificationState()

                // And once it's ready we share it and save the actual session data
                newMatrixClientObservers.forEach { it.invoke(matrixClient) }
                sessionStore.addSession(sessionData)

                // Clean up the strong reference held here since it's no longer necessary
                currentClient = null

                SessionId(sessionData.userId)
            }
        }

    // 待处理的 OIDC 授权数据
    private var pendingOAuthAuthorizationData: OAuthAuthorizationData? = null

    /**
     * 获取 OIDC 登录 URL
     *
     * 生成用于 OIDC 认证的 URL，用户将在浏览器中访问此 URL 进行身份验证。
     *
     * @param prompt OIDC 提示类型，指定认证行为
     * @param loginHint 登录提示，可预填充用户名
     * @return Result<OidcDetails> 成功时返回包含登录 URL 的 OidcDetails，失败时返回错误
     */
    override suspend fun getOidcUrl(
        prompt: OidcPrompt,
        loginHint: String?,
    ): Result<OidcDetails> {
        return withContext(coroutineDispatchers.io) {
            runCatchingExceptions {
                val client = currentClient ?: error("You need to call `setHomeserver()` first")
                val oAuthAuthorizationData = client.urlForOidc(
                    oidcConfiguration = oidcConfigurationProvider.get(),
                    prompt = prompt.toRustPrompt(),
                    loginHint = loginHint,
                    // If we want to restore a previous session for which we have encryption keys, we can pass the deviceId here. At the moment, we don't
                    deviceId = null,
                    additionalScopes = emptyList(),
                )
                val url = oAuthAuthorizationData.loginUrl()
                pendingOAuthAuthorizationData = oAuthAuthorizationData
                OidcDetails(url)
            }.mapFailure { failure ->
                Timber.e(failure, "Failed to get OIDC URL")
                failure.mapAuthenticationException()
            }
        }
    }

    /**
     * 取消 OIDC 登录
     *
     * 中止正在进行的 OIDC 认证流程。
     *
     * @return Result<Unit> 成功时返回空结果，失败时返回错误
     */
    override suspend fun cancelOidcLogin(): Result<Unit> {
        return withContext(coroutineDispatchers.io) {
            runCatchingExceptions {
                pendingOAuthAuthorizationData?.use {
                    currentClient?.abortOidcAuth(it)
                }
                pendingOAuthAuthorizationData = null
            }.mapFailure { failure ->
                Timber.e(failure, "Failed to cancel OIDC login")
                failure.mapAuthenticationException()
            }
        }
    }

    /**
     * callbackUrl should be the uriRedirect from OidcClientMetadata (with all the parameters).
     */
    /**
     * 使用 OIDC 回调登录
     *
     * 处理身份验证提供商返回的回调 URL，完成 OIDC 认证流程。
     *
     * @param callbackUrl 包含认证参数的回调 URL（来自 OidcClientMetadata 的 uriRedirect）
     * @return Result<SessionId> 成功时返回会话 ID，失败时返回错误
     */
    override suspend fun loginWithOidc(callbackUrl: String): Result<SessionId> {
        return withContext(coroutineDispatchers.io) {
            runCatchingExceptions {
                val client = currentClient ?: error("You need to call `setHomeserver()` first")
                val currentSessionPaths = sessionPaths ?: error("You need to call `setHomeserver()` first")
                client.loginWithOidcCallback(callbackUrl)

                // Free the pending data since we won't use it to abort the flow anymore
                pendingOAuthAuthorizationData?.close()
                pendingOAuthAuthorizationData = null

                // Ensure that the user is not already logged in with the same account
                ensureNotAlreadyLoggedIn(client)
                val sessionData = client.session().toSessionData(
                    isTokenValid = true,
                    loginType = LoginType.OIDC,
                    passphrase = pendingPassphrase,
                    sessionPaths = currentSessionPaths,
                )
                val matrixClient = rustMatrixClientFactory.create(client)
                matrixClient.waitForKnownVerificationState()

                newMatrixClientObservers.forEach { it.invoke(matrixClient) }
                sessionStore.addSession(sessionData)

                // Clean up the strong reference held here since it's no longer necessary
                currentClient = null

                SessionId(sessionData.userId)
            }.mapFailure { failure ->
                Timber.e(failure, "Failed to login with OIDC")
                failure.mapAuthenticationException()
            }
        }
    }

    /**
     * 确保用户未使用同一账户重复登录
     *
     * 检查是否已存在相同用户的会话，如果存在则登出该客户端并抛出异常。
     *
     * @param client 要检查的 Matrix 客户端
     * @throws AuthenticationException.AccountAlreadyLoggedIn 如果账户已登录
     */
    @Throws(AuthenticationException.AccountAlreadyLoggedIn::class)
    private suspend fun ensureNotAlreadyLoggedIn(client: Client) {
        val newUserId = client.userId()
        val accountAlreadyLoggedIn = sessionStore.getAllSessions().any {
            it.userId == newUserId
        }
        if (accountAlreadyLoggedIn) {
            // Sign out the client, ignoring any error
            runCatchingExceptions {
                client.logout()
            }
            throw AuthenticationException.AccountAlreadyLoggedIn(newUserId)
        }
    }

    /**
     * 使用二维码登录
     *
     * 通过扫描桌面端或其他设备显示的二维码来登录。
     *
     * @param qrCodeData 从二维码扫描获取的登录数据
     * @param progress 登录进度回调，用于更新 UI
     * @return Result<SessionId> 成功时返回会话 ID，失败时返回错误
     */
    override suspend fun loginWithQrCode(qrCodeData: MatrixQrCodeLoginData, progress: (QrCodeLoginStep) -> Unit) =
        withContext(coroutineDispatchers.io) {
            val sdkQrCodeLoginData = (qrCodeData as SdkQrCodeLoginData).rustQrCodeData
            val emptySessionPaths = rotateSessionPath()
            val oidcConfiguration = oidcConfigurationProvider.get()
            val progressListener = object : QrLoginProgressListener {
                override fun onUpdate(state: QrLoginProgress) {
                    Timber.d("QR Code login progress: $state")
                    progress(state.toStep())
                }
            }
            runCatchingExceptions {
                val client = makeQrCodeLoginClient(
                    sessionPaths = emptySessionPaths,
                    qrCodeData = sdkQrCodeLoginData,
                )
                client.newLoginWithQrCodeHandler(
                    oidcConfiguration = oidcConfiguration,
                ).use {
                    it.scan(
                        qrCodeData = qrCodeData.rustQrCodeData,
                        progressListener = progressListener,
                    )
                }
                // Ensure that the user is not already logged in with the same account
                ensureNotAlreadyLoggedIn(client)
                val sessionData = client.session()
                    .toSessionData(
                        isTokenValid = true,
                        loginType = LoginType.QR,
                        passphrase = pendingPassphrase,
                        sessionPaths = emptySessionPaths,
                    )
                val matrixClient = rustMatrixClientFactory.create(client)
                newMatrixClientObservers.forEach { it.invoke(matrixClient) }
                sessionStore.addSession(sessionData)

                // Clean up the strong reference held here since it's no longer necessary
                currentClient = null

                SessionId(sessionData.userId)
            }.mapFailure {
                when (it) {
                    is QrCodeDecodeException -> QrErrorMapper.map(it)
                    is HumanQrLoginException -> QrErrorMapper.map(it)
                    else -> it
                }
            }.onFailure { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                Timber.e(throwable, "Failed to login with QR code")
            }
        }

    /**
     * 创建 Matrix 客户端
     *
     * 使用基础构建器创建配置好的 Matrix 客户端。
     *
     * @param sessionPaths 会话路径
     * @param config 客户端配置lambda
     * @return 配置好的客户端
     */
    private suspend fun makeClient(
        sessionPaths: SessionPaths,
        config: suspend ClientBuilder.() -> ClientBuilder,
    ): Client {
        Timber.d("Creating client with simplified sliding sync")
        return rustMatrixClientFactory
            .getBaseClientBuilder(
                sessionPaths = sessionPaths,
                passphrase = pendingPassphrase,
                slidingSyncType = ClientBuilderSlidingSync.Discovered,
            )
            .config()
            .build()
    }

    /**
     * 创建用于二维码登录的 Matrix 客户端
     *
     * @param sessionPaths 会话路径
     * @param qrCodeData 二维码数据
     * @return 配置好的客户端
     */
    private suspend fun makeQrCodeLoginClient(
        sessionPaths: SessionPaths,
        qrCodeData: QrCodeData,
    ): Client {
        Timber.d("Creating client for QR Code login with simplified sliding sync")
        return rustMatrixClientFactory
            .getBaseClientBuilder(
                sessionPaths = sessionPaths,
                passphrase = pendingPassphrase,
                slidingSyncType = ClientBuilderSlidingSync.Discovered,
            )
            .serverNameOrHomeserverUrl(qrCodeData.serverName()!!)
            .build()
    }

    /**
     * 清理当前客户端
     *
     * 关闭并释放当前客户端资源。
     */
    private fun clear() {
        currentClient?.close()
        currentClient = null
    }

    /**
     * 等待会话验证状态已知
     *
     * 等待会话的验证状态确定（已知或未知），超时时间为10秒。
     * 这确保在继续之前，设备验证状态已被正确加载。
     *
     * @receiver MatrixClient 实例
     */
    private suspend fun MatrixClient.waitForKnownVerificationState() {
        withTimeoutOrNull(10.seconds) {
            Timber.d("Waiting for a known verification status...")
            val status = sessionVerificationService.sessionVerifiedStatus.first { it != SessionVerifiedStatus.Unknown }
            Timber.d("Finished waiting for a known verification status: $status")
        } ?: Timber.w("Timed out waiting for a known verification status")
    }
}
