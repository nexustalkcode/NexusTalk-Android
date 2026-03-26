/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.analytics

import im.vector.app.features.analytics.plan.Error
import io.element.android.services.analytics.api.AnalyticsService
import org.matrix.rustcomponents.sdk.UnableToDecryptDelegate
import org.matrix.rustcomponents.sdk.UnableToDecryptInfo
import timber.log.Timber
import uniffi.matrix_sdk_crypto.UtdCause

/**
 * 无法解密消息 (UTD) 追踪器
 *
 * UTD = Unable To Decrypt，即"无法解密"。这是 Matrix 端到端加密 (E2EE) 中的一个重要概念。
 * 当用户收到一条加密消息但无法解密时（例如因为缺少密钥），该类负责：
 *
 * 1. 监听 Rust SDK 报告的无法解密事件
 * 2. 根据不同的失败原因，将事件分类并映射到相应的分析指标
 * 3. 将分析事件上报到分析服务，用于监控和优化 E2EE 体验
 *
 * 该类实现 [UnableToDecryptDelegate] 接口，这是 Rust SDK 提供的回调接口，
 * 用于接收无法解密事件的通知。
 *
 * @property analyticsService 分析服务，用于上报无法解密事件的统计数据
 *
 * @see UnableToDecryptDelegate Rust SDK 提供的无法解密回调接口
 * @see <a href="https://matrix.org/docs/e2ee/">Matrix E2EE 加密文档</a>
 */
class UtdTracker(
    private val analyticsService: AnalyticsService,
) : UnableToDecryptDelegate {

    /**
     * 处理无法解密事件
     *
     * 当 Rust Matrix SDK 无法解密一条消息时，会调用此回调方法。
     * 方法会：
     * 1. 根据失败原因 (UtdCause) 确定错误类型
     * 2. 构建包含详细信息的分析事件
     * 3. 将事件上报到分析服务
     *
     * @param info 包含无法解密事件详细信息的对象，包括：
     *             - eventId: 事件 ID
     *             - timeToDecryptMs: 尝试解密耗时（毫秒）
     *             - cause: 导致无法解密的具体原因
     *             - eventLocalAgeMillis: 消息本地存在时间
     *             - userTrustsOwnIdentity: 用户是否信任自己的身份
     *             - ownHomeserver: 用户自己的 homeserver
     *             - senderHomeserver: 发送者的 homeserver
     */
    override fun onUtd(info: UnableToDecryptInfo) {
        Timber.d("onUtd for event ${info.eventId}, timeToDecryptMs: ${info.timeToDecryptMs}")

        // 根据失败原因映射到分析系统中的错误名称
        val name = when (info.cause) {
            // 未知原因 - 通常是 Olm 密钥未发送
            UtdCause.UNKNOWN -> Error.Name.OlmKeysNotSentError

            // 消息发送时我们还未加入房间（我们后来才加入）
            UtdCause.SENT_BEFORE_WE_JOINED -> Error.Name.ExpectedDueToMembership

            // 验证失败 - 设备验证不通过
            UtdCause.VERIFICATION_VIOLATION -> Error.Name.ExpectedVerificationViolation

            // 设备未签名或设备未知 - 使用不安全的设备发送
            UtdCause.UNSIGNED_DEVICE,
            UtdCause.UNKNOWN_DEVICE -> {
                Error.Name.ExpectedSentByInsecureDevice
            }

            // 历史消息且备份已禁用
            UtdCause.HISTORICAL_MESSAGE_AND_BACKUP_IS_DISABLED,
            // 历史消息且设备未验证
            UtdCause.HISTORICAL_MESSAGE_AND_DEVICE_IS_UNVERIFIED,
                -> Error.Name.HistoricalMessage

            // 密钥因设备未验证或不安全而被扣留
            UtdCause.WITHHELD_FOR_UNVERIFIED_OR_INSECURE_DEVICE -> Error.Name.RoomKeysWithheldForUnverifiedDevice

            // 发送者扣留了密钥
            UtdCause.WITHHELD_BY_SENDER -> Error.Name.OlmKeysNotSentError
        }

        // 构建分析事件对象，包含所有相关指标
        val event = Error(
            context = null,                              // 上下文（未使用）
            // 保留 cryptoModule 字段以保持兼容性
            cryptoModule = Error.CryptoModule.Rust,       // 加密模块：Rust
            cryptoSDK = Error.CryptoSDK.Rust,             // 加密 SDK：Rust
            timeToDecryptMillis = info.timeToDecryptMs?.toInt() ?: -1, // 解密耗时（毫秒）
            domain = Error.Domain.E2EE,                  // 错误领域：端到端加密
            name = name,                                  // 错误名称
            eventLocalAgeMillis = info.eventLocalAgeMillis.toInt(), // 消息本地存在时间
            userTrustsOwnIdentity = info.userTrustsOwnIdentity, // 用户是否信任自己的身份
            isFederated = info.ownHomeserver != info.senderHomeserver, // 是否为联邦房间
            isMatrixDotOrg = info.ownHomeserver == "matrix.org", // 是否为 matrix.org 服务器
        )

        // 上报分析事件
        analyticsService.capture(event)
    }
}
