/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.changeserver

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.login.impl.error.ChangeServerError
import io.element.android.libraries.architecture.AsyncData

/**
 * 更改服务器状态预览参数提供者
 *
 * 用于在 Compose 预览中提供不同状态的 ChangeServerState 测试数据。
 *
 * @see ChangeServerState 更改服务器状态
 * @see aChangeServerState 创建测试用状态的辅助函数
 */
open class ChangeServerStateProvider : PreviewParameterProvider<ChangeServerState> {
    override val values: Sequence<ChangeServerState>
        get() = sequenceOf(
            aChangeServerState(),
            aChangeServerState(changeServerAction = AsyncData.Failure(ChangeServerError.Error(null))),
            aChangeServerState(changeServerAction = AsyncData.Failure(ChangeServerError.SlidingSyncAlert)),
            aChangeServerState(
                changeServerAction = AsyncData.Failure(
                    ChangeServerError.UnauthorizedAccountProvider(
                        unauthorisedAccountProviderTitle = "example.com",
                        authorisedAccountProviderTitles = listOf("element.io", "element.org"),
                    )
                )
            ),
            aChangeServerState(
                changeServerAction = AsyncData.Failure(
                    ChangeServerError.NeedElementPro(
                        unauthorisedAccountProviderTitle = "example.com",
                        applicationId = "applicationId",
                    ),
                )
            ),
            aChangeServerState(
                changeServerAction = AsyncData.Failure(
                    ChangeServerError.UnsupportedServer
                )
            ),
        )
}

/**
 * 创建测试用更改服务器状态
 *
 * 辅助函数，用于在测试和预览中快速创建 ChangeServerState 对象。
 *
 * @param changeServerAction 更改服务器操作的异步状态
 * @return 配置好的 ChangeServerState 对象
 */
fun aChangeServerState(
    changeServerAction: AsyncData<Unit> = AsyncData.Uninitialized,
) = ChangeServerState(
    changeServerAction = changeServerAction,
    eventSink = {}
)
