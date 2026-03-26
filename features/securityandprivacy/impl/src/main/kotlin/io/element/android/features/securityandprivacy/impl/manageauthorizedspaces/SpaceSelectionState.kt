/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.manageauthorizedspaces

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 空间选择状态数据类
 *
 * 用于存储用户在选择授权空间时的状态信息。
 * 此状态在页面间共享，以便在多个地方维护选择结果。
 *
 * @property selectableSpaces 可选的空间集合
 * @property unknownSpaceIds 未知空间 ID 列表（无法获取详细信息的空间）
 * @property selectedSpaceIds 已选中的空间 ID 列表
 * @property completion 完成状态（初始/已完成/已取消）
 */
data class SpaceSelectionState(
    /** 可选的空间集合 */
    val selectableSpaces: ImmutableSet<SpaceRoom>,
    /** 未知空间 ID 列表 */
    val unknownSpaceIds: ImmutableList<RoomId>,
    /** 已选中的空间 ID 列表 */
    val selectedSpaceIds: ImmutableList<RoomId>,
    /** 完成状态 */
    val completion: Completion,
) {
    /**
     * 完成状态枚举
     *
     * 表示用户完成选择后的状态。
     */
    enum class Completion {
        /** 初始状态，用户尚未做出选择 */
        Initial,
        /** 用户已完成选择 */
        Completed,
        /** 用户取消了选择 */
        Cancelled,
    }

    companion object {
        /**
         * 初始状态
         *
         * 所有字段均为空，完成状态为 Initial。
         */
        val INITIAL = SpaceSelectionState(
            selectableSpaces = persistentSetOf(),
            unknownSpaceIds = persistentListOf(),
            selectedSpaceIds = persistentListOf(),
            completion = Completion.Initial,
        )
    }
}

/**
 * 空间选择状态持有者
 *
 * 用于在应用范围内（RoomScope）共享和更新空间选择状态。
 * 这是一个单例类，确保多个页面可以访问和修改相同的状态。
 *
 * @see SpaceSelectionState 空间选择状态数据类
 */
@Inject
@SingleIn(RoomScope::class)
class SpaceSelectionStateHolder {
    /** 内部状态流 */
    private val _state = MutableStateFlow(SpaceSelectionState.INITIAL)

    /**
     * 状态流
     *
     * 外部可观察的状态对象。
     */
    val state: StateFlow<SpaceSelectionState> = _state.asStateFlow()

    /**
     * 更新状态
     *
     * @param transform 状态转换函数
     */
    fun update(transform: (SpaceSelectionState) -> SpaceSelectionState) {
        _state.update(transform)
    }

    /**
     * 更新选中的空间 ID 列表
     *
     * @param selectedSpaceIds 新的选中空间 ID 列表
     */
    fun updateSelectedSpaceIds(selectedSpaceIds: ImmutableList<RoomId>) {
        update { it.copy(selectedSpaceIds = selectedSpaceIds) }
    }

    /**
     * 设置完成状态
     *
     * @param completion 新的完成状态
     */
    fun setCompletion(completion: SpaceSelectionState.Completion) {
        update { it.copy(completion = completion) }
    }
}
