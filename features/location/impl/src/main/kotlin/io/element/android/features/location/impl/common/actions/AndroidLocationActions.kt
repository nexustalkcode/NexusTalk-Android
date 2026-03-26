/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.location.api.Location
import io.element.android.libraries.androidutils.system.openAppSettingsPage
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.di.annotations.ApplicationContext
import timber.log.Timber
import java.util.Locale

/**
 * Android 位置操作实现
 *
 * 在 Android 平台上实现位置操作功能，包括分享位置和打开设置。
 */
@ContributesBinding(AppScope::class)
class AndroidLocationActions(
    /** 应用上下文 */
    @ApplicationContext private val context: Context
) : LocationActions {
    override fun share(location: Location, label: String?) {
        runCatchingExceptions {
            val uri = buildUrl(location, label).toUri()
            val showMapsIntent = Intent(Intent.ACTION_VIEW).setData(uri)
            val chooserIntent = Intent.createChooser(showMapsIntent, null)
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        }.onSuccess {
            Timber.v("Open location succeed")
        }.onFailure {
            Timber.e(it, "Open location failed")
        }
    }

    override fun openSettings() {
        context.openAppSettingsPage()
    }
}

// 参考：https://developer.android.com/guide/components/intents-common#ViewMap

/**
 * 构建地理位置 URI
 *
 * 将位置信息转换为 Android 地图Intent使用的 URI 格式。
 *
 * @param location 地理位置信息
 * @param label 位置标签/描述
 * @param urlEncoder URL 编码函数，默认为 Uri::encode
 * @return String 格式化的地理位置 URI
 */
@VisibleForTesting
internal fun buildUrl(
    location: Location,
    label: String?,
    urlEncoder: (String) -> String = Uri::encode
): String {
    // This is needed so the coordinates are formatted with a dot as decimal separator
    val locale = Locale.ENGLISH
    return "geo:0,0?q=%.6f,%.6f (%s)".format(locale, location.lat, location.lon, urlEncoder(label.orEmpty()))
}
