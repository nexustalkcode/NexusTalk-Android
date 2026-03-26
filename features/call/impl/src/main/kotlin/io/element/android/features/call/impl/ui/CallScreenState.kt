/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.libraries.architecture.AsyncData

/**
 * 通话界面状态数据类
 *
 * 表示通话界面的完整状态，包含 WebView 加载状态、错误信息、用户代理、呼叫状态等信息。
 * 此状态由 CallScreenPresenter 生成，供 CallScreenView 渲染 UI 使用。
 *
 * @property urlState Element Call URL 的加载状态，使用 AsyncData 表示加载中/成功/失败
 * @property webViewError WebView 加载错误信息（可选）
 * @property userAgent 用户代理字符串，用于 WebView 加载
 * @property isCallActive 通话是否处于活动状态
 * @property isInWidgetMode 是否处于小组件模式（房间通话为 true，外部 URL 通话为 false）
 * @property eventSink 事件处理函数，用于将用户操作传递给 Presenter
 *
 * @see CallScreenPresenter 生成此状态的 Presenter
 * @see CallScreenView 使用此状态渲染 UI
 * @see CallScreenEvents 通话界面事件
 */
data class CallScreenState(
    /** Element Call URL 的加载状态，使用 AsyncData 表示加载中/成功/失败 */
    val urlState: AsyncData<String>,
    /** WebView 加载错误信息（可选） */
    val webViewError: String?,
    /** 用户代理字符串，用于 WebView 加载 */
    val userAgent: String,
    /** 通话是否处于活动状态 */
    val isCallActive: Boolean,
    /** 是否处于小组件模式（房间通话为 true，外部 URL 通话为 false） */
    val isInWidgetMode: Boolean,
    /** 事件处理函数，用于将用户操作传递给 Presenter */
    val eventSink: (CallScreenEvents) -> Unit,
)
