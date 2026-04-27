/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.element.android.features.home.impl.R
import io.element.android.libraries.designsystem.components.Announcement
import io.element.android.libraries.designsystem.components.AnnouncementType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.push.api.battery.BatteryOptimizationEvents
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import io.element.android.libraries.push.api.battery.aBatteryOptimizationState
import timber.log.Timber
import java.util.Locale

private const val batteryOptimizationDebugTag = "BatteryOptimizationDebug"

/**
 * 电池优化横幅
 *
 * 渲染电池优化提示横幅，当应用受到电池优化限制时显示。
 * 用户可以通过横幅中的操作按钮跳转到系统设置禁用电池优化。
 *
 * @param state 电池优化状态
 * @param modifier 修饰符
 */
@Composable
internal fun BatteryOptimizationBanner(
    state: BatteryOptimizationState,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isHonorOrHuaweiDevice = rememberIsHonorOrHuaweiDevice()
    val description = if (isHonorOrHuaweiDevice) {
        stringResource(
            R.string.screen_roomlist_battery_optimization_honor_huawei_content,
            stringResource(io.element.android.appconfig.R.string.app_name),
        )
    } else {
        stringResource(
            R.string.screen_roomlist_battery_optimization_explicit_content,
            stringResource(io.element.android.appconfig.R.string.app_name),
        )
    }
    val actionText = if (isHonorOrHuaweiDevice) {
        stringResource(R.string.screen_roomlist_battery_optimization_honor_huawei_action)
    } else {
        stringResource(io.element.android.libraries.ui.strings.CommonStrings.action_go_to_settings)
    }

    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_battery_optimization_title_android),
        description = description,
        type = AnnouncementType.Actionable(
            actionText = actionText,
            onActionClick = {
                Timber.tag(batteryOptimizationDebugTag).i("BatteryOptimizationBanner.click_go_to_settings")
                state.eventSink(BatteryOptimizationEvents.RequestDisableOptimizations)
            },
            onDismissClick = onDismissClick,
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun BatteryOptimizationBannerPreview() = ElementPreview {
    BatteryOptimizationBanner(
        state = aBatteryOptimizationState(),
        onDismissClick = {},
    )
}

@Composable
private fun rememberIsHonorOrHuaweiDevice(): Boolean {
    val normalizedManufacturer = Build.MANUFACTURER.orEmpty().lowercase(Locale.ROOT)
    val normalizedBrand = Build.BRAND.orEmpty().lowercase(Locale.ROOT)
    return normalizedManufacturer.contains("honor") ||
        normalizedBrand.contains("honor") ||
        normalizedManufacturer.contains("huawei") ||
        normalizedBrand.contains("huawei")
}
