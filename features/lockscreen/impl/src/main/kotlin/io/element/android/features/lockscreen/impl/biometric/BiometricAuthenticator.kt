/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.biometric

import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.cryptography.api.EncryptionDecryptionService
import io.element.android.libraries.cryptography.api.SecretKeyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import timber.log.Timber
import java.security.InvalidKeyException
import javax.crypto.Cipher

/**
 * 生物识别认证器接口
 *
 * 提供生物识别认证功能，支持指纹、面部识别等生物特征验证。
 */
interface BiometricAuthenticator {
    /**
     * 生物识别认证回调接口
     *
     * 用于接收生物识别认证过程中的各种事件。
     */
    interface Callback {
        /**
         * 生物识别设置错误时调用
         *
         * 当生物识别密钥无效或出现问题时触发。
         */
        fun onBiometricSetupError()

        /**
         * 生物识别认证成功时调用
         */
        fun onBiometricAuthenticationSuccess()

        /**
         * 生物识别认证失败时调用
         *
         * @param error 失败原因的错误对象（可选）
         */
        fun onBiometricAuthenticationFailed(error: Exception?)
    }

    /**
     * 生物识别认证结果密封接口
     */
    sealed interface AuthenticationResult {
        /** 认证成功 */
        data object Success : AuthenticationResult
        /** 认证失败
         * @param error 失败原因的错误对象（可选）
         */
        data class Failure(val error: Exception? = null) : AuthenticationResult
    }

    /**
     * 认证器是否处于活跃状态
     *
     * 如果为 true，表示生物识别可用于解锁应用。
     */
    val isActive: Boolean

    /**
     * 设置生物识别认证
     *
     * 初始化加密密钥，为认证做准备。
     */
    fun setup()

    /**
     * 执行生物识别认证
     *
     * @return 认证结果，表示成功或失败
     */
    suspend fun authenticate(): AuthenticationResult
}

/**
 * 无操作生物识别认证实现
 *
 * 当设备不支持生物识别时使用的默认实现，不执行任何实际操作。
 */
class NoopBiometricAuthentication : BiometricAuthenticator {
    override val isActive: Boolean = false
    override fun setup() = Unit
    override suspend fun authenticate() = BiometricAuthenticator.AuthenticationResult.Failure()
}

/**
 * 默认生物识别认证实现
 *
 * 使用 Android BiometricPrompt API 实现完整的生物识别认证流程，
 * 配合加密服务确保认证安全性。
 *
 * @param activity FragmentActivity 实例，用于显示生物识别对话框
 * @param promptInfo 生物识别提示信息配置
 * @param secretKeyRepository 密钥仓库，用于管理加密密钥
 * @param encryptionDecryptionService 加解密服务
 * @param keyAlias 密钥别名
 * @param callbacks 认证回调列表
 */
class DefaultBiometricAuthentication(
    private val activity: FragmentActivity,
    private val promptInfo: PromptInfo,
    private val secretKeyRepository: SecretKeyRepository,
    private val encryptionDecryptionService: EncryptionDecryptionService,
    private val keyAlias: String,
    private val callbacks: List<BiometricAuthenticator.Callback>
) : BiometricAuthenticator {
    override val isActive: Boolean = true

    /** 加密对象，用于生物识别认证 */
    private var cryptoObject: CryptoObject? = null

    override fun setup() {
        try {
            val secretKey = ensureKey()
            val cipher = encryptionDecryptionService.createEncryptionCipher(secretKey)
            cryptoObject = CryptoObject(cipher)
        } catch (e: InvalidKeyException) {
            callbacks.forEach { it.onBiometricSetupError() }
            Timber.e(e, "Invalid biometric key")
        }
    }

    override suspend fun authenticate(): BiometricAuthenticator.AuthenticationResult {
        val cryptoObject = cryptoObject ?: return BiometricAuthenticator.AuthenticationResult.Failure()

        val deferredAuthenticationResult = CompletableDeferred<BiometricAuthenticator.AuthenticationResult>()
        val executor = ContextCompat.getMainExecutor(activity.baseContext)
        val callback = AuthenticationCallback(callbacks, deferredAuthenticationResult)
        val prompt = BiometricPrompt(activity, executor, callback)
        prompt.authenticate(promptInfo, cryptoObject)
        return try {
            deferredAuthenticationResult.await()
        } catch (cancellation: CancellationException) {
            prompt.cancelAuthentication()
            BiometricAuthenticator.AuthenticationResult.Failure(cancellation)
        }
    }

    @Throws(KeyPermanentlyInvalidatedException::class)
    private fun ensureKey() = secretKeyRepository.getOrCreateKey(keyAlias, true).also {
        encryptionDecryptionService.createEncryptionCipher(it)
    }
}

/**
 * 内部认证回调类
 *
 * 处理 BiometricPrompt 的各种认证事件，并将结果传递给注册的回调。
 *
 * @param callbacks 回调列表
 * @param deferredAuthenticationResult 用于异步返回认证结果的 Deferred 对象
 */
private class AuthenticationCallback(
    private val callbacks: List<BiometricAuthenticator.Callback>,
    private val deferredAuthenticationResult: CompletableDeferred<BiometricAuthenticator.AuthenticationResult>,
) : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        val biometricUnlockError = BiometricUnlockError(errorCode, errString.toString())
        callbacks.forEach { it.onBiometricAuthenticationFailed(biometricUnlockError) }
        deferredAuthenticationResult.complete(BiometricAuthenticator.AuthenticationResult.Failure(biometricUnlockError))
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        callbacks.forEach { it.onBiometricAuthenticationFailed(null) }
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        if (result.cryptoObject?.cipher.isValid()) {
            callbacks.forEach { it.onBiometricAuthenticationSuccess() }
            deferredAuthenticationResult.complete(BiometricAuthenticator.AuthenticationResult.Success)
        } else {
            val error = IllegalStateException("Invalid cipher")
            callbacks.forEach { it.onBiometricAuthenticationFailed(error) }
            deferredAuthenticationResult.complete(BiometricAuthenticator.AuthenticationResult.Failure())
        }
    }

    private fun Cipher?.isValid(): Boolean {
        if (this == null) return false
        return runCatchingExceptions {
            doFinal("biometric_challenge".toByteArray())
        }.isSuccess
    }
}
