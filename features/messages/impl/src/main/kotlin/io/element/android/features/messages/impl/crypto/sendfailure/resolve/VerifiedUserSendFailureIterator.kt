/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import timber.log.Timber

/**
 * 已验证用户发送失败迭代器接口
 *
 * 用于遍历 [LocalEventSendState.Failed.VerifiedUser] 失败状态的迭代器接口。
 * 允许按步骤解决失败情况，例如逐个处理每个用户的失败。
 *
 * 迭代器设计模式使得可以：
 * - 逐个处理多个用户的失败情况
 * - 在解决完一个用户后继续处理下一个
 * - 提供统一的迭代接口给解决器使用
 *
 * @see LocalEventSendState.Failed.VerifiedUser 发送失败的基础类型
 * @see VerifiedUserSendFailureResolver 失败解决器，使用此迭代器
 */
interface VerifiedUserSendFailureIterator : Iterator<LocalEventSendState.Failed.VerifiedUser> {
    companion object {
        /**
         * 根据失败类型创建相应的迭代器
         *
         * 工厂方法，根据传入的失败对象类型返回对应的迭代器实现。
         *
         * @param failure 发送失败状态对象
         * @return 对应类型的迭代器实例
         * - [LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice] -> [UnsignedDeviceSendFailureIterator]
         * - [LocalEventSendState.Failed.VerifiedUserChangedIdentity] -> [ChangedIdentitySendFailureIterator]
         */
        fun from(failure: LocalEventSendState.Failed.VerifiedUser): VerifiedUserSendFailureIterator {
            return when (failure) {
                is LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice -> UnsignedDeviceSendFailureIterator(failure)
                is LocalEventSendState.Failed.VerifiedUserChangedIdentity -> ChangedIdentitySendFailureIterator(failure)
            }
        }
    }
}

/**
 * 未验证设备发送失败迭代器
 *
 * 用于遍历包含未验证设备的失败状态。
 * 每次迭代返回一个仅包含单个用户的失败对象，方便逐个处理每个用户的设备验证问题。
 *
 * @property failure 包含多个用户设备的失败状态
 */
class UnsignedDeviceSendFailureIterator(
    failure: LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice
) : VerifiedUserSendFailureIterator {
    private val iterator = failure.devices.iterator()

    /**
     * 初始化检查
     *
     * 确保失败状态中至少包含一个设备，如果为空则记录警告日志。
     */
    init {
        if (!hasNext()) {
            Timber.w("Got $failure without any devices, shouldn't happen.")
        }
    }

    /**
     * 检查是否还有下一个未处理的设备
     *
     * @return 布尔值，表示是否还有更多设备
     */
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    /**
     * 获取下一个未验证设备的失败状态
     *
     * 返回一个仅包含当前用户设备的失败对象。
     *
     * @return [LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice] 单用户设备失败对象
     */
    override fun next(): LocalEventSendState.Failed.VerifiedUser {
        val (userId, deviceIds) = iterator.next()
        return LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice(
            mapOf(userId to deviceIds)
        )
    }
}

/**
 * 身份变更发送失败迭代器
 *
 * 用于遍历包含身份变更用户的失败状态。
 * 每次迭代返回一个仅包含单个用户的失败对象，方便逐个处理每个用户的身份问题。
 *
 * @property failure 包含多个用户身份变更的失败状态
 */
class ChangedIdentitySendFailureIterator(
    failure: LocalEventSendState.Failed.VerifiedUserChangedIdentity
) : VerifiedUserSendFailureIterator {
    private val iterator = failure.users.iterator()

    /**
     * 初始化检查
     *
     * 确保失败状态中至少包含一个用户，如果为空则记录警告日志。
     */
    init {
        if (!hasNext()) {
            Timber.w("Got $failure without any users, shouldn't happen.")
        }
    }

    /**
     * 检查是否还有下一个未处理的用户
     *
     * @return 布尔值，表示是否还有更多用户
     */
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    /**
     * 获取下一个身份变更用户的失败状态
     *
     * 返回一个仅包含当前用户身份变更的失败对象。
     *
     * @return [LocalEventSendState.Failed.VerifiedUserChangedIdentity] 单用户身份变更失败对象
     */
    override fun next(): LocalEventSendState.Failed.VerifiedUser {
        val userId = iterator.next()
        return LocalEventSendState.Failed.VerifiedUserChangedIdentity(
            listOf(userId)
        )
    }
}
