/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.CreatedRoom
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.createroom.CreateRoomParameters
import io.element.android.libraries.matrix.api.createroom.RoomPreset
import io.element.android.libraries.matrix.api.room.alias.RoomAliasHelper
import io.element.android.libraries.matrix.api.room.history.RoomHistoryVisibility
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.roomdirectory.RoomVisibility
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidity
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidityEffect
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaPreProcessor
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

/**
 * 配置房间 Presenter
 *
 * 负责处理配置房间界面的业务逻辑，包括：
 * - 管理房间配置状态（名称、主题、头像等）
 * - 处理用户输入事件
 * - 执行创建房间操作
 * - 上传房间头像
 * - 管理相机权限
 *
 * @property isSpace 是否创建为空间
 * @property dataStore 配置存储，用于持久化配置
 * @property matrixClient Matrix 客户端，用于与服务器通信
 * @property mediaPickerProvider 媒体选择器提供者
 * @property mediaPreProcessor 媒体预处理器，用于处理头像图片
 * @property analyticsService 分析服务
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property featureFlagService 特性开关服务
 * @property roomAliasHelper 房间别名辅助工具
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 */
@AssistedInject
class ConfigureRoomPresenter(
    @Assisted private val isSpace: Boolean,
    private val dataStore: CreateRoomConfigStore,
    private val matrixClient: MatrixClient,
    private val mediaPickerProvider: PickerProvider,
    private val mediaPreProcessor: MediaPreProcessor,
    private val analyticsService: AnalyticsService,
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    private val featureFlagService: FeatureFlagService,
    private val roomAliasHelper: RoomAliasHelper,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
) : Presenter<ConfigureRoomState> {
    /**
     * Presenter 工厂接口
     *
     * 用于创建 ConfigureRoomPresenter 实例
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 ConfigureRoomPresenter 实例
         *
         * @param isSpace 是否创建为空间
         * @return ConfigureRoomPresenter 实例
         */
        fun create(isSpace: Boolean): ConfigureRoomPresenter
    }

    /** 相机权限 Presenter，用于管理相机权限请求 */
    private val cameraPermissionPresenter: PermissionsPresenter = permissionsPresenterFactory.create(android.Manifest.permission.CAMERA)
    /** 是否有待处理的权限请求 */
    private var pendingPermissionRequest = false

    init {
        dataStore.setIsSpace(isSpace)
    }

    @Composable
    override fun present(): ConfigureRoomState {
        val canAddRoomToSpace by featureFlagService.isFeatureEnabledFlow(FeatureFlags.CreateSpaces).collectAsState(false)
        val cameraPermissionState = cameraPermissionPresenter.present()
        val createRoomConfig by dataStore.getCreateRoomConfigFlow().collectAsState()
        val homeserverName = remember { matrixClient.userIdServerName() }
        val isKnockFeatureEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.Knock)
        }.collectAsState(initial = false)
        val roomAddressValidity = remember {
            mutableStateOf<RoomAddressValidity>(RoomAddressValidity.Unknown)
        }

        val cameraPhotoPicker = mediaPickerProvider.registerCameraPhotoPicker(
            onResult = { uri -> if (uri != null) dataStore.setAvatarUri(uri = uri, cached = true) },
        )
        val galleryImagePicker = mediaPickerProvider.registerGalleryImagePicker(
            onResult = { uri -> if (uri != null) dataStore.setAvatarUri(uri = uri) }
        )

        val avatarActions by remember(createRoomConfig.avatarUri) {
            derivedStateOf {
                listOfNotNull(
                    AvatarAction.TakePhoto,
                    AvatarAction.ChoosePhoto,
                    AvatarAction.Remove.takeIf { createRoomConfig.avatarUri != null },
                ).toImmutableList()
            }
        }

        var spaces by remember { mutableStateOf<ImmutableList<SpaceRoom>>(persistentListOf()) }
        LaunchedEffect(canAddRoomToSpace) {
            spaces = if (canAddRoomToSpace) {
                matrixClient.spaceService.editableSpaces().getOrElse { emptyList() }.toImmutableList()
            } else {
                persistentListOf()
            }
        }

        LaunchedEffect(cameraPermissionState.permissionGranted) {
            if (cameraPermissionState.permissionGranted && pendingPermissionRequest) {
                pendingPermissionRequest = false
                cameraPhotoPicker.launch()
            }
        }

        RoomAddressValidityEffect(
            client = matrixClient,
            roomAliasHelper = roomAliasHelper,
            newRoomAddress = createRoomConfig.visibilityState.roomAddress().getOrDefault(""),
            knownRoomAddress = null,
        ) { newRoomAddressValidity ->
            roomAddressValidity.value = newRoomAddressValidity
        }

        val localCoroutineScope = rememberCoroutineScope()
        val createRoomAction: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        // Calculate available join rules based:
        // 1. If we are creating a space.
        // 2. If it has a parent space.
        // 3. If knocking is enabled.
        val parentSpace = createRoomConfig.parentSpace
        val availableJoinRules = remember(createRoomConfig.parentSpace, isSpace, isKnockFeatureEnabled) {
            when {
                isSpace && parentSpace != null -> TODO("Adding a space to a parent space is not supported yet! How did you get here?")
                parentSpace == null || parentSpace.joinRule == JoinRule.Public -> listOfNotNull(
                    JoinRuleItem.PublicVisibility.Public,
                    JoinRuleItem.PublicVisibility.AskToJoin.takeIf { !isSpace && isKnockFeatureEnabled },
                    JoinRuleItem.Private,
                ).toImmutableList()
                else -> listOfNotNull(
                    JoinRuleItem.PublicVisibility.Restricted(parentSpace.roomId),
                    JoinRuleItem.PublicVisibility.AskToJoinRestricted(parentSpace.roomId).takeIf { !isSpace && isKnockFeatureEnabled },
                    JoinRuleItem.Private,
                ).toImmutableList()
            }
        }

        /**
         * 创建房间
         *
         * 根据配置创建新房间或新空间
         *
         * @param config 房间配置信息
         */
        fun createRoom(config: CreateRoomConfig) {
            createRoomAction.value = AsyncAction.Uninitialized
            localCoroutineScope.createRoom(config, createRoomAction)
        }

        /**
         * 处理事件
         *
         * 根据事件类型调用相应的处理方法
         *
         * @param event 配置房间事件
         */
        fun handleEvent(event: ConfigureRoomEvents) {
            when (event) {
                is ConfigureRoomEvents.RoomNameChanged -> dataStore.setRoomName(event.name)
                is ConfigureRoomEvents.TopicChanged -> dataStore.setTopic(event.topic)
                is ConfigureRoomEvents.JoinRuleChanged -> dataStore.setJoinRule(event.joinRuleItem)
                is ConfigureRoomEvents.RoomAddressChanged -> dataStore.setRoomAddress(event.roomAddress)
                is ConfigureRoomEvents.CreateRoom -> createRoom(createRoomConfig)
                is ConfigureRoomEvents.HandleAvatarAction -> {
                    when (event.action) {
                        AvatarAction.ChoosePhoto -> galleryImagePicker.launch()
                        AvatarAction.TakePhoto -> if (cameraPermissionState.permissionGranted) {
                            cameraPhotoPicker.launch()
                        } else {
                            pendingPermissionRequest = true
                            cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                        }
                        AvatarAction.Remove -> dataStore.setAvatarUri(uri = null)
                    }
                }
                is ConfigureRoomEvents.SetParentSpace -> {
                    dataStore.setParentSpace(event.space)
                }
                ConfigureRoomEvents.CancelCreateRoom -> {
                    createRoomAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return ConfigureRoomState(
            config = createRoomConfig,
            avatarActions = avatarActions,
            createRoomAction = createRoomAction.value,
            cameraPermissionState = cameraPermissionState,
            homeserverName = homeserverName,
            roomAddressValidity = roomAddressValidity.value,
            availableJoinRules = availableJoinRules,
            spaces = spaces,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 协程作用域内创建房间
     *
     * 实际的房间创建逻辑，包括：
     * - 上传头像
     * - 构建创建房间参数
     * - 调用 Matrix API 创建房间
     * - 如果设置了父空间，将房间添加到父空间
     *
     * @param config 房间配置
     * @param createRoomAction 创建房间的异步操作状态
     */
    private fun CoroutineScope.createRoom(
        config: CreateRoomConfig,
        createRoomAction: MutableState<AsyncAction<RoomId>>
    ) = launch {
        suspend {
            val avatarUrl = config.avatarUri?.let { uploadAvatar(it.toUri()) }
            val params = if (config.visibilityState is RoomVisibilityState.Public) {
                CreateRoomParameters(
                    name = config.roomName,
                    topic = config.topic,
                    isEncrypted = false,
                    isDirect = false,
                    visibility = RoomVisibility.Public,
                    joinRuleOverride = config.visibilityState.joinRuleItem.toJoinRule()
                        // No need to specify the public join rule override, since the preset is already PUBLIC_CHAT
                        .takeIf { it != JoinRule.Public },
                    preset = RoomPreset.PUBLIC_CHAT,
                    invite = config.invites.map { it.userId },
                    avatar = avatarUrl,
                    roomAliasName = config.visibilityState.roomAddress(),
                    isSpace = isSpace,
                )
            } else {
                CreateRoomParameters(
                    name = config.roomName,
                    topic = config.topic,
                    isEncrypted = config.visibilityState is RoomVisibilityState.Private,
                    isDirect = false,
                    visibility = RoomVisibility.Private,
                    historyVisibilityOverride = RoomHistoryVisibility.Invited,
                    preset = RoomPreset.PRIVATE_CHAT,
                    invite = config.invites.map { it.userId },
                    avatar = avatarUrl,
                    isSpace = isSpace,
                )
            }
            val roomId = matrixClient.createRoom(params)
                .onFailure { failure ->
                    Timber.e(failure, "Failed to create room")
                }
                .onSuccess {
                    dataStore.clearCachedData()
                    analyticsService.capture(CreatedRoom(isDM = false))
                }
                .getOrThrow()

            // Add the newly created room to the parent space too
            if (config.parentSpace != null) {
                Timber.d("Adding room $roomId to parent space ${config.parentSpace.roomId}")
                // Wait until we receive the power level info for the room, as it's needed to check if it can be added to a space
                // TODO create some SDK function that does this instead?
                withTimeoutOrNull(30.seconds) {
                    matrixClient.getRoomInfoFlow(roomId).first { it.getOrNull()?.roomPowerLevels != null }
                } ?: error("Did not receive created room power levels for room $roomId, needed for adding it to a space")

                matrixClient.spaceService.addChildToSpace(spaceId = config.parentSpace.roomId, childId = roomId).getOrThrow()
            }

            roomId
        }.runCatchingUpdatingState(createRoomAction)
            .onFailure { Timber.e(it, "Could not create room or add it to parent space ${config.parentSpace?.roomId}") }
    }

    /**
     * 上传头像
     *
     * 处理并上传房间头像到 Matrix 服务器
     *
     * @param avatarUri 头像的 URI
     * @return 上传后的头像 URL
     */
    private suspend fun uploadAvatar(avatarUri: Uri): String {
        val preprocessed = mediaPreProcessor.process(
            uri = avatarUri,
            mimeType = MimeTypes.Jpeg,
            deleteOriginal = false,
            mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
        ).getOrThrow()
        val byteArray = preprocessed.file.readBytes()
        return matrixClient.uploadMedia(MimeTypes.Jpeg, byteArray).getOrThrow()
    }
}
