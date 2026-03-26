/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyPermissions
import io.element.android.features.securityandprivacy.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 安全与隐私状态数据类
 *
 * 表示安全与隐私页面的完整状态，包含保存的设置、编辑的设置、权限信息等。
 *
 * @property savedSettings 当前应用的设置
 * @property editedSettings 用户编辑的设置
 * @property homeserverName 服务器名称
 * @property showEnableEncryptionConfirmation 是否显示启用加密确认对话框
 * @property isKnockEnabled 是否启用敲门功能
 * @property isSpaceSettingsEnabled 是否启用空间设置
 * @property saveAction 保存操作的异步状态
 * @property isSpace 是否为空间
 * @property permissions 安全与隐私权限
 * @property selectableJoinedSpaces 可选的可加入空间集合
 * @property spaceSelectionMode 空间选择模式
 * @property eventSink 事件处理函数
 */
data class SecurityAndPrivacyState(
    // the settings that are currently applied on the room.
    /** 当前已应用到房间的设置 */
    val savedSettings: SecurityAndPrivacySettings,
    // the settings the user wants to apply.
    /** 用户编辑后想要应用的设置 */
    val editedSettings: SecurityAndPrivacySettings,
    /** 服务器名称 */
    val homeserverName: String,
    /** 是否显示启用加密确认对话框 */
    val showEnableEncryptionConfirmation: Boolean,
    /** 是否启用敲门（Knock）功能 */
    private val isKnockEnabled: Boolean,
    /** 是否启用空间设置功能 */
    private val isSpaceSettingsEnabled: Boolean,
    /** 保存操作的异步状态 */
    val saveAction: AsyncAction<Unit>,
    /** 是否为空间（而非普通房间） */
    val isSpace: Boolean,
    /** 安全与隐私权限 */
    private val permissions: SecurityAndPrivacyPermissions,
    /** 可选的可加入空间集合 */
    private val selectableJoinedSpaces: ImmutableSet<SpaceRoom>,
    /** 空间选择模式 */
    private val spaceSelectionMode: SpaceSelectionMode,
    /** 事件处理函数 */
    val eventSink: (SecurityAndPrivacyEvent) -> Unit
) {
    val isSpaceMemberSelectable = isSpaceSettingsEnabled && spaceSelectionMode != SpaceSelectionMode.None

    // Show SpaceMember option in two cases:
    // - SpaceMember is the current saved value
    // - SpaceMember option is selectable (ie. the FF is enabled and there is at least one space to select)
    val showSpaceMemberOption = savedSettings.roomAccess is SecurityAndPrivacyRoomAccess.SpaceMember || isSpaceMemberSelectable

    val showManageSpaceFooter = spaceSelectionMode is SpaceSelectionMode.Multiple &&
        (editedSettings.roomAccess is SecurityAndPrivacyRoomAccess.SpaceMember ||
            editedSettings.roomAccess is SecurityAndPrivacyRoomAccess.AskToJoinWithSpaceMember)

    val isAskToJoinSelectable = isKnockEnabled

    val isAskToJoinWithSpaceMembersSelectable = isAskToJoinSelectable && isSpaceMemberSelectable

    // Show Ask to join option only when:
    // - AskToJoin is the current saved value (legacy), OR
    // - Knock FF enabled BUT (SpaceSettings FF disabled OR no spaces available)
    val showAskToJoinOption = savedSettings.roomAccess == SecurityAndPrivacyRoomAccess.AskToJoin ||
        isAskToJoinSelectable && !isAskToJoinWithSpaceMembersSelectable

    // Show AskToJoinWithSpaceMember option when:
    // - It's the current saved value, OR
    // - Both FFs enabled AND spaces available
    val showAskToJoinWithSpaceMemberOption = savedSettings.roomAccess is SecurityAndPrivacyRoomAccess.AskToJoinWithSpaceMember ||
        isAskToJoinWithSpaceMembersSelectable

    val canBeSaved = savedSettings != editedSettings

    // Logic is in https://github.com/element-hq/element-meta/issues/3029
    val availableHistoryVisibilities = buildList {
        // Shared is always available
        add(SecurityAndPrivacyHistoryVisibility.Shared)
        if (editedSettings.roomAccess == SecurityAndPrivacyRoomAccess.Anyone && !editedSettings.isEncrypted) {
            add(SecurityAndPrivacyHistoryVisibility.WorldReadable)
        } else {
            add(SecurityAndPrivacyHistoryVisibility.Invited)
        }
    }
        .sorted()
        .toImmutableList()

    val showRoomAccessSection = permissions.canChangeRoomAccess

    val showRoomVisibilitySections = permissions.canChangeRoomVisibility &&
        editedSettings.roomAccess.canConfigureRoomVisibility()

    val showHistoryVisibilitySection = permissions.canChangeHistoryVisibility && !isSpace
    val showEncryptionSection = permissions.canChangeEncryption && !isSpace

    @Composable
    fun spaceMemberDescription(): String {
        return if (isSpaceMemberSelectable) {
            when (spaceSelectionMode) {
                is SpaceSelectionMode.Single -> {
                    val spaceName = spaceSelectionMode.spaceRoom?.displayName ?: spaceSelectionMode.spaceId.value
                    stringResource(R.string.screen_security_and_privacy_room_access_space_members_option_single_parent_description, spaceName)
                }
                is SpaceSelectionMode.None,
                is SpaceSelectionMode.Multiple -> stringResource(
                    R.string.screen_security_and_privacy_room_access_space_members_option_multiple_parents_description
                )
            }
        } else {
            stringResource(R.string.screen_security_and_privacy_room_access_space_members_option_unavailable_description)
        }
    }

    @Composable
    fun askToJoinWithSpaceMembersDescription(): String {
        return if (isAskToJoinWithSpaceMembersSelectable) {
            when (spaceSelectionMode) {
                is SpaceSelectionMode.Single -> {
                    val spaceName = spaceSelectionMode.spaceRoom?.displayName ?: spaceSelectionMode.spaceId.value
                    stringResource(R.string.screen_security_and_privacy_ask_to_join_single_space_members_option_description, spaceName)
                }
                is SpaceSelectionMode.None,
                is SpaceSelectionMode.Multiple -> stringResource(R.string.screen_security_and_privacy_ask_to_join_multiple_spaces_members_option_description)
            }
        } else {
            stringResource(R.string.screen_security_and_privacy_ask_to_join_option_description)
        }
    }
}

/**
 * 安全与隐私设置数据类
 *
 * 存储安全与隐私相关的所有设置项。
 *
 * @property roomAccess 房间访问权限设置
 * @property isEncrypted 是否加密
 * @property historyVisibility 历史可见性设置
 * @property address 房间地址
 * @property isVisibleInRoomDirectory 是否在房间目录中可见
 */
data class SecurityAndPrivacySettings(
    /** 房间访问权限设置 */
    val roomAccess: SecurityAndPrivacyRoomAccess,
    /** 是否加密 */
    val isEncrypted: Boolean,
    /** 历史可见性设置 */
    val historyVisibility: SecurityAndPrivacyHistoryVisibility,
    /** 房间地址 */
    val address: String?,
    /** 是否在房间目录中可见 */
    val isVisibleInRoomDirectory: AsyncData<Boolean>
)

/**
 * 历史可见性枚举
 *
 * 定义房间消息历史的可见性级别，按限制程度从高到低排序。
 */
enum class SecurityAndPrivacyHistoryVisibility {
    // Order matters, and is from the most to the least restrictive

    /** 自邀请起可见（限制最严格） */
    Invited,
    /** 选择后可见（默认） */
    Shared,
    /** 所有人可见（限制最宽松） */
    WorldReadable;

    /**
     * 获取当当前可见性选项不可用时的备选可见性
     *
     * @return 备选的可见性选项
     */
    fun fallback(): SecurityAndPrivacyHistoryVisibility {
        return when (this) {
            Invited,
            Shared -> Shared
            WorldReadable -> Invited
        }
    }
}

/**
 * 空间选择模式密封接口
 *
 * 定义用户可以选择授权空间的方式。
 */
sealed interface SpaceSelectionMode {
    /** 无可选空间 */
    data object None : SpaceSelectionMode

    /**
     * 仅有一个可选空间
     * @property spaceId 空间 ID
     * @property spaceRoom 空间房间信息（可能为 null）
     */
    data class Single(val spaceId: RoomId, val spaceRoom: SpaceRoom?) : SpaceSelectionMode

    /** 多个可选空间，需要进入多选页面 */
    data object Multiple : SpaceSelectionMode
}

/**
 * 房间访问权限密封接口
 *
 * 定义房间的各种访问权限级别。
 */
sealed interface SecurityAndPrivacyRoomAccess {
    /** 仅限邀请（私有房间） */
    data object InviteOnly : SecurityAndPrivacyRoomAccess

    /** 需要申请才能加入 */
    data object AskToJoin : SecurityAndPrivacyRoomAccess

    /** 任何人都可以加入 */
    data object Anyone : SecurityAndPrivacyRoomAccess

    /**
     * 仅限空间成员加入
     * @property spaceIds 允许访问的空间 ID 列表
     */
    data class SpaceMember(val spaceIds: ImmutableList<RoomId>) : SecurityAndPrivacyRoomAccess

    /**
     * 通过空间成员邀请才能加入（需要申请）
     * @property spaceIds 允许访问的空间 ID 列表
     */
    data class AskToJoinWithSpaceMember(val spaceIds: ImmutableList<RoomId>) : SecurityAndPrivacyRoomAccess

    /**
     * 检查是否可配置房间可见性
     *
     * @return Boolean 是否可以配置房间可见性
     */
    fun canConfigureRoomVisibility(): Boolean {
        return when (this) {
            InviteOnly, is SpaceMember -> false
            AskToJoin, Anyone, is AskToJoinWithSpaceMember -> true
        }
    }

    /**
     * 获取关联的空间 ID 列表
     *
     * @return 空间 ID 列表（如果没有关联空间则返回空列表）
     */
    fun spaceIds(): ImmutableList<RoomId> {
        return when (this) {
            is SpaceMember -> spaceIds
            is AskToJoinWithSpaceMember -> spaceIds
            else -> persistentListOf()
        }
    }
}

/**
 * 安全与隐私操作失败异常密封类
 *
 * 定义安全与隐私功能中可能出现的错误。
 */
sealed class SecurityAndPrivacyFailures : Exception() {
    /** 保存设置失败 */
    data object SaveFailed : SecurityAndPrivacyFailures()
}
