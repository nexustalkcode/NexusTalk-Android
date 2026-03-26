/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.annotations.ContributesNode
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.knockrequests.api.list.KnockRequestsListEntryPoint
import io.element.android.features.messages.api.MessagesEntryPoint
import io.element.android.features.poll.api.history.PollHistoryEntryPoint
import io.element.android.features.reportroom.api.ReportRoomEntryPoint
import io.element.android.features.rolesandpermissions.api.ChangeRoomMemberRolesEntryPoint
import io.element.android.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import io.element.android.features.rolesandpermissions.api.RolesAndPermissionsEntryPoint
import io.element.android.features.roomdetails.api.RoomDetailsEntryPoint
import io.element.android.features.roomdetails.impl.invite.RoomInviteMembersNode
import io.element.android.features.roomdetails.impl.members.RoomMemberListNode
import io.element.android.features.roomdetails.impl.members.details.RoomMemberDetailsNode
import io.element.android.features.roomdetails.impl.notificationsettings.RoomNotificationSettingsNode
import io.element.android.features.roomdetailsedit.api.RoomDetailsEditEntryPoint
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyEntryPoint
import io.element.android.features.userprofile.shared.UserProfileNodeHelper
import io.element.android.features.verifysession.api.OutgoingVerificationEntryPoint
import io.element.android.libraries.architecture.BackstackWithOverlayBox
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.overlay.operation.hide
import io.element.android.libraries.architecture.overlay.operation.show
import io.element.android.libraries.designsystem.utils.OpenUrlInTabView
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.verification.VerificationRequest
import io.element.android.libraries.mediaviewer.api.MediaGalleryEntryPoint
import io.element.android.libraries.mediaviewer.api.MediaViewerEntryPoint
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

/**
 * 房间详情流程节点
 *
 * 负责管理房间详情模块的整体导航流程。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 BaseFlowNode 基类，管理多个子页面的导航栈。
 *
 * 包含以下子页面导航目标：
 * - RoomDetails: 房间详情
 * - RoomMemberList: 成员列表
 * - RoomDetailsEdit: 房间编辑
 * - InviteMembers: 邀请成员
 * - RoomNotificationSettings: 通知设置
 * - RoomMemberDetails: 成员详情
 * - AvatarPreview: 头像预览
 * - PollHistory: 投票历史
 * - MediaGallery: 媒体库
 * - AdminSettings: 管理设置
 * - PinnedMessagesList: 固定消息列表
 * - KnockRequestsList: 敲门请求列表
 * - SecurityAndPrivacy: 安全与隐私
 * - VerifyUser: 用户验证
 * - ReportRoom: 举报房间
 * - SelectNewOwnersWhenLeaving: 离开时选择新管理员
 *
 * @see BaseFlowNode 基础流程节点
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomDetailsFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val pollHistoryEntryPoint: PollHistoryEntryPoint,
    private val elementCallEntryPoint: ElementCallEntryPoint,
    private val room: JoinedRoom,
    private val analyticsService: AnalyticsService,
    private val messagesEntryPoint: MessagesEntryPoint,
    private val knockRequestsListEntryPoint: KnockRequestsListEntryPoint,
    private val mediaViewerEntryPoint: MediaViewerEntryPoint,
    private val mediaGalleryEntryPoint: MediaGalleryEntryPoint,
    private val outgoingVerificationEntryPoint: OutgoingVerificationEntryPoint,
    private val reportRoomEntryPoint: ReportRoomEntryPoint,
    private val changeRoomMemberRolesEntryPoint: ChangeRoomMemberRolesEntryPoint,
    private val rolesAndPermissionsEntryPoint: RolesAndPermissionsEntryPoint,
    private val securityAndPrivacyEntryPoint: SecurityAndPrivacyEntryPoint,
    private val roomDetailsEditEntryPoint: RoomDetailsEditEntryPoint,
) : BaseFlowNode<RoomDetailsFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = plugins.filterIsInstance<RoomDetailsEntryPoint.Params>().first().initialElement.toNavTarget(),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 导航目标密封接口
     *
     * 定义房间详情模块中所有可能的导航目标。
     * 使用密封接口确保类型安全，只能创建预定义的子类型。
     *
     * @see Parcelable 可序列化接口
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 房间详情页面
         */
        @Parcelize
        data object RoomDetails : NavTarget

        /**
         * 房间成员列表页面
         */
        @Parcelize
        data object RoomMemberList : NavTarget

        /**
         * 房间编辑页面
         */
        @Parcelize
        data object RoomDetailsEdit : NavTarget

        /**
         * 邀请成员页面
         */
        @Parcelize
        data object InviteMembers : NavTarget

        /**
         * 通知设置页面
         *
         * @property showUserDefinedSettingStyle 是否显示用户定义设置样式。
         *   当从房间外部上下文展示时，UI 设计不同。
         *   Figma 设计：https://www.figma.com/file/0MMNu7cTOzLOlWb7ctTkv3/Element-X?type=design&node-id=5199-198932&mode=design&t=fTTvpuxYFjewYQOe-0
         */
        @Parcelize
        data class RoomNotificationSettings(
            val showUserDefinedSettingStyle: Boolean
        ) : NavTarget

        /**
         * 房间成员详情页面
         *
         * @property roomMemberId 成员的 UserID
         */
        @Parcelize
        data class RoomMemberDetails(val roomMemberId: UserId) : NavTarget

        /**
         * 头像预览页面
         *
         * @property name 名称
         * @property avatarUrl 头像URL
         */
        @Parcelize
        data class AvatarPreview(val name: String, val avatarUrl: String) : NavTarget

        /**
         * 投票历史页面
         */
        @Parcelize
        data object PollHistory : NavTarget

        /**
         * 媒体库页面
         */
        @Parcelize
        data object MediaGallery : NavTarget

        /**
         * 管理设置页面
         */
        @Parcelize
        data object AdminSettings : NavTarget

        /**
         * 固定消息列表页面
         */
        @Parcelize
        data object PinnedMessagesList : NavTarget

        /**
         * 敲门请求列表页面
         */
        @Parcelize
        data object KnockRequestsList : NavTarget

        /**
         * 安全与隐私页面
         */
        @Parcelize
        data object SecurityAndPrivacy : NavTarget

        /**
         * 用户验证页面
         *
         * @property userId 要验证的 UserID
         */
        @Parcelize
        data class VerifyUser(val userId: UserId) : NavTarget

        /**
         * 举报房间页面
         */
        @Parcelize
        data object ReportRoom : NavTarget

        /**
         * 离开时选择新管理员页面
         */
        @Parcelize
        data object SelectNewOwnersWhenLeaving : NavTarget
    }

    private val callback: RoomDetailsEntryPoint.Callback = callback()

    override fun onBuilt() {
        super.onBuilt()
        whenChildrenAttached {
            commonLifecycle: Lifecycle,
            roomDetailsNode: RoomDetailsNode,
            changeRoomMemberRolesNode: ChangeRoomMemberRolesEntryPoint.NodeProxy,
            ->
            commonLifecycle.coroutineScope.launch {
                val isNewOwnerSelected = changeRoomMemberRolesNode.waitForCompletion()
                withContext(NonCancellable) {
                    backstack.pop()
                    if (isNewOwnerSelected) {
                        roomDetailsNode.onNewOwnersSelected()
                    }
                }
            }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.RoomDetails -> {
                val roomDetailsCallback = object : RoomDetailsNode.Callback {
                    override fun navigateToRoomMemberList() {
                        backstack.push(NavTarget.RoomMemberList)
                    }

                    override fun navigateToRoomDetailsEdit() {
                        backstack.push(NavTarget.RoomDetailsEdit)
                    }

                    override fun navigateToInviteMembers() {
                        backstack.push(NavTarget.InviteMembers)
                    }

                    override fun navigateToRoomNotificationSettings() {
                        backstack.push(NavTarget.RoomNotificationSettings(showUserDefinedSettingStyle = false))
                    }

                    override fun navigateToAvatarPreview(name: String, url: String) {
                        overlay.show(NavTarget.AvatarPreview(name, url))
                    }

                    override fun navigateToPollHistory() {
                        backstack.push(NavTarget.PollHistory)
                    }

                    override fun navigateToMediaGallery() {
                        backstack.push(NavTarget.MediaGallery)
                    }

                    override fun navigateToAdminSettings() {
                        backstack.push(NavTarget.AdminSettings)
                    }

                    override fun navigateToPinnedMessagesList() {
                        backstack.push(NavTarget.PinnedMessagesList)
                    }

                    override fun navigateToKnockRequestsList() {
                        backstack.push(NavTarget.KnockRequestsList)
                    }

                    override fun navigateToSecurityAndPrivacy() {
                        backstack.push(NavTarget.SecurityAndPrivacy)
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        backstack.push(NavTarget.RoomMemberDetails(userId))
                    }

                    override fun navigateToRoomCall() {
                        val inputs = CallType.RoomCall(
                            sessionId = room.sessionId,
                            roomId = room.roomId,
                        )
                        analyticsService.captureInteraction(Interaction.Name.MobileRoomCallButton)
                        elementCallEntryPoint.startCall(inputs)
                    }

                    override fun navigateToReportRoom() {
                        backstack.push(NavTarget.ReportRoom)
                    }

                    override fun navigateToSelectNewOwnersWhenLeaving() {
                        backstack.push(NavTarget.SelectNewOwnersWhenLeaving)
                    }
                }
                createNode<RoomDetailsNode>(buildContext, listOf(roomDetailsCallback))
            }

            NavTarget.RoomMemberList -> {
                val roomMemberListCallback = object : RoomMemberListNode.Callback {
                    override fun navigateToRoomMemberDetails(roomMemberId: UserId) {
                        backstack.push(NavTarget.RoomMemberDetails(roomMemberId))
                    }

                    override fun navigateToInviteMembers() {
                        backstack.push(NavTarget.InviteMembers)
                    }
                }
                createNode<RoomMemberListNode>(buildContext, listOf(roomMemberListCallback))
            }

            NavTarget.RoomDetailsEdit -> {
                roomDetailsEditEntryPoint.createNode(this, buildContext)
            }

            NavTarget.InviteMembers -> {
                createNode<RoomInviteMembersNode>(buildContext)
            }

            is NavTarget.RoomNotificationSettings -> {
                val input = RoomNotificationSettingsNode.RoomNotificationSettingInput(navTarget.showUserDefinedSettingStyle)
                val callback = object : RoomNotificationSettingsNode.Callback {
                    override fun navigateToGlobalNotificationSettings() {
                        callback.navigateToGlobalNotificationSettings()
                    }
                }
                createNode<RoomNotificationSettingsNode>(buildContext, listOf(input, callback))
            }

            is NavTarget.RoomMemberDetails -> {
                val callback = object : UserProfileNodeHelper.Callback {
                    override fun navigateToAvatarPreview(username: String, avatarUrl: String) {
                        overlay.show(NavTarget.AvatarPreview(username, avatarUrl))
                    }

                    override fun navigateToRoom(roomId: RoomId) {
                        callback.navigateToRoom(roomId, emptyList())
                    }

                    override fun startCall(dmRoomId: RoomId) {
                        elementCallEntryPoint.startCall(CallType.RoomCall(roomId = dmRoomId, sessionId = room.sessionId))
                    }

                    override fun startVerifyUserFlow(userId: UserId) {
                        backstack.push(NavTarget.VerifyUser(userId))
                    }
                }
                val plugins = listOf(RoomMemberDetailsNode.RoomMemberDetailsInput(navTarget.roomMemberId), callback)
                createNode<RoomMemberDetailsNode>(buildContext, plugins)
            }
            is NavTarget.AvatarPreview -> {
                val callback = object : MediaViewerEntryPoint.Callback {
                    override fun onDone() {
                        overlay.hide()
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        // Cannot happen
                    }

                    override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) {
                        // Cannot happen
                    }
                }
                val params = mediaViewerEntryPoint.createParamsForAvatar(
                    filename = navTarget.name,
                    avatarUrl = navTarget.avatarUrl,
                )
                mediaViewerEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
            is NavTarget.PollHistory -> {
                pollHistoryEntryPoint.createNode(this, buildContext)
            }
            is NavTarget.MediaGallery -> {
                val callback = object : MediaGalleryEntryPoint.Callback {
                    override fun onBackClick() {
                        backstack.pop()
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        val permalinkData = PermalinkData.RoomLink(
                            roomIdOrAlias = room.roomId.toRoomIdOrAlias(),
                            eventId = eventId,
                        )
                        callback.handlePermalinkClick(permalinkData, pushToBackstack = false)
                    }

                    override fun forward(eventId: EventId, fromPinnedEvents: Boolean) {
                        callback.startForwardEventFlow(eventId, fromPinnedEvents)
                    }
                }
                mediaGalleryEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }

            is NavTarget.AdminSettings -> {
                val callback = object : RolesAndPermissionsEntryPoint.Callback {
                    override fun onDone() {
                        backstack.pop()
                    }
                }
                rolesAndPermissionsEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }
            NavTarget.PinnedMessagesList -> {
                val params = MessagesEntryPoint.Params(
                    MessagesEntryPoint.InitialTarget.PinnedMessages
                )
                val callback = object : MessagesEntryPoint.Callback {
                    override fun navigateToRoomDetails() = Unit

                    override fun navigateToRoomMemberDetails(userId: UserId) = Unit

                    override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) {
                        callback.handlePermalinkClick(data, pushToBackstack)
                    }

                    override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) {
                        callback.startForwardEventFlow(eventId, fromPinnedEvents)
                    }

                    override fun navigateToRoom(roomId: RoomId) {
                        callback.navigateToRoom(roomId, emptyList())
                    }
                }
                return messagesEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
            NavTarget.KnockRequestsList -> {
                knockRequestsListEntryPoint.createNode(this, buildContext)
            }
            NavTarget.SecurityAndPrivacy -> {
                val callback = object : SecurityAndPrivacyEntryPoint.Callback {
                    override fun onDone() {
                        backstack.pop()
                    }
                }
                securityAndPrivacyEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }
            is NavTarget.VerifyUser -> {
                val params = OutgoingVerificationEntryPoint.Params(
                    showDeviceVerifiedScreen = true,
                    verificationRequest = VerificationRequest.Outgoing.User(userId = navTarget.userId)
                )
                outgoingVerificationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = object : OutgoingVerificationEntryPoint.Callback {
                        override fun onDone() {
                            backstack.pop()
                        }

                        override fun onBack() {
                            backstack.pop()
                        }

                        override fun navigateToLearnMoreAboutEncryption() {
                            learnMoreUrl.value = LearnMoreConfig.ENCRYPTION_URL
                        }
                    },
                )
            }
            is NavTarget.ReportRoom -> {
                reportRoomEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    roomId = room.roomId,
                )
            }

            is NavTarget.SelectNewOwnersWhenLeaving -> {
                changeRoomMemberRolesEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    room = room,
                    listType = ChangeRoomMemberRolesListType.SelectNewOwnersWhenLeaving,
                )
            }
        }
    }

    private val learnMoreUrl = mutableStateOf<String?>(null)

    @Composable
    override fun View(modifier: Modifier) {
        BackstackWithOverlayBox(modifier)

        OpenUrlInTabView(learnMoreUrl)
    }
}
