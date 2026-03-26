/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.changeserver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.login.impl.accesscontrol.DefaultAccountProviderAccessControl
import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.error.ChangeServerError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 更改服务器Presenter
 *
 * 负责处理更改账户提供商（homeserver）的业务逻辑。
 * 验证用户选择的服务器是否允许连接，检查服务器是否支持 Matrix 协议，
 * 并将有效的服务器保存到数据源中。
 *
 * @property authenticationService Matrix 认证服务，用于设置 homeserver
 * @property accountProviderDataSource 账户提供商数据源，用于保存用户选择的服务器
 * @property defaultAccountProviderAccessControl 访问控制验证
 * @see ChangeServerState 对应的 UI 状态
 * @see ChangeServerEvents 用户交互事件
 */
@Inject
class ChangeServerPresenter(
    private val authenticationService: MatrixAuthenticationService,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val defaultAccountProviderAccessControl: DefaultAccountProviderAccessControl,
) : Presenter<ChangeServerState> {
    @Composable
    override fun present(): ChangeServerState {
        val localCoroutineScope = rememberCoroutineScope()

        val changeServerAction: MutableState<AsyncData<Unit>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        fun handleEvent(event: ChangeServerEvents) {
            when (event) {
                is ChangeServerEvents.ChangeServer -> localCoroutineScope.changeServer(event.accountProvider, changeServerAction)
                ChangeServerEvents.ClearError -> changeServerAction.value = AsyncData.Uninitialized
            }
        }

        return ChangeServerState(
            changeServerAction = changeServerAction.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.changeServer(
        data: AccountProvider,
        changeServerAction: MutableState<AsyncData<Unit>>,
    ) = launch {
        suspend {
            defaultAccountProviderAccessControl.assertIsAllowedToConnectToAccountProvider(
                title = data.title,
                accountProviderUrl = data.url,
            )
            val details = authenticationService.setHomeserver(data.url).getOrThrow()
            if (!details.isSupported) {
                throw ChangeServerError.UnsupportedServer
            }
            // Homeserver is valid, remember user choice
            accountProviderDataSource.setAccountProvider(data)
        }.runCatchingUpdatingState(changeServerAction, errorTransform = ChangeServerError::from)
    }
}
