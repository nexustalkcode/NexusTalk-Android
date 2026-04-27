/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import io.element.android.libraries.designsystem.theme.components.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.roomcall.api.hasPermissionToJoin
import io.element.android.features.userprofile.api.UserProfileVerificationState
import io.element.android.features.userprofile.shared.blockuser.BlockUserDialogs
import io.element.android.features.userprofile.shared.blockuser.BlockUserSection
import io.element.android.libraries.androidutils.system.copyToClipboard
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.atomic.atoms.MatrixBadgeAtom
import io.element.android.libraries.designsystem.atomic.molecules.MatrixBadgeRowMolecule
import io.element.android.libraries.designsystem.components.ClickableLinkText
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.avatar.DmAvatars
import io.element.android.libraries.designsystem.components.button.MainActionButton
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.components.preferences.PreferenceCategory
import io.element.android.libraries.designsystem.components.preferences.PreferenceSwitch
import io.element.android.libraries.designsystem.modifiers.niceClickable
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.ElementPreviewLight
import io.element.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.DropdownMenu
import io.element.android.libraries.designsystem.theme.components.DropdownMenuItem
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.CenteredTitleTopBar
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.api.room.getBestName
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.analytics.compose.LocalAnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间详情视图
 *
 * Composable 函数，用于渲染房间详情页面。
 * 根据房间类型（普通房间/DM）显示不同的 UI 布局。
 * 包含房间信息、成员列表、通知设置、收藏、投票等功能入口。
 *
 * @param state 房间详情状态
 * @param goBack 返回回调
 * @param onActionClick 操作按钮点击回调
 * @param onShareRoom 分享房间回调
 * @param openRoomMemberList 打开成员列表回调
 * @param openRoomNotificationSettings 打开通知设置回调
 * @param invitePeople 邀请人员回调
 * @param openAvatarPreview 打开头像预览回调
 * @param openPollHistory 打开投票历史回调
 * @param openMediaGallery 打开媒体库回调
 * @param openAdminSettings 打开管理设置回调
 * @param onJoinCallClick 加入通话点击回调
 * @param onPinnedMessagesClick 固定消息点击回调
 * @param onKnockRequestsClick 敲门请求点击回调
 * @param onSecurityAndPrivacyClick 安全与隐私点击回调
 * @param onProfileClick 个人资料点击回调
 * @param onReportRoomClick 举报房间点击回调
 * @param modifier 视图修饰符
 * @param leaveRoomView 离开房间视图
 * @see RoomDetailsState 房间详情状态
 */
@Composable
fun RoomDetailsView(
    state: RoomDetailsState,
    goBack: () -> Unit,
    onActionClick: (RoomDetailsAction) -> Unit,
    onShareRoom: () -> Unit,
    openRoomMemberList: () -> Unit,
    openRoomNotificationSettings: () -> Unit,
    invitePeople: () -> Unit,
    openAvatarPreview: (name: String, url: String) -> Unit,
    openPollHistory: () -> Unit,
    openMediaGallery: () -> Unit,
    openAdminSettings: () -> Unit,
    onJoinCallClick: () -> Unit,
    onPinnedMessagesClick: () -> Unit,
    onKnockRequestsClick: () -> Unit,
    onSecurityAndPrivacyClick: () -> Unit,
    onProfileClick: (UserId) -> Unit,
    onReportRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
    leaveRoomView: @Composable () -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)
    Scaffold(
        modifier = modifier,
        containerColor = ElementTheme.colors.bgSubtleSecondary,
        topBar = {
            CenteredTitleTopBar(
                title = state.roomName,
                onBackClick = goBack,
                actions = {
                    if (state.canEdit) {
                        TextButton(
                            text = stringResource(CommonStrings.action_edit),
                            onClick = { onActionClick(RoomDetailsAction.Edit) },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .consumeWindowInsets(padding)
        ) {
            leaveRoomView()

            when (state.roomType) {
                RoomDetailsType.Room -> {
                    RoomHeaderSection(
                        avatarUrl = state.roomAvatarUrl,
                        roomId = state.roomId,
                        roomName = state.roomName,
                        roomAlias = state.roomAlias,
                        heroes = state.heroes,
                        isTombstoned = state.isTombstoned,
                        openAvatarPreview = { avatarUrl ->
                            openAvatarPreview(state.roomName, avatarUrl)
                        },
                        onSubtitleClick = { subtitle ->
                            state.eventSink(RoomDetailsEvent.CopyToClipboard(subtitle))
                        }
                    )
                }
                is RoomDetailsType.Dm -> {
                    DmHeaderSection(
                        me = state.roomType.me,
                        otherMember = state.roomType.otherMember,
                        roomName = state.roomName,
                        openAvatarPreview = { name, avatarUrl ->
                            openAvatarPreview(name, avatarUrl)
                        },
                        onSubtitleClick = { subtitle ->
                            state.eventSink(RoomDetailsEvent.CopyToClipboard(subtitle))
                        }
                    )
                }
            }
            BadgeList(
                roomBadge = state.roomBadges,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(32.dp))
            MainActionsSection(
                state = state,
                onShareRoom = onShareRoom,
                onInvitePeople = invitePeople,
                onCall = onJoinCallClick,
            )
            Spacer(Modifier.height(12.dp))

            if (state.roomTopic !is RoomTopicState.Hidden) {
                TopicSection(
                    roomTopic = state.roomTopic,
                    onActionClick = onActionClick,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {
                PreferenceCategory(showTopDivider = false) {
                    if (state.roomNotificationSettings != null) {
                        NotificationItem(
                            isDefaultMode = state.roomNotificationSettings.isDefault,
                            openRoomNotificationSettings = openRoomNotificationSettings
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    }

                    FavoriteItem(
                        isFavorite = state.isFavorite,
                        onFavoriteChanges = {
                            state.eventSink(RoomDetailsEvent.SetFavorite(it))
                        }
                    )

                    if (state.canShowSecurityAndPrivacy) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        SecurityAndPrivacyItem(
                            onClick = onSecurityAndPrivacyClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {

                state.roomMemberDetailsState?.let { dmMemberDetails ->
                    ProfileItem(
                        verificationState = dmMemberDetails.verificationState,
                        onClick = { onProfileClick(dmMemberDetails.userId) }
                    )
                }

                if (state.roomType is RoomDetailsType.Room) {
                    PreferenceCategory(showTopDivider = false) {
                        MembersItem(
                            memberCount = state.memberCount,
                            hasVerificationViolations = state.hasMemberVerificationViolations,
                            openRoomMemberList = openRoomMemberList,
                        )
                        if (state.canShowKnockRequests) {
                            KnockRequestsItem(
                                knockRequestsCount = state.knockRequestsCount,
                                onKnockRequestsClick = onKnockRequestsClick
                            )
                        }
                        if (state.displayRolesAndPermissionsSettings) {
                            ListItem(
                                headlineContent = { Text(stringResource(R.string.screen_room_details_roles_and_permissions)) },
                                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Admin())),
                                onClick = openAdminSettings,
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
            PreferenceCategory(showTopDivider = false) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    PinnedMessagesItem(
                        pinnedMessagesCount = state.pinnedMessagesCount,
                        onPinnedMessagesClick = onPinnedMessagesClick
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    PollsItem(
                        openPollHistory = openPollHistory
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    MediaGalleryItem(
                        onClick = openMediaGallery
                    )
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            if (state.roomType is RoomDetailsType.Dm && state.roomMemberDetailsState != null) {
                val roomMemberState = state.roomMemberDetailsState
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    BlockUserSection(roomMemberState)
                }
                BlockUserDialogs(roomMemberState)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 敲门请求项组件
 *
 * Composable 函数，用于渲染敲门请求列表项。
 * 显示请求数量（如果有）。
 *
 * @param knockRequestsCount 敲门请求数量
 * @param onKnockRequestsClick 点击回调
 */
@Composable
private fun KnockRequestsItem(knockRequestsCount: Int?, onKnockRequestsClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.screen_room_details_requests_to_join_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.AskToJoin())),
        trailingContent = if (knockRequestsCount == null || knockRequestsCount == 0) {
            null
        } else {
            ListItemContent.Counter(knockRequestsCount)
        },
        onClick = onKnockRequestsClick,
    )
}

/**
 * 主要操作区域组件
 *
 * Composable 函数，用于渲染房间详情页面的主要操作按钮区域。
 * 包括静音/取消静音、呼叫、邀请成员、分享房间等操作。
 *
 * @param state 房间详情状态
 * @param onShareRoom 分享房间回调
 * @param onInvitePeople 邀请人员回调
 * @param onCall 呼叫回调
 */
@Composable
private fun MainActionsSection(
    state: RoomDetailsState,
    onShareRoom: () -> Unit,
    onInvitePeople: () -> Unit,
    onCall: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        state.roomNotificationSettings?.let { roomNotificationSettings ->
            if (roomNotificationSettings.mode == RoomNotificationMode.MUTE) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
                ) {
                    MainActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        title = stringResource(CommonStrings.common_unmute),
                        imageVector = CompoundIcons.NotificationsOff(),
                        onClick = {
                            state.eventSink(RoomDetailsEvent.UnmuteNotification)
                        },
                        iconTint = ElementTheme.colors.textSecondary
                    )
                }
            } else {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
                ) {
                    MainActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        title = stringResource(CommonStrings.common_mute),
                        imageVector = CompoundIcons.Notifications(),
                        onClick = {
                            state.eventSink(RoomDetailsEvent.MuteNotification)
                        },
                    )
                }
            }
        }
        if (state.roomCallState.hasPermissionToJoin()) {
            // TODO Improve the view depending on all the cases here?
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
            ) {
                MainActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    title = stringResource(CommonStrings.action_call),
                    imageVector = CompoundIcons.VideoCall(),
                    onClick = onCall,
                    iconTint = ElementTheme.colors.textSecondary
                )
            }
        }
        if (state.roomType is RoomDetailsType.Room) {
            if (state.canInvite) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
                ) {
                    MainActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        title = stringResource(CommonStrings.action_invite),
                        imageVector = CompoundIcons.UserAdd(),
                        onClick = onInvitePeople,
                        iconTint = ElementTheme.colors.textSecondary
                    )
                }
            }
            // Share CTA should be hidden for DMs
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
            ) {
                MainActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    title = stringResource(CommonStrings.action_share),
                    imageVector = CompoundIcons.ShareAndroid(),
                    onClick = onShareRoom,
                    iconTint = ElementTheme.colors.textSecondary
                )
            }
        }
    }
}

/**
 * 房间头部区域组件
 *
 * Composable 函数，用于渲染房间详情页面的头部区域。
 * 显示房间头像、名称和别名。
 *
 * @param avatarUrl 头像URL
 * @param roomId 房间ID
 * @param roomName 房间名称
 * @param roomAlias 房间别名
 * @param heroes 重要成员列表
 * @param isTombstoned 是否为墓碑状态
 * @param openAvatarPreview 打开头像预览回调
 * @param onSubtitleClick 副标题点击回调
 * @see MatrixUser Matrix用户
 */
@Composable
private fun RoomHeaderSection(
    avatarUrl: String?,
    roomId: RoomId,
    roomName: String,
    roomAlias: RoomAlias?,
    heroes: ImmutableList<MatrixUser>,
    isTombstoned: Boolean,
    openAvatarPreview: (url: String) -> Unit,
    onSubtitleClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Avatar(
            avatarData = AvatarData(roomId.value, roomName, avatarUrl, AvatarSize.RoomDetailsHeader),
            avatarType = AvatarType.Room(
                heroes = heroes.map { user ->
                    user.getAvatarData(size = AvatarSize.RoomDetailsHeader)
                }.toImmutableList(),
                isTombstoned = isTombstoned,
            ),
            contentDescription = stringResource(CommonStrings.a11y_room_avatar),
            modifier = Modifier
                .clickable(
                    enabled = avatarUrl != null,
                    onClickLabel = stringResource(CommonStrings.action_view),
                ) {
                    openAvatarPreview(avatarUrl!!)
                }
                .testTag(TestTags.roomDetailAvatar)
        )
        TitleAndSubtitle(
            title = roomName,
            subtitle = roomAlias?.value,
            onSubtitleClick = onSubtitleClick,
        )
    }
}

/**
 * DM头部区域组件
 *
 * Composable 函数，用于渲染 DM（直接消息）详情页面的头部区域。
 * 显示当前用户和其他用户的头像组合、名称和用户ID。
 *
 * @param me 当前用户成员
 * @param otherMember 其他成员
 * @param roomName 房间名称
 * @param openAvatarPreview 打开头像预览回调
 * @param onSubtitleClick 副标题点击回调
 * @param modifier 视图修饰符
 * @see RoomMember 房间成员
 */
@Composable
private fun DmHeaderSection(
    me: RoomMember,
    otherMember: RoomMember,
    roomName: String,
    openAvatarPreview: (name: String, url: String) -> Unit,
    onSubtitleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DmAvatars(
            userAvatarData = me.getAvatarData(size = AvatarSize.DmCluster),
            otherUserAvatarData = otherMember.getAvatarData(size = AvatarSize.DmCluster),
            openAvatarPreview = { url -> openAvatarPreview(me.getBestName(), url) },
            openOtherAvatarPreview = { url -> openAvatarPreview(roomName, url) },
        )
        TitleAndSubtitle(
            title = roomName,
            subtitle = otherMember.userId.value,
            onSubtitleClick = onSubtitleClick,
        )
    }
}

/**
 * 标题和副标题组件
 *
 * Composable 函数，用于渲染标题和可选的副标题。
 * 标题居中显示，副标题可点击。
 *
 * @param title 标题文本
 * @param subtitle 副标题文本
 * @param onSubtitleClick 副标题点击回调
 */
@Composable
private fun TitleAndSubtitle(
    title: String,
    subtitle: String?,
    onSubtitleClick: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = ElementTheme.typography.fontHeadingLgBold.copy(color = ElementTheme.colors.textPrimary),
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                modifier = Modifier.niceClickable { onSubtitleClick(subtitle) },
                text = subtitle,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 徽章列表组件
 *
 * Composable 函数，用于渲染房间徽章列表。
 * 显示加密状态和公开状态徽章。
 *
 * @param roomBadge 房间徽章列表
 * @param modifier 视图修饰符
 * @see RoomBadge 房间徽章
 * @see ImmutableList 不可变列表
 */
@Composable
private fun BadgeList(
    roomBadge: ImmutableList<RoomBadge>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (roomBadge.isNotEmpty()) {
            MatrixBadgeRowMolecule(
                data = roomBadge.map {
                    it.toMatrixBadgeData()
                }.toImmutableList(),
            )
        }
    }
}

/**
 * 转换为矩阵徽章数据
 *
 * 扩展函数，将 RoomBadge 转换为 MatrixBadgeAtom.MatrixBadgeData。
 *
 * @return MatrixBadgeAtom.MatrixBadgeData 矩阵徽章数据
 * @see MatrixBadgeAtom.MatrixBadgeData 矩阵徽章数据
 */
@Composable
private fun RoomBadge.toMatrixBadgeData(): MatrixBadgeAtom.MatrixBadgeData {
    return when (this) {
        RoomBadge.ENCRYPTED -> {
            MatrixBadgeAtom.MatrixBadgeData(
                text = stringResource(R.string.screen_room_details_badge_encrypted),
                icon = CompoundIcons.LockSolid(),
                type = MatrixBadgeAtom.Type.Positive,
            )
        }
        RoomBadge.NOT_ENCRYPTED -> {
            MatrixBadgeAtom.MatrixBadgeData(
                text = stringResource(R.string.screen_room_details_badge_not_encrypted),
                icon = CompoundIcons.LockOff(),
                type = MatrixBadgeAtom.Type.Info,
            )
        }
        RoomBadge.PUBLIC -> {
            MatrixBadgeAtom.MatrixBadgeData(
                text = stringResource(R.string.screen_room_details_badge_public),
                icon = CompoundIcons.Public(),
                type = MatrixBadgeAtom.Type.Info,
            )
        }
    }
}

/**
 * 主题区域组件
 *
 * Composable 函数，用于渲染房间主题区域。
 * 根据主题状态显示添加主题按钮或现有主题内容。
 *
 * @param roomTopic 房间主题状态
 * @param onActionClick 操作点击回调
 * @see RoomTopicState 房间主题状态
 */
@Composable
private fun TopicSection(
    roomTopic: RoomTopicState,
    onActionClick: (RoomDetailsAction) -> Unit,
) {
    PreferenceCategory(
        title = stringResource(CommonStrings.common_topic),
        showTopDivider = false,
    ) {
        if (roomTopic is RoomTopicState.CanAddTopic) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {
                ListItem(
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus())),
                    headlineContent = {
                        Text(stringResource(id = R.string.screen_room_details_add_topic_title))
                    },
                    onClick = {
                        onActionClick(RoomDetailsAction.AddTopic)
                    },
                )
            }
        } else if (roomTopic is RoomTopicState.ExistingTopic) {
            ClickableLinkText(
                text = roomTopic.topic,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                interactionSource = remember { MutableInteractionSource() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary,
                ),
            )
        }
    }
}

/**
 * 通知设置项组件
 *
 * Composable 函数，用于渲染通知设置列表项。
 * 显示通知设置入口和当前通知模式。
 *
 * @param isDefaultMode 是否为默认模式
 * @param openRoomNotificationSettings 打开通知设置回调
 */
@Composable
private fun NotificationItem(
    isDefaultMode: Boolean,
    openRoomNotificationSettings: () -> Unit,
) {
    val subtitle = if (isDefaultMode) {
        stringResource(R.string.screen_room_details_notification_mode_default)
    } else {
        stringResource(R.string.screen_room_details_notification_mode_custom)
    }
    ListItem(
        headlineContent = { Text(text = stringResource(R.string.screen_room_details_notification_title)) },
        supportingContent = { Text(text = subtitle) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Notifications())),
        onClick = openRoomNotificationSettings,
    )
}

/**
 * 安全与隐私项组件
 *
 * Composable 函数，用于渲染安全与隐私设置列表项。
 *
 * @param onClick 点击回调
 * @param modifier 视图修饰符
 */
@Composable
private fun SecurityAndPrivacyItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.screen_room_details_security_and_privacy_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
        onClick = onClick,
        modifier = modifier,
    )
}

/**
 * 收藏项组件
 *
 * Composable 函数，用于渲染收藏开关列表项。
 *
 * @param isFavorite 是否已收藏
 * @param onFavoriteChanges 收藏状态变更回调
 */
@Composable
private fun FavoriteItem(
    isFavorite: Boolean,
    onFavoriteChanges: (Boolean) -> Unit,
) {
    PreferenceSwitch(
        icon = CompoundIcons.Favourite(),
        title = stringResource(id = CommonStrings.common_favourite),
        isChecked = isFavorite,
        onCheckedChange = onFavoriteChanges
    )
}

/**
 * 个人资料项组件
 *
 * Composable 函数，用于渲染 DM 详情页面的个人资料列表项。
 * 显示验证状态图标（已验证/验证失败）。
 *
 * @param verificationState 验证状态
 * @param onClick 点击回调
 */
@Composable
private fun ProfileItem(
    verificationState: UserProfileVerificationState,
    onClick: () -> Unit,
) {
    ListItem(
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserProfile())),
        headlineContent = { Text(stringResource(id = R.string.screen_room_details_profile_row_title)) },
        trailingContent = when (verificationState) {
            UserProfileVerificationState.VERIFIED -> ListItemContent.Icon(
                iconSource = IconSource.Vector(CompoundIcons.Verified()),
                tintColor = ElementTheme.colors.iconSuccessPrimary,
            )
            UserProfileVerificationState.VERIFICATION_VIOLATION -> ListItemContent.Icon(
                iconSource = IconSource.Vector(CompoundIcons.ErrorSolid()),
                tintColor = ElementTheme.colors.iconCriticalPrimary,
            )
            else -> null
        },
        onClick = onClick,
    )
}

/**
 * 成员项组件
 *
 * Composable 函数，用于渲染成员数量列表项。
 * 显示成员数量和验证违规警告图标。
 *
 * @param memberCount 成员数量
 * @param hasVerificationViolations 是否有验证违规
 * @param openRoomMemberList 打开成员列表回调
 */
@Composable
private fun MembersItem(
    memberCount: Long,
    hasVerificationViolations: Boolean,
    openRoomMemberList: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(stringResource(CommonStrings.common_people)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.User())),
        trailingContent = if (hasVerificationViolations) {
            ListItemContent.Icon(
                iconSource = IconSource.Vector(CompoundIcons.ErrorSolid()),
                tintColor = ElementTheme.colors.textCriticalPrimary,
            )
        } else {
            ListItemContent.Text(memberCount.toString())
        },
        onClick = openRoomMemberList,
    )
}

/**
 * 固定消息项组件
 *
 * Composable 函数，用于渲染固定消息列表项。
 * 显示固定消息数量。
 *
 * @param pinnedMessagesCount 固定消息数量
 * @param onPinnedMessagesClick 点击回调
 */
@Composable
private fun PinnedMessagesItem(
    pinnedMessagesCount: Int?,
    onPinnedMessagesClick: () -> Unit,
) {
    val analyticsService = LocalAnalyticsService.current
    ListItem(
        headlineContent = { Text(stringResource(R.string.screen_room_details_pinned_events_row_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Pin())),
        trailingContent =
            if (pinnedMessagesCount == null) {
                ListItemContent.Custom {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                }
            } else {
                ListItemContent.Text(pinnedMessagesCount.toString())
            },
        onClick = {
            analyticsService.captureInteraction(Interaction.Name.PinnedMessageRoomInfoButton)
            onPinnedMessagesClick()
        }
    )
}

/**
 * 投票项组件
 *
 * Composable 函数，用于渲染投票列表项。
 *
 * @param openPollHistory 打开投票历史回调
 */
@Composable
private fun PollsItem(
    openPollHistory: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.screen_polls_history_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Polls())),
        onClick = openPollHistory,
    )
}

/**
 * 媒体库项组件
 *
 * Composable 函数，用于渲染媒体库列表项。
 *
 * @param onClick 点击回调
 */
@Composable
private fun MediaGalleryItem(
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.screen_room_details_media_gallery_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Image())),
        onClick = onClick,
    )
}

/**
 * 其他操作区域组件
 *
 * Composable 函数，用于渲染房间详情页面的其他操作区域。
 * 包含离开房间等操作。
 *
 * @param canReportRoom 是否可以举报房间
 * @param onReportRoomClick 举报房间点击回调
 * @param onLeaveRoomClick 离开房间点击回调
 */
@Composable
private fun OtherActionsSection(
    canReportRoom: Boolean,
    onReportRoomClick: () -> Unit,
    onLeaveRoomClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
    ) {
        ListItem(
            headlineContent = {
                Text(stringResource(CommonStrings.action_leave_room))
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Leave())),
            style = ListItemStyle.Destructive,
            onClick = onLeaveRoomClick,
        )
    }
}

/**
 * 调试信息区域组件
 *
 * Composable 函数，用于渲染调试信息区域。
 * 仅在开发者模式下显示，包含房间ID和房间版本信息。
 *
 * @param roomId 房间ID
 * @param roomVersion 房间版本
 */
@Composable
private fun DebugInfoSection(
    roomId: RoomId,
    roomVersion: String?,
) {
    val context = LocalContext.current
    PreferenceCategory(showTopDivider = true) {
        val toastMessage = stringResource(CommonStrings.common_copied_to_clipboard)
        ListItem(
            headlineContent = {
                Text("Internal room ID")
            },
            supportingContent = {
                Text(
                    text = roomId.value,
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.textSecondary,
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Code())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Copy())),
            onClick = {
                context.copyToClipboard(
                    text = roomId.value,
                    toastMessage = toastMessage,
                )
            },
        )
        ListItem(
            headlineContent = {
                Text("Room version")
            },
            supportingContent = {
                Text(
                    text = roomVersion ?: "Unknown",
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.textSecondary,
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Info())),
        )
    }
}

@PreviewWithLargeHeight
@Composable
internal fun RoomDetailsPreview(@PreviewParameter(RoomDetailsStateProvider::class) state: RoomDetailsState) =
    ElementPreviewLight { ContentToPreview(state) }

@PreviewWithLargeHeight
@Composable
internal fun RoomDetailsDarkPreview(@PreviewParameter(RoomDetailsStateProvider::class) state: RoomDetailsState) =
    ElementPreviewDark { ContentToPreview(state) }

@PreviewWithLargeHeight
@Composable
internal fun RoomDetailsA11yPreview() = ElementPreview {
    ContentToPreview(
        state = aRoomDetailsState(displayAdminSettings = true)
    )
}

/**
 * 预览内容组件
 *
 * Composable 函数，用于在预览中渲染房间详情内容。
 * 标记为 @ExcludeFromCoverage 以排除代码覆盖率统计。
 *
 * @param state 房间详情状态
 * @see RoomDetailsState 房间详情状态
 * @see ExcludeFromCoverage 排除覆盖率统计
 */
@ExcludeFromCoverage
@Composable
private fun ContentToPreview(state: RoomDetailsState) {
    RoomDetailsView(
        state = state,
        goBack = {},
        onActionClick = {},
        onShareRoom = {},
        openRoomMemberList = {},
        openRoomNotificationSettings = {},
        invitePeople = {},
        openAvatarPreview = { _, _ -> },
        openPollHistory = {},
        openMediaGallery = {},
        openAdminSettings = {},
        onJoinCallClick = {},
        onPinnedMessagesClick = {},
        onKnockRequestsClick = {},
        onSecurityAndPrivacyClick = {},
        onProfileClick = {},
        onReportRoomClick = {},
        leaveRoomView = {},
    )
}
