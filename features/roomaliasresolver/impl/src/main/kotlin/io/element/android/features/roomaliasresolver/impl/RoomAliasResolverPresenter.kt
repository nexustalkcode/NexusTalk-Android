/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrElse

/**
 * 房间别名解析器 Presenter
 *
 * 负责房间别名解析功能的业务逻辑处理。
 * 遵循 MVP 架构模式中的 Presenter 角色，处理用户交互并更新状态。
 *
 * 使用 @AssistedInject 注解实现依赖注入，
 * 接收房间别名和 MatrixClient 来执行解析操作。
 *
 * @see RoomAliasResolverState 界面状态数据类
 * @see RoomAliasResolverEvents 用户事件定义
 * @see RoomAliasResolverView 界面渲染
 */
@AssistedInject
class RoomAliasResolverPresenter(
    /** 要解析的房间别名 */
    @Assisted private val roomAlias: RoomAlias,
    /** Matrix 客户端，用于执行实际的解析请求 */
    private val matrixClient: MatrixClient,
) : Presenter<RoomAliasResolverState> {
    /**
     * Presenter 工厂接口
     *
     * 用于创建 RoomAliasResolverPresenter 实例的工厂。
     * 工厂模式便于依赖注入和测试。
     */
    fun interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param roomAlias 要解析的房间别名
         * @return 新的 Presenter 实例
         */
        fun create(
            roomAlias: RoomAlias,
        ): RoomAliasResolverPresenter
    }

    /**
     * 生成界面状态
     *
     * @return 包含当前解析状态的 RoomAliasResolverState
     */
    @Composable
    override fun present(): RoomAliasResolverState {
        // 协程作用域，用于执行异步操作
        val coroutineScope = rememberCoroutineScope()
        // 解析状态，使用 MutableState 管理 Compose 响应式更新
        val resolveState: MutableState<AsyncData<ResolvedRoomAlias>> = remember { mutableStateOf(AsyncData.Uninitialized) }

        // 组件加载时自动触发解析
        LaunchedEffect(Unit) {
            resolveAlias(resolveState)
        }

        /**
         * 处理用户事件
         *
         * @param event 用户交互事件
         */
        fun handleEvent(event: RoomAliasResolverEvents) {
            when (event) {
                // 重试解析
                RoomAliasResolverEvents.Retry -> coroutineScope.resolveAlias(resolveState)
                // 关闭错误提示
                RoomAliasResolverEvents.DismissError -> resolveState.value = AsyncData.Uninitialized
            }
        }

        return RoomAliasResolverState(
            roomAlias = roomAlias,
            resolveState = resolveState.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 执行房间别名解析
     *
     * 使用协程在后台执行解析操作，更新解析状态。
     *
     * @param resolveState 解析状态容器，用于更新 UI
     */
    private fun CoroutineScope.resolveAlias(resolveState: MutableState<AsyncData<ResolvedRoomAlias>>) = launch {
        suspend {
            // 调用 MatrixClient 解析房间别名
            matrixClient.resolveRoomAlias(roomAlias)
                .getOrThrow()
                .getOrElse { throw RoomAliasResolverFailures.UnknownAlias }
        }.runCatchingUpdatingState(resolveState)
    }
}
