/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(FlowPreview::class)

package io.element.android.features.networkmonitor.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.features.networkmonitor.api.NetworkMonitor
import io.element.android.features.networkmonitor.api.NetworkStatus
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * NetworkMonitor 的默认实现
 *
 * 使用 Android ConnectivityManager 实现网络连接状态的监控。
 * 提供设备网络连接状态的实时流，支持状态变化检测和防抖动处理。
 *
 * **注意：** 监控的是网络连接状态，不是互联网连接状态。
 * 设备可能已连接到 WiFi 或移动网络，但无法访问互联网。
 *
 * @property context 应用上下文
 * @property appCoroutineScope 应用级协程作用域
 * @see NetworkMonitor 网络监控器接口
 * @see ConnectivityManager Android 连接管理器
 */
@ContributesBinding(scope = AppScope::class)
@SingleIn(AppScope::class)
class DefaultNetworkMonitor(
    @ApplicationContext context: Context,
    @AppCoroutineScope
    appCoroutineScope: CoroutineScope,
) : NetworkMonitor {
    /** Android 连接管理器 */
    private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)

    /**
     * 网络连接状态流
     *
     * 使用 callbackFlow 实现异步回调到 Flow 的转换。
     * 包含防抖动处理（300ms），避免快速的网络状态变化。
     */
    override val connectivity: StateFlow<NetworkStatus> = callbackFlow {

        /**
         * 由于在回调中同步调用 ConnectivityManager 方法不安全，
         * 我们只维护活动网络的计数。
         * 对结果进行防抖动处理，避免快速的离线<->在线切换。
         */
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val activeNetworksCount = AtomicInteger(0)

            override fun onLost(network: Network) {
                if (activeNetworksCount.decrementAndGet() == 0) {
                    trySendBlocking(NetworkStatus.Disconnected)
                }
            }

            override fun onAvailable(network: Network) {
                if (activeNetworksCount.incrementAndGet() > 0) {
                    trySendBlocking(NetworkStatus.Connected)
                }
            }
        }
        trySendBlocking(connectivityManager.activeNetworkStatus())
        val request = NetworkRequest.Builder().build()

        connectivityManager.registerNetworkCallback(request, callback)
        Timber.d("Subscribe")
        awaitClose {
            Timber.d("Unsubscribe")
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
        .distinctUntilChanged()
        .debounce(300)
        .onEach {
            Timber.d("NetworkStatus changed=$it")
        }
        .stateIn(appCoroutineScope, SharingStarted.WhileSubscribed(), connectivityManager.activeNetworkStatus())

    /**
     * 获取当前网络状态
     *
     * @return 网络连接状态
     */
    private fun ConnectivityManager.activeNetworkStatus(): NetworkStatus {
        return if (activeNetwork != null) NetworkStatus.Connected else NetworkStatus.Disconnected
    }
}
