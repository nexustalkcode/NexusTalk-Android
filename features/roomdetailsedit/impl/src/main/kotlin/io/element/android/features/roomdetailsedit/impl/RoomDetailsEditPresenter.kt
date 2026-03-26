/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import android.Manifest
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import dev.zacsweers.metro.Inject
import io.element.android.features.roomdetailsedit.api.RoomDetailsEditPermissions
import io.element.android.features.roomdetailsedit.api.roomDetailsEditPermissions
import io.element.android.libraries.androidutils.file.TemporaryUriDeleter
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.powerlevels.permissionsAsState
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaPreProcessor
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 房间详情编辑页面的Presenter
 *
 * 负责处理房间详情编辑页面的业务逻辑，包括：
 * - 加载和同步房间信息（名称、主题、头像）
 * - 处理用户编辑操作
 * - 管理相机权限
 * - 保存更改到服务器
 *
 * @property room 已加入的房间，用于获取和更新房间信息
 * @property mediaPickerProvider 媒体选择器提供者，用于选择图片或拍照
 * @property mediaPreProcessor 媒体预处理器，用于处理上传的图片
 * @property temporaryUriDeleter 临时URI删除器，用于清理临时文件
 * @property permissionsPresenterFactory 权限Presenter工厂，用于创建相机权限Presenter
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 */
@Inject
class RoomDetailsEditPresenter(
    /** 已加入的房间，用于获取和更新房间信息 */
    private val room: JoinedRoom,
    /** 媒体选择器提供者，用于选择图片或拍照 */
    private val mediaPickerProvider: PickerProvider,
    /** 媒体预处理器，用于处理上传的图片 */
    private val mediaPreProcessor: MediaPreProcessor,
    /** 临时URI删除器，用于清理临时文件 */
    private val temporaryUriDeleter: TemporaryUriDeleter,
    /** 权限Presenter工厂，用于创建相机权限Presenter */
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    /** 媒体优化配置提供者 */
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
) : Presenter<RoomDetailsEditState> {
    /** 相机权限Presenter，用于管理相机权限请求 */
    private val cameraPermissionPresenter = permissionsPresenterFactory.create(Manifest.permission.CAMERA)
    /** 待处理的权限请求标志，防止重复请求 */
    private var pendingPermissionRequest = false

    /**
     * 生成房间详情编辑页面的状态
     *
     * @return 包含当前编辑状态的 [RoomDetailsEditState] 对象
     */
    @Composable
    override fun present(): RoomDetailsEditState {
        val cameraPermissionState = cameraPermissionPresenter.present()
        val roomInfo by room.roomInfoFlow.collectAsState()
        val roomAvatarUri = roomInfo.avatarUrl
        var roomAvatarUriEdited by rememberSaveable { mutableStateOf<String?>(null) }
        LaunchedEffect(roomAvatarUri) {
            // Every time the roomAvatar change (from sync), we can set the new avatar.
            temporaryUriDeleter.delete(roomAvatarUriEdited?.toUri())
            roomAvatarUriEdited = roomAvatarUri
        }

        val roomRawNameTrimmed = roomInfo.rawName.orEmpty().trim()
        var roomRawNameEdited by rememberSaveable { mutableStateOf("") }
        LaunchedEffect(roomRawNameTrimmed) {
            // Every time the rawName change (from sync), we can set the new name.
            roomRawNameEdited = roomRawNameTrimmed
        }
        val roomTopicTrimmed = roomInfo.topic.orEmpty().trim()
        var roomTopicEdited by rememberSaveable { mutableStateOf("") }
        LaunchedEffect(roomTopicTrimmed) {
            // Every time the topic change (from sync), we can set the new topic.
            roomTopicEdited = roomTopicTrimmed
        }

        val saveButtonEnabled by remember(
            roomRawNameTrimmed,
            roomTopicTrimmed,
            roomAvatarUri,
        ) {
            derivedStateOf {
                roomRawNameTrimmed != roomRawNameEdited.trim() ||
                    roomTopicTrimmed != roomTopicEdited.trim() ||
                    roomAvatarUri != roomAvatarUriEdited
            }
        }

        val permissions by room.permissionsAsState(RoomDetailsEditPermissions.DEFAULT) { perms ->
            perms.roomDetailsEditPermissions()
        }

        val cameraPhotoPicker = mediaPickerProvider.registerCameraPhotoPicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(roomAvatarUriEdited?.toUri())
                    roomAvatarUriEdited = uri.toString()
                }
            }
        )
        val galleryImagePicker = mediaPickerProvider.registerGalleryImagePicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(roomAvatarUriEdited?.toUri())
                    roomAvatarUriEdited = uri.toString()
                }
            }
        )

        LaunchedEffect(cameraPermissionState.permissionGranted) {
            if (cameraPermissionState.permissionGranted && pendingPermissionRequest) {
                pendingPermissionRequest = false
                cameraPhotoPicker.launch()
            }
        }

        val avatarActions by remember(roomAvatarUriEdited) {
            derivedStateOf {
                listOfNotNull(
                    AvatarAction.TakePhoto,
                    AvatarAction.ChoosePhoto,
                    AvatarAction.Remove.takeIf { roomAvatarUriEdited != null },
                ).toImmutableList()
            }
        }

        val saveAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val localCoroutineScope = rememberCoroutineScope()
        fun handleEvent(event: RoomDetailsEditEvent) {
            when (event) {
                is RoomDetailsEditEvent.Save -> localCoroutineScope.saveChanges(
                    currentNameTrimmed = roomRawNameTrimmed,
                    newNameTrimmed = roomRawNameEdited.trim(),
                    currentTopicTrimmed = roomTopicTrimmed,
                    newTopicTrimmed = roomTopicEdited.trim(),
                    currentAvatar = roomAvatarUri?.toUri(),
                    newAvatarUri = roomAvatarUriEdited?.toUri(),
                    action = saveAction,
                )
                is RoomDetailsEditEvent.HandleAvatarAction -> {
                    when (event.action) {
                        AvatarAction.ChoosePhoto -> galleryImagePicker.launch()
                        AvatarAction.TakePhoto -> if (cameraPermissionState.permissionGranted) {
                            cameraPhotoPicker.launch()
                        } else {
                            pendingPermissionRequest = true
                            cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                        }
                        AvatarAction.Remove -> {
                            temporaryUriDeleter.delete(roomAvatarUriEdited?.toUri())
                            roomAvatarUriEdited = null
                        }
                    }
                }

                is RoomDetailsEditEvent.UpdateRoomName -> roomRawNameEdited = event.name
                is RoomDetailsEditEvent.UpdateRoomTopic -> roomTopicEdited = event.topic
                RoomDetailsEditEvent.CloseDialog -> saveAction.value = AsyncAction.Uninitialized
                RoomDetailsEditEvent.OnBackPress -> if (saveButtonEnabled.not() || saveAction.value == AsyncAction.ConfirmingCancellation) {
                    // No changes to save or already confirming exit without saving
                    saveAction.value = AsyncAction.Success(Unit)
                } else {
                    saveAction.value = AsyncAction.ConfirmingCancellation
                }
            }
        }

        return RoomDetailsEditState(
            roomId = room.roomId,
            roomRawName = roomRawNameEdited,
            canChangeName = permissions.canEditName,
            roomTopic = roomTopicEdited,
            canChangeTopic = permissions.canEditTopic,
            roomAvatarUrl = roomAvatarUriEdited,
            canChangeAvatar = permissions.canEditAvatar,
            avatarActions = avatarActions,
            saveButtonEnabled = saveButtonEnabled,
            saveAction = saveAction.value,
            cameraPermissionState = cameraPermissionState,
            isSpace = roomInfo.isSpace,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 保存房间详情更改的协程方法
     *
     * 将用户编辑的房间名称、主题和头像保存到Matrix服务器
     *
     * @param currentNameTrimmed 当前房间名称（已去除首尾空格）
     * @param newNameTrimmed 新房间名称（已去除首尾空格）
     * @param currentTopicTrimmed 当前房间主题（已去除首尾空格）
     * @param newTopicTrimmed 新房间主题（已去除首尾空格）
     * @param currentAvatar 当前头像URI
     * @param newAvatarUri 新头像URI
     * @param action 用于更新保存操作状态的MutableState
     */
    private fun CoroutineScope.saveChanges(
        currentNameTrimmed: String,
        newNameTrimmed: String,
        currentTopicTrimmed: String,
        newTopicTrimmed: String,
        currentAvatar: Uri?,
        newAvatarUri: Uri?,
        action: MutableState<AsyncAction<Unit>>,
    ) = launch {
        val results = mutableListOf<Result<Unit>>()
        suspend {
            if (newTopicTrimmed != currentTopicTrimmed) {
                results.add(room.setTopic(newTopicTrimmed).onFailure {
                    Timber.e(it, "Failed to set room topic")
                })
            }
            if (newNameTrimmed.isNotEmpty() && newNameTrimmed != currentNameTrimmed) {
                results.add(room.setName(newNameTrimmed).onFailure {
                    Timber.e(it, "Failed to set room name")
                })
            }
            if (newAvatarUri != currentAvatar) {
                results.add(updateAvatar(newAvatarUri).onFailure {
                    Timber.e(it, "Failed to update avatar")
                })
            }
            if (results.all { it.isSuccess }) Unit else results.first { it.isFailure }.getOrThrow()
        }.runCatchingUpdatingState(action)
    }

    /**
     * 更新房间头像
     *
     * 处理头像的上传或删除，包括图片预处理和上传到Matrix服务器
     *
     * @param avatarUri 新的头像URI，如果为null表示删除头像
     * @return 操作结果，成功返回Unit，失败返回异常
     */
    private suspend fun updateAvatar(avatarUri: Uri?): Result<Unit> {
        return runCatchingExceptions {
            if (avatarUri != null) {
                val preprocessed = mediaPreProcessor.process(
                    uri = avatarUri,
                    mimeType = MimeTypes.Jpeg,
                    deleteOriginal = false,
                    mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
                ).getOrThrow()
                room.updateAvatar(MimeTypes.Jpeg, preprocessed.file.readBytes()).getOrThrow()
            } else {
                room.removeAvatar().getOrThrow()
            }
        }
    }
}
