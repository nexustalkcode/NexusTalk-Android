/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.featureflag.api

import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.core.meta.BuildType

/**
 * To enable or disable a FeatureFlags, change the `defaultValue` value.
 */
enum class FeatureFlags(
    override val key: String,
    override val title: String,
    override val titleResource: Int,
    override val description: String? = null,
    override val descriptionResource: Int? = null,
    override val defaultValue: (BuildMeta) -> Boolean,
    override val isFinished: Boolean,
    override val isInLabs: Boolean = false,
) : Feature {
    RoomDirectorySearch(
        key = "feature.roomdirectorysearch",
        title = "Room directory search",
        titleResource = R.string.feature_flag_room_directory_search_title,
        description = "Allow user to search for public rooms in their homeserver",
        descriptionResource = R.string.feature_flag_room_directory_search_description,
        defaultValue = { false },
        isFinished = false,
    ),
    ShowBlockedUsersDetails(
        key = "feature.showBlockedUsersDetails",
        title = "Show blocked users details",
        titleResource = R.string.feature_flag_show_blocked_users_details_title,
        description = "Show the name and avatar of blocked users in the blocked users list",
        descriptionResource = R.string.feature_flag_show_blocked_users_details_description,
        defaultValue = { false },
        isFinished = false,
    ),
    SyncOnPush(
        key = "feature.syncOnPush",
        title = "Sync on push",
        titleResource = R.string.feature_flag_sync_on_push_title,
        description = "Subscribe to room sync when a push is received",
        descriptionResource = R.string.feature_flag_sync_on_push_description,
        defaultValue = { true },
        isFinished = false,
    ),
    OnlySignedDeviceIsolationMode(
        key = "feature.onlySignedDeviceIsolationMode",
        title = "Exclude insecure devices when sending/receiving messages",
        titleResource = R.string.feature_flag_only_signed_device_isolation_mode_title,
        description = "This setting controls how end-to-end encryption (E2E) keys are shared." +
            " Enabling it will prevent the inclusion of devices that have not been explicitly verified by their owners." +
            " You'll have to stop and re-open the app manually for that setting to take effect.",
        descriptionResource = R.string.feature_flag_only_signed_device_isolation_mode_description,
        defaultValue = { false },
        isFinished = false,
    ),
    EnableKeyShareOnInvite(
        key = "feature.enableKeyShareOnInvite",
        title = "Share encrypted history with new members",
        titleResource = R.string.feature_flag_enable_key_share_on_invite_title,
        description = "When inviting a user to an encrypted room that has history visibility set to \"shared\"," +
            " share encrypted history with that user, and accept encrypted history when you are invited to such a room." +
            "\nRequires an app restart to take effect." +
            "\n\nWARNING: this feature is EXPERIMENTAL and not all security precautions are implemented." +
            " Do not enable on production accounts.",
        descriptionResource = R.string.feature_flag_enable_key_share_on_invite_description,
        defaultValue = { false },
        isFinished = false,
    ),
    Knock(
        key = "feature.knock",
        title = "Ask to join",
        titleResource = R.string.feature_flag_knock_title,
        description = "Allow creating rooms which users can request access to.",
        descriptionResource = R.string.feature_flag_knock_description,
        defaultValue = { false },
        isFinished = false,
    ),
    CreateSpaces(
        key = "feature.createSpaces",
        title = "Create spaces",
        titleResource = R.string.feature_flag_create_spaces_title,
        description = "Allow creating spaces.",
        descriptionResource = R.string.feature_flag_create_spaces_description,
        defaultValue = { false },
        isFinished = false,
    ),
    SpaceSettings(
        key = "feature.spaceSettings",
        title = "Space settings",
        titleResource = R.string.feature_flag_space_settings_title,
        description = "Allow managing space settings such as details, permissions and privacy.",
        descriptionResource = R.string.feature_flag_space_settings_description,
        defaultValue = { false },
        isFinished = false,
    ),
    PrintLogsToLogcat(
        key = "feature.print_logs_to_logcat",
        title = "Print logs to logcat",
        titleResource = R.string.feature_flag_print_logs_to_logcat_title,
        description = "Print logs to logcat in addition to log files. Requires an app restart to take effect." +
            "\n\nWARNING: this will make the logs visible in the device logs and may affect performance. " +
            "It's not intended for daily usage in release builds.",
        descriptionResource = R.string.feature_flag_print_logs_to_logcat_description,
        defaultValue = { buildMeta -> buildMeta.buildType != BuildType.RELEASE },
        // False so it's displayed in the developer options screen
        isFinished = false,
    ),
    SelectableMediaQuality(
        key = "feature.selectable_media_quality",
        title = "Select media quality per upload",
        titleResource = R.string.feature_flag_selectable_media_quality_title,
        description = "You can select the media quality for each attachment you upload.",
        descriptionResource = R.string.feature_flag_selectable_media_quality_description,
        defaultValue = { false },
        // False so it's displayed in the developer options screen
        isFinished = false,
    ),
    Threads(
        key = "feature.thread_timeline",
        title = "Threads",
        titleResource = R.string.feature_flag_threads_title,
        description = "Renders thread messages as a dedicated timeline. Restarting the app is required for this setting to fully take effect.",
        descriptionResource = R.string.feature_flag_threads_description,
        defaultValue = { false },
        isFinished = false,
        isInLabs = true,
    ),
    MultiAccount(
        key = "feature.multi_account",
        title = "Multi accounts",
        titleResource = R.string.feature_flag_multi_account_title,
        description = "Allow the application to connect to multiple accounts at the same time." +
            "\n\nWARNING: this feature is EXPERIMENTAL and UNSTABLE.",
        descriptionResource = R.string.feature_flag_multi_account_description,
        defaultValue = { false },
        isFinished = false,
    ),
    SyncNotificationsWithWorkManager(
        key = "feature.sync_notifications_with_workmanager",
        title = "Sync notifications with WorkManager",
        titleResource = R.string.feature_flag_sync_notifications_with_workmanager_title,
        description = "Use WorkManager to schedule notification sync tasks when a push is received." +
            " This should improve reliability and battery usage.",
        descriptionResource = R.string.feature_flag_sync_notifications_with_workmanager_description,
        defaultValue = { true },
        isFinished = false,
    ),
    QrCodeLogin(
        key = "feature.qr_code_login",
        title = "QR Code Login",
        titleResource = R.string.feature_flag_qr_code_login_title,
        description = "Allow logging in on other devices using a QR code.",
        descriptionResource = R.string.feature_flag_qr_code_login_description,
        defaultValue = { false },
        isFinished = false,
    ),
    SignInWithClassic(
        key = "feature.signin_with_classic",
        title = "Sign in with Element Classic",
        titleResource = R.string.feature_flag_sign_in_with_classic_title,
        description = "Allow the application to sign in to the current Element Classic account.",
        descriptionResource = R.string.feature_flag_sign_in_with_classic_description,
        defaultValue = { false },
        isFinished = false,
    ),

    //打开视频记得复原这个两个权限
//    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
//
//    <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
    //还有 RoomListContentView里 取消了全屏通知
    VideoCall(
        key = "feature.video_call",
        title = "Video call",
        titleResource = R.string.feature_flag_video_call_title,
        description = "Show video call entry points in the app.",
        descriptionResource = R.string.feature_flag_video_call_description,
        defaultValue = { true },
        isFinished = false,
    ),
}
