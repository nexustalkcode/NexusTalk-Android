/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import android.net.Uri
import dev.zacsweers.metro.Inject
import io.element.android.libraries.androidutils.file.safeDelete
import io.element.android.libraries.matrix.api.room.alias.RoomAliasHelper
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import java.io.File

/**
 * 创建房间配置存储类
 *
 * 负责管理和持久化创建房间流程中的用户配置信息。
 * 使用 StateFlow 管理配置状态，支持配置的实时更新和观察。
 *
 * @property roomAliasHelper 房间别名辅助工具，用于根据房间名称生成房间别名
 */
@Inject
class CreateRoomConfigStore(
    private val roomAliasHelper: RoomAliasHelper,
) {
    /** 创建房间配置的 Flow，用于观察配置变化 */
    private val createRoomConfigFlow: MutableStateFlow<CreateRoomConfig> = MutableStateFlow(CreateRoomConfig())

    /**
     * 缓存的头像 URI
     *
     * 设置新值时自动删除旧的头像文件
     */
    private var cachedAvatarUri: Uri? = null
        set(value) {
            field?.path?.let { File(it) }?.safeDelete()
            field = value
        }

    /**
     * 获取创建房间配置的 Flow
     *
     * @return 配置的 StateFlow，供观察者使用
     */
    fun getCreateRoomConfigFlow(): StateFlow<CreateRoomConfig> = createRoomConfigFlow

    /**
     * 设置房间名称
     *
     * 更新配置中的房间名称。如果房间可见性为公开且地址是自动生成的，
     * 会根据新名称自动更新房间地址。
     *
     * @param roomName 新的房间名称
     */
    fun setRoomName(roomName: String) {
        createRoomConfigFlow.getAndUpdate { config ->
            val roomAccessWithNewAddress = if (config.visibilityState is RoomVisibilityState.Public) {
                val roomAddress = config.visibilityState.roomAddress
                if (roomAddress is RoomAddress.AutoFilled || roomName.isEmpty()) {
                    val roomAliasName = roomAliasHelper.roomAliasNameFromRoomDisplayName(roomName)
                    config.visibilityState.copy(roomAddress = RoomAddress.AutoFilled(roomAliasName))
                } else {
                    config.visibilityState
                }
            } else {
                config.visibilityState
            }
            config.copy(
                roomName = roomName.takeIf { it.isNotEmpty() },
                visibilityState = roomAccessWithNewAddress,
            )
        }
    }

    /**
     * 设置房间主题
     *
     * 更新配置中的房间主题/描述。如果主题为空字符串，则不保存。
     *
     * @param topic 新的房间主题
     */
    fun setTopic(topic: String) {
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(topic = topic.takeIf { it.isNotEmpty() })
        }
    }

    /**
     * 设置房间头像 URI
     *
     * 更新配置中的房间头像。如果设置了 cached 为 true，
     * 新的头像 URI 会存储在缓存中，旧的头像文件会被删除。
     *
     * @param uri 头像的 URI，为 null 表示移除头像
     * @param cached 是否缓存该 URI，缓存的头像会在设置新头像时自动删除旧文件
     */
    fun setAvatarUri(uri: Uri?, cached: Boolean = false) {
        cachedAvatarUri = uri.takeIf { cached }
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(avatarUri = uri?.toString())
        }
    }

    /**
     * 设置加入规则
     *
     * 根据提供的加入规则设置房间的可见性和访问权限。
     * 如果设置为私有房间，可见性状态将变为私有；
     * 如果设置为公开可见性，会自动生成房间地址。
     *
     * @param joinRule 加入规则项，决定房间的访问权限
     */
    fun setJoinRule(joinRule: JoinRuleItem) {
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(
                visibilityState = when (joinRule) {
                    JoinRuleItem.Private -> RoomVisibilityState.Private()
                    is JoinRuleItem.PublicVisibility -> {
                        val roomAliasName = roomAliasHelper.roomAliasNameFromRoomDisplayName(config.roomName.orEmpty())
                        RoomVisibilityState.Public(
                            roomAddress = RoomAddress.AutoFilled(roomAliasName),
                            joinRuleItem = joinRule,
                        )
                    }
                }
            )
        }
    }

    /**
     * 设置房间地址
     *
     * 更新配置中的房间地址。仅在房间可见性为公开时生效。
     * 地址会被转换为小写并进行必要的清理。
     *
     * @param address 新的房间地址
     */
    fun setRoomAddress(address: String) {
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(
                visibilityState = when (config.visibilityState) {
                    is RoomVisibilityState.Public -> {
                        val sanitizedAddress = address.lowercase()
                        config.visibilityState.copy(roomAddress = RoomAddress.Edited(sanitizedAddress))
                    }
                    else -> config.visibilityState
                }
            )
        }
    }

    /**
     * 设置是否为空间
     *
     * 更新配置，标识当前创建的是普通房间还是空间（Space）。
     *
     * @param isSpace 是否创建为空间，true 表示创建空间，false 表示创建普通房间
     */
    fun setIsSpace(isSpace: Boolean) {
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(isSpace = isSpace)
        }
    }

    /**
     * 设置父空间
     *
     * 设置房间所属的父空间。设置父空间后，房间可见性会自动设置为私有。
     *
     * @param parentSpace 父空间房间，null 表示不添加到任何空间
     */
    fun setParentSpace(parentSpace: SpaceRoom?) {
        createRoomConfigFlow.getAndUpdate { config ->
            config.copy(
                parentSpace = parentSpace,
                visibilityState = RoomVisibilityState.Private(),
            )
        }
    }

    /**
     * 清除缓存数据
     *
     * 清除所有缓存的数据，如头像文件等。
     * 通常在房间创建成功后调用以清理临时文件。
     */
    fun clearCachedData() {
        cachedAvatarUri = null
    }
}
