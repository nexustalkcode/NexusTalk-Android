/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package io.element.android.features.login.impl.screens.changeaccountprovider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.accountprovider.AccountProviderOtherView
import io.element.android.features.login.impl.accountprovider.AccountProviderView
import io.element.android.features.login.impl.changeserver.ChangeServerEvents
import io.element.android.features.login.impl.changeserver.ChangeServerView
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar

/**
 * 更改账户提供商视图组件
 *
 * 用于显示账户提供商列表和选择界面的可组合组件。
 * 展示预设的服务器选项，以及搜索其他服务器的入口。
 *
 * @param state 更改账户提供商状态
 * @param onBackClick 返回按钮点击回调
 * @param onLearnMoreClick 了解更多按钮点击回调
 * @param onSuccess 成功选择服务器后的回调
 * @param onOtherProviderClick 点击"其他服务器"选项的回调
 * @param modifier 修饰符
 * @see <a href="https://www.figma.com/file/o9p34zmiuEpZRyvZXJZAYL/FTUE?type=design&node-id=604-60817">Figma 设计稿</a>
 */
@Composable
fun ChangeAccountProviderView(
    state: ChangeAccountProviderState,
    onBackClick: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onSuccess: () -> Unit,
    onOtherProviderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
            ) {
                IconTitleSubtitleMolecule(
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
                    iconStyle = BigIcon.Style.Default(CompoundIcons.HomeSolid()),
                    title = stringResource(id = R.string.screen_change_account_provider_title),
                    subTitle = stringResource(id = R.string.screen_change_account_provider_subtitle),
                )

                state.accountProviders.forEach { item ->
                    val alteredItem = if (item.isMatrixOrg) {
                        // Set the subtitle from the resource
                        item.copy(
                            subtitle = stringResource(id = R.string.screen_change_account_provider_matrix_org_subtitle),
                        )
                    } else {
                        item
                    }
                    AccountProviderView(
                        item = alteredItem,
                        onClick = {
                            state.changeServerState.eventSink.invoke(ChangeServerEvents.ChangeServer(alteredItem))
                        }
                    )
                }
                // Other
                if (state.canSearchForAccountProviders) {
                    AccountProviderOtherView(
                        onClick = onOtherProviderClick
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
            ChangeServerView(
                state = state.changeServerState,
                onLearnMoreClick = onLearnMoreClick,
                onSuccess = onSuccess,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ChangeAccountProviderViewPreview(@PreviewParameter(ChangeAccountProviderStateProvider::class) state: ChangeAccountProviderState) = ElementPreview {
    ChangeAccountProviderView(
        state = state,
        onBackClick = { },
        onLearnMoreClick = { },
        onSuccess = { },
        onOtherProviderClick = { },
    )
}
