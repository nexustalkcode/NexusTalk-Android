/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.filters.RoomListFilter
import io.element.android.features.home.impl.filters.RoomListFiltersEmptyStateResources
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.aRoomListFiltersState
import io.element.android.features.home.impl.filters.selection.FilterSelectionState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.roomlist.RoomListContentState
import io.element.android.features.home.impl.roomlist.RoomListContentStateProvider
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.SecurityBannerState
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import timber.log.Timber

@Composable
fun RoomListContentView(
    contentState: RoomListContentState,
    filtersState: RoomListFiltersState,
    lazyListState: LazyListState,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    when (contentState) {
        is RoomListContentState.Skeleton -> {
            SkeletonView(
                modifier = modifier,
                count = contentState.count,
                contentPadding = contentPadding,
            )
        }
        is RoomListContentState.Empty -> {
            EmptyView(
                modifier = modifier.padding(contentPadding),
                state = contentState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onCreateRoomClick = onCreateRoomClick,
            )
        }
        is RoomListContentState.Rooms -> {
            RoomsView(
                modifier = modifier,
                state = contentState,
                hideInvitesAvatars = hideInvitesAvatars,
                filtersState = filtersState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = onRoomClick,
                lazyListState = lazyListState,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun SkeletonView(
    count: Int,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        repeat(count) { index ->
            item {
                RoomSummaryPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyView(
    state: RoomListContentState.Empty,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = CommonStrings.screen_roomlist_empty_no_call_title,
            subtitle = R.string.screen_roomlist_empty_message_new,
            action = {
                GradientButton(
                    text = stringResource(CommonStrings.action_start_call),
                    onClick = onCreateRoomClick,
                    modifier = Modifier
                        .width(197.dp)
                        .height(50.dp)
                )
            },
            modifier = Modifier.align(Alignment.Center),
        )
        Box {
            RoomListBanner(
                securityBannerState = state.securityBannerState,
                fullScreenIntentPermissionsState = state.fullScreenIntentPermissionsState,
                batteryOptimizationState = state.batteryOptimizationState,
                showNewNotificationSoundBanner = state.showNewNotificationSoundBanner,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            )
        }
    }
}

@Composable
private fun RoomsView(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    filtersState: RoomListFiltersState,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    if (state.summaries.isEmpty() && filtersState.hasAnyFilterSelected) {
        EmptyViewForFilterStates(
            selectedFilters = filtersState.selectedFilters(),
            modifier = modifier.fillMaxSize()
        )
    } else {
        RoomsViewList(
            state = state,
            hideInvitesAvatars = hideInvitesAvatars,
            eventSink = eventSink,
            onSetUpRecoveryClick = onSetUpRecoveryClick,
            onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            onRoomClick = onRoomClick,
            contentPadding = contentPadding,
            lazyListState = lazyListState,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun RoomsViewList(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val visibleRange by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val firstItemIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            val size = layoutInfo.visibleItemsInfo.size
            firstItemIndex until firstItemIndex + size
        }
    }
    val updatedEventSink by rememberUpdatedState(newValue = eventSink)
    LaunchedEffect(visibleRange) {
        updatedEventSink(RoomListEvents.UpdateVisibleRange(visibleRange))
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        if (
            state.securityBannerState != SecurityBannerState.None ||
            state.fullScreenIntentPermissionsState.shouldDisplayBanner ||
            state.batteryOptimizationState.shouldDisplayBanner ||
            state.showNewNotificationSoundBanner
        ) {
            item {
                RoomListBanner(
                    securityBannerState = state.securityBannerState,
                    fullScreenIntentPermissionsState = state.fullScreenIntentPermissionsState,
                    batteryOptimizationState = state.batteryOptimizationState,
                    showNewNotificationSoundBanner = state.showNewNotificationSoundBanner,
                    eventSink = updatedEventSink,
                    onSetUpRecoveryClick = onSetUpRecoveryClick,
                    onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                )
            }
        }

        // Note: do not use a key for the LazyColumn, or the scroll will not behave as expected if a room
        // is moved to the top of the list.
        itemsIndexed(
            items = state.summaries,
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            RoomSummaryRow(
                room = room,
                hideInviteAvatars = hideInvitesAvatars,
                isInviteSeen = room.displayType == RoomSummaryDisplayType.INVITE &&
                    state.seenRoomInvites.contains(room.roomId),
                onClick = onRoomClick,
                eventSink = eventSink,
            )
            if (index != state.summaries.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RoomListBanner(
    securityBannerState: SecurityBannerState,
    fullScreenIntentPermissionsState: io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState,
    batteryOptimizationState: io.element.android.libraries.push.api.battery.BatteryOptimizationState,
    showNewNotificationSoundBanner: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
) {
    LaunchedEffect(
        securityBannerState,
        fullScreenIntentPermissionsState.shouldDisplayBanner,
        batteryOptimizationState.shouldDisplayBanner,
        showNewNotificationSoundBanner,
    ) {
        val selectedBanner = when {
            securityBannerState == SecurityBannerState.EnterRecoveryKey -> "EnterRecoveryKey"
            securityBannerState == SecurityBannerState.SetUpRecovery -> "SetUpRecovery"
            securityBannerState == SecurityBannerState.RecoveryKeyConfirmation -> "RecoveryKeyConfirmation"
            fullScreenIntentPermissionsState.shouldDisplayBanner -> "FullScreenIntentPermission"
            batteryOptimizationState.shouldDisplayBanner -> "BatteryOptimization"
            showNewNotificationSoundBanner -> "NewNotificationSound"
            else -> "None"
        }
        Timber.d(
            "RoomListBanner debug: render selected=%s security=%s fullScreen=%s battery=%s newSound=%s",
            selectedBanner,
            securityBannerState,
            fullScreenIntentPermissionsState.shouldDisplayBanner,
            batteryOptimizationState.shouldDisplayBanner,
            showNewNotificationSoundBanner,
        )
    }

    when (securityBannerState) {
        SecurityBannerState.EnterRecoveryKey -> {
            EnterRecoveryKeyBanner(
                onContinueClick = onConfirmRecoveryKeyClick,
                onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
            )
        }
        SecurityBannerState.SetUpRecovery -> {
            SetUpRecoveryKeyBanner(
                onContinueClick = onSetUpRecoveryClick,
                onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
            )
        }
        SecurityBannerState.RecoveryKeyConfirmation -> {
            EnterRecoveryKeyBanner(
                onContinueClick = onConfirmRecoveryKeyClick,
                onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
            )
        }
        SecurityBannerState.None -> if (fullScreenIntentPermissionsState.shouldDisplayBanner) {
            FullScreenIntentPermissionBanner(
                state = fullScreenIntentPermissionsState,
                onDismissClick = { eventSink(RoomListEvents.DismissFullScreenIntentPermissionBanner) },
            )
        } else if (batteryOptimizationState.shouldDisplayBanner) {
            BatteryOptimizationBanner(
                state = batteryOptimizationState,
                onDismissClick = { eventSink(RoomListEvents.DismissBatteryOptimizationBanner) },
            )
        } else if (showNewNotificationSoundBanner) {
            NewNotificationSoundBanner(
                onDismissClick = { eventSink(RoomListEvents.DismissNewNotificationSoundBanner) },
            )
        }
    }
}

@Composable
private fun EmptyViewForFilterStates(
    selectedFilters: ImmutableList<RoomListFilter>,
    modifier: Modifier = Modifier,
) {
    val emptyStateResources = RoomListFiltersEmptyStateResources.fromSelectedFilters(selectedFilters) ?: return
    EmptyScaffold(
        title = emptyStateResources.title,
        subtitle = emptyStateResources.subtitle,
        modifier = modifier,
    )
}

@Composable
private fun EmptyScaffold(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    modifier: Modifier = Modifier,
    action: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(horizontal = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(io.element.android.appicon.element.R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(118.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(30.dp))
        androidx.compose.material.Text(
            text = stringResource(title),
            style = ElementTheme.typography.fontHeadingMdBold.copy(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4EF4BE),  // 起始颜色
                        Color(0xFF018FE7)   // 结束颜色
                    )
                ),
            ),
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(subtitle),
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        action?.invoke(this)
    }
}

@PreviewsDayNight
@Composable
internal fun RoomListContentViewPreview(@PreviewParameter(RoomListContentStateProvider::class) state: RoomListContentState) = ElementPreview {
    RoomListContentView(
        contentState = state,
        filtersState = aRoomListFiltersState(
            filterSelectionStates = RoomListFilter.entries.map {
                FilterSelectionState(
                    filter = it,
                    isSelected = true
                )
            }
        ),
        hideInvitesAvatars = false,
        eventSink = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onRoomClick = {},
        onCreateRoomClick = {},
        lazyListState = rememberLazyListState(),
        contentPadding = PaddingValues(0.dp),
    )
}
