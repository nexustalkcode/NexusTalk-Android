/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.analytics

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.services.analytics.api.AnalyticsSdkManager
import io.element.android.services.analytics.api.AnalyticsSdkSpan
import org.matrix.rustcomponents.sdk.enableSentryLogging

/**
 * Rust SDK 分析服务管理器实现类
 *
 * 该类是 [AnalyticsSdkManager] 接口的 Rust 实现，负责管理与 Rust Matrix SDK
 * 集成的分析（遥测）功能。主要职责包括：
 *
 * 1. **启用/禁用 Sentry 日志**：Sentry 是一个错误追踪和性能监控平台，
 *    该类提供了开启和关闭 Sentry 日志记录的功能。
 *
 * 2. **创建追踪 Span**：用于性能分析，追踪代码执行的时间和调用链。
 *
 * 3. **桥接追踪**：支持在不同的追踪上下文之间建立连接。
 *
 * 使用 @ContributesBinding 注解将此类绑定到 AppScope，使得整个应用可以使用
 * [AnalyticsSdkManager] 接口来访问分析功能。
 *
 * @see AnalyticsSdkManager 分析服务管理器接口
 * @see RustAnalyticsSdkSpan Rust SDK 追踪 Span 实现
 * @see <a href="https://sentry.io/">Sentry 错误追踪平台</a>
 */
@ContributesBinding(AppScope::class)
class RustAnalyticsSdkManager : AnalyticsSdkManager {

    /**
     * 启用或禁用 SDK 级别的分析功能
     *
     * 该方法控制 Rust Matrix SDK 内部的 Sentry 日志记录功能。
     * 当 enabled 为 true 时，SDK 会收集并上报错误和性能数据到 Sentry；
     * 当为 false 时，禁用所有分析数据的收集和上报。
     *
     * @param enabled 是否启用分析功能
     *                - true: 启用 Sentry 日志记录
     *                - false: 禁用 Sentry 日志记录
     */
    override fun enableSdkAnalytics(enabled: Boolean) {
        enableSentryLogging(enabled)
    }

    /**
     * 启动一个新的追踪 Span
     *
     * Span 是 Sentry 性能追踪的基本单元，代表一个操作或代码块的执行区间。
     * 通过创建嵌套的 Span，可以构建出完整的调用链，分析每个步骤的执行时间。
     *
     * @param name Span 的名称，应清晰描述被追踪的操作，例如："HTTP GET /messages"
     * @param parentTraceId 父追踪的唯一标识符，用于构建追踪链。
     *                       如果提供，会将新 Span 作为父追踪的子 Span；
     *                       如果为 null，则创建根级别的 Span。
     * @return 一个新的 [RustAnalyticsSdkSpan] 实例，用于追踪操作
     *
     * @see AnalyticsSdkSpan 追踪 Span 接口
     * @see #bridge(String) 创建桥接 Span
     */
    override fun startSpan(name: String, parentTraceId: String?): AnalyticsSdkSpan {
        return RustAnalyticsSdkSpan(name = name, parentTraceId = parentTraceId)
    }

    /**
     * 创建一个桥接 Span
     *
     * 桥接 Span 是一种特殊的 Span，它没有名称，用于在不同追踪上下文之间建立连接。
     * 典型使用场景是在跨进程或跨线程调用时，将子追踪与父追踪关联起来。
     *
     * 例如：当应用发送一个网络请求到服务器，服务器返回追踪信息时，
     * 可以使用桥接 Span 将服务器返回的追踪 ID 与本地的追踪链关联。
     *
     * @param parentTraceId 父追踪的唯一标识符，用于将桥接 Span 关联到父追踪链
     * @return 一个没有名称的 [RustAnalyticsSdkSpan] 实例，作为桥接 Span
     *
     * @see #startSpan(String, String) 创建普通 Span
     */
    override fun bridge(parentTraceId: String?): AnalyticsSdkSpan {
        // 桥接 Span 没有名称
        return RustAnalyticsSdkSpan(name = null, parentTraceId = parentTraceId)
    }
}
