/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.components.preferences

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CenteredTitleTopBar
import io.element.android.libraries.designsystem.theme.components.Scaffold

@Composable
fun PreferencePage(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    handleSystemBack: Boolean = false,
    snackbarHost: @Composable () -> Unit = {},
    topBarBackgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (handleSystemBack) {
        BackHandler(onBack = onBackClick)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = ElementTheme.colors.bgSubtleSecondary,
        topBar = {
            CenteredTitleTopBar(
                title = title,
                onBackClick = onBackClick,
                backgroundColor = topBarBackgroundColor,
            )
        },
        snackbarHost = snackbarHost,
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .consumeWindowInsets(it)
                    .verticalScroll(state = rememberScrollState())
            ) {
                content()
            }
        }
    )
}

@PreviewsDayNight
@Composable
internal fun PreferencePagePreview() = ElementPreview {
    PreferencePage(
        title = "Preference screen",
        onBackClick = {},
    ) {
        PreferenceCategory(
            title = "Category title",
        ) {
            PreferenceDivider()
            PreferenceSwitch(
                title = "Switch",
                icon = CompoundIcons.Threads(),
                isChecked = true,
                onCheckedChange = {},
            )
            PreferenceDivider()
            PreferenceCheckbox(
                title = "Checkbox",
                icon = CompoundIcons.Notifications(),
                isChecked = true,
                onCheckedChange = {},
            )
            PreferenceDivider()
            PreferenceSlide(
                title = "Slide",
                summary = "Summary",
                value = 0.75F,
                showIconAreaIfNoIcon = true,
                onValueChange = {},
            )
        }
    }
}
