/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.element.android.features.rageshake.impl.crash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.features.rageshake.api.crash.CrashDetectionEvent
import io.element.android.features.rageshake.api.crash.CrashDetectionPresenter
import io.element.android.features.rageshake.api.crash.CrashDetectionState
import io.element.android.libraries.androidutils.clipboard.ClipboardHelper
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * 默认崩溃检测 Presenter
 *
 * CrashDetectionPresenter 接口的实现，负责呈现崩溃检测功能的状态。
 * 监听崩溃数据存储，当检测到崩溃时显示相应的用户界面。
 *
 * @property buildMeta 构建元数据
 * @property crashDataStore 崩溃数据存储
 * @property rageshakeFeatureAvailability 摇一摇功能可用性
 */
@ContributesBinding(AppScope::class)
class DefaultCrashDetectionPresenter(
    private val buildMeta: BuildMeta,
    private val crashDataStore: CrashDataStore,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val clipboardHelper: ClipboardHelper,
    private val snackbarDispatcher: SnackbarDispatcher,
) : CrashDetectionPresenter {
    @Composable
    override fun present(): CrashDetectionState {
        val localCoroutineScope = rememberCoroutineScope()
        val crashDetected by remember {
            rageshakeFeatureAvailability.isAvailable()
                .flatMapLatest { isAvailable ->
                    if (isAvailable) {
                        crashDataStore.appHasCrashed()
                    } else {
                        flowOf(false)
                    }
                }
        }.collectAsState(false)
        val crashInfo by remember {
            crashDataStore.crashInfo()
        }.collectAsState(initial = "")
        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

        fun handleEvent(event: CrashDetectionEvent) {
            when (event) {
                CrashDetectionEvent.CopyDiagnosticInfo -> {
                    clipboardHelper.copyPlainText(
                        formatCrashDiagnosticInfo(
                            buildMeta = buildMeta,
                            crashInfo = crashInfo,
                        )
                    )
                    snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_copied_to_clipboard))
                    localCoroutineScope.resetAppHasCrashed()
                }
                CrashDetectionEvent.ResetAllCrashData -> localCoroutineScope.resetAll()
                CrashDetectionEvent.ResetAppHasCrashed -> localCoroutineScope.resetAppHasCrashed()
            }
        }

        return CrashDetectionState(
            appName = buildMeta.applicationName,
            crashDetected = crashDetected,
            snackbarMessage = snackbarMessage,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.resetAppHasCrashed() = launch {
        crashDataStore.resetAppHasCrashed()
    }

    private fun CoroutineScope.resetAll() = launch {
        crashDataStore.reset()
    }
}
