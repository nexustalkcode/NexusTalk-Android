/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.di

import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.libraries.architecture.Presenter

/**
 * 时间线项Presenter工厂函数接口
 *
 * 用于创建与时间线项关联的Presenter实例的工厂接口。
 * 不同类型的时间线项内容（如文本、语音、文件等）对应不同的Presenter。
 *
 * 实现类应使用@AssistedFactory注解标记，以便依赖注入库创建实例。
 *
 * @param C 时间线项的TimelineItemEventContent子类型
 * @param S Presenter的状态类类型
 * @return 一个Presenter，为给定的C类型内容生成S类型的状态
 *
 * @see TimelineItemEventContent 时间线项事件内容
 * @see Presenter Presenter基类
 */
fun interface TimelineItemPresenterFactory<C : TimelineItemEventContent, S : Any> {
    fun create(content: C): Presenter<S>
}
