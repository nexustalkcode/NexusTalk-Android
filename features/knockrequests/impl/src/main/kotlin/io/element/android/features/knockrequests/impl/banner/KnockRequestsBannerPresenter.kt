/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.features.knockrequests.impl.data.KnockRequestPresentable
import io.element.android.features.knockrequests.impl.data.KnockRequestsService
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.mapState
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 接受错误显示持续时间（毫秒） */
private const val ACCEPT_ERROR_DISPLAY_DURATION = 1500L

/**
 * 敲门请求横幅 Presenter
 *
 * 负责处理房间列表中敲门请求横幅的业务逻辑和状态管理。
 * 管理敲门请求的快速接受、忽略和显示控制。
 *
 * @property knockRequestsService 敲门请求服务
 * @property sessionCoroutineScope 会话级别的协程作用域
 */
@Inject
class KnockRequestsBannerPresenter(
    private val knockRequestsService: KnockRequestsService,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) : Presenter<KnockRequestsBannerState> {
    /**
     * 生成界面状态
     *
     * @return KnockRequestsBannerState 敲门请求横幅状态
     */
    @Composable
    override fun present(): KnockRequestsBannerState {
        val knockRequests by remember {
            knockRequestsService.knockRequestsFlow.mapState { knockRequests ->
                knockRequests.dataOrNull().orEmpty()
                    .filter { !it.isSeen }
                    .toImmutableList()
            }
        }.collectAsState()

        val permissions by knockRequestsService.permissionsFlow.collectAsState()
        val showAcceptError = remember { mutableStateOf(false) }

        val shouldShowBanner by remember {
            derivedStateOf {
                permissions.hasAny && knockRequests.isNotEmpty()
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 敲门请求横幅事件
         */
        fun handleEvent(event: KnockRequestsBannerEvents) {
            when (event) {
                is KnockRequestsBannerEvents.AcceptSingleRequest -> {
                    sessionCoroutineScope.acceptSingleKnockRequest(
                        knockRequests = knockRequests,
                        displayAcceptError = showAcceptError,
                    )
                }
                is KnockRequestsBannerEvents.Dismiss -> {
                    sessionCoroutineScope.launch {
                        knockRequestsService.markAllKnockRequestsAsSeen()
                    }
                }
            }
        }

        return KnockRequestsBannerState(
            knockRequests = knockRequests,
            displayAcceptError = showAcceptError.value,
            canAccept = permissions.canAccept,
            isVisible = shouldShowBanner,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 接受单个敲门请求
     *
     * @param knockRequests 敲门请求列表
     * @param displayAcceptError 是否显示接受错误
     */
    private fun CoroutineScope.acceptSingleKnockRequest(
        knockRequests: List<KnockRequestPresentable>,
        displayAcceptError: MutableState<Boolean>,
    ) = launch {
        val knockRequest = knockRequests.singleOrNull()
        if (knockRequest != null) {
            knockRequestsService.acceptKnockRequest(knockRequest, optimistic = true)
                .onFailure {
                    displayAcceptError.value = true
                    delay(ACCEPT_ERROR_DISPLAY_DURATION)
                    displayAcceptError.value = false
                }
        }
    }
}
