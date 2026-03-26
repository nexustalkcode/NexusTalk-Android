/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.analytics

import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.services.analytics.api.AnalyticsSdkSpan
import kotlinx.coroutines.DelicateCoroutinesApi
import org.matrix.rustcomponents.sdk.LogLevel
import org.matrix.rustcomponents.sdk.Span
import timber.log.Timber

/**
 * Rust SDK 分析 SDK Span 的实现类
 *
 * 该类是 [AnalyticsSdkSpan] 接口的 Rust 实现，用于在性能追踪系统中表示一个操作的执行区间。
 * 它底层使用 Rust 的 Sentry SDK (通过 `org.matrix.rustcomponents.sdk.Span`) 来实现分布式追踪。
 *
 * Span 是 Sentry 追踪系统中的核心概念，用于记录一个操作从开始到结束的时间跨度。
 * 通过嵌套的 Span，可以构建出完整的调用链，帮助开发者分析性能瓶颈和错误来源。
 *
 * @property name Span 的名称，用于标识这个追踪区间执行的操作类型。
 *                 如果为 null，则表示这是一个"桥接 Span"，用于连接父追踪和子追踪。
 * @property parentTraceId 父追踪的唯一标识符，用于将当前 Span 关联到父追踪链中。
 *                        如果为 null，则表示这是一个根级别的追踪。
 *
 * @see AnalyticsSdkSpan 分析 SDK Span 的接口定义
 * @see <a href="https://docs.sentry.io/product/sentry-basics/tracing/">Sentry Tracing 文档</a>
 */
class RustAnalyticsSdkSpan(
    name: String? = null,
    private val parentTraceId: String?,
) : AnalyticsSdkSpan {
    // 内部的 Rust Span 对象，用于执行实际的追踪操作
    // 根据是否有名称，创建不同类型的 Span：
    // - 有名称：创建一个完整的追踪区间
    // - 无名称：创建一个桥接 Span，用于连接不同的追踪链
    private val inner = if (name != null) {
        Span(
            target = "elementx",   // 标识这是 Element X 应用的追踪数据
            name = name,           // Span 名称
            file = "-",           // 文件位置（不可用）
            line = null,          // 行号（不可用）
            level = LogLevel.WARN, // 日志级别
            bridgeTraceId = parentTraceId, // 关联的父追踪 ID
        )
    } else {
        // 桥接 Span：没有名称，用于连接父追踪和子追踪
        Span.newBridgeSpan(
            target = "elementx",
            parentTraceId = parentTraceId,
        )
    }

    /**
     * 进入 Span 区间，开始追踪
     *
     * 调用此方法后，开始记录当前操作的执行时间。
     * 如果当前已经存在一个活动的 Span，则不会进入新的 Span，以避免 Span 嵌套混乱。
     * 这是因为 Sentry 追踪通常要求严格的父子层级关系。
     */
    override fun enter() {
        // 检查是否已经有活动的 Span
        if (Span.current().isNone()) {
            // 没有活动的 Span，可以安全进入
            inner.enter()
        } else {
            // 已有活动的 Span，跳过进入操作并记录警告日志
            Timber.w("Not entering span sentry.trace='$parentTraceId' because another span is already active")
        }
    }

    /**
     * 退出 Span 区间，结束追踪
     *
     * 调用此方法后，结束当前 Span 的追踪记录，并记录执行时间。
     * 无论追踪是否成功，都会调用 destroy() 释放资源。
     *
     * 注意：使用了 @OptIn(DelicateCoroutinesApi) 注解，
     * 因为 destroy() 可能在非协程上下文中被调用。
     */
    @OptIn(DelicateCoroutinesApi::class)
    override fun exit() {
        inner.exit()
        // 尝试销毁 Span，捕获可能的异常以避免影响主流程
        runCatchingExceptions { inner.destroy() }
        Timber.d("Exited span sentry.trace='$parentTraceId'")
    }
}
