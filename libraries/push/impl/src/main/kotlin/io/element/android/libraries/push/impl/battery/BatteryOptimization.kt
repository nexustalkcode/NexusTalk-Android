/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.battery

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.services.toolbox.api.intent.ExternalIntentLauncher
import timber.log.Timber
import java.util.Locale

private const val batteryOptimizationDebugTag = "BatteryOptimizationDebug"

interface BatteryOptimization {
    /**
     * Tells if the application ignores battery optimizations.
     *
     * Ignoring them allows the app to run in background to make background sync with the homeserver.
     * This user option appears on Android M but Android O enforces its usage and kills apps not
     * authorised by the user to run in background.
     *
     * @return true if battery optimisations are ignored
     */
    fun isIgnoringBatteryOptimizations(): Boolean

    /**
     * 小米和 Redmi 这类机会在没有失败记录时也需要主动提示。
     */
    fun shouldDisplayBannerWithoutPreviousPushFailure(): Boolean

    /**
     * 对荣耀/华为这类只能跳到厂商“启动管理”页、但又缺少稳定已配置检测能力的设备，
     * 在成功拉起设置页后直接结束后续重复提示，避免用户每次重进应用都看到相同横幅。
     */
    fun shouldDismissBannerAfterSuccessfulRequest(): Boolean

    /**
     * Request the user to disable battery optimizations for this app.
     * This will open the system settings where the user can disable battery optimizations.
     * See https://developer.android.com/training/monitoring-device-state/doze-standby#exemption-cases
     *
     * @return true if the intent was successfully started, false if the activity was not found
     */
    fun requestDisablingBatteryOptimization(): Boolean
}

@ContributesBinding(AppScope::class)
class AndroidBatteryOptimization(
    @ApplicationContext
    private val context: Context,
    private val externalIntentLauncher: ExternalIntentLauncher,
) : BatteryOptimization {
    override fun isIgnoringBatteryOptimizations(): Boolean {
        return context.getSystemService<PowerManager>()
            ?.isIgnoringBatteryOptimizations(context.packageName) == true
    }

    override fun shouldDisplayBannerWithoutPreviousPushFailure(): Boolean {
        return requiresProactiveBatteryOptimizationBanner(
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
        ) && !isIgnoringBatteryOptimizations()
    }

    override fun shouldDismissBannerAfterSuccessfulRequest(): Boolean {
        return isHuaweiOrHonorFamily(
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
        )
    }

    @SuppressLint("BatteryLife")
    override fun requestDisablingBatteryOptimization(): Boolean {
        val launchIntents = createBatteryOptimizationIntents(
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            packageName = context.packageName,
        )
        Timber.tag(batteryOptimizationDebugTag).i(
            "AndroidBatteryOptimization.request_disable_optimizations manufacturer=%s brand=%s packageName=%s ignoringOptimizations=%s candidateCount=%s",
            Build.MANUFACTURER,
            Build.BRAND,
            context.packageName,
            isIgnoringBatteryOptimizations(),
            launchIntents.size,
        )
        launchIntents.forEachIndexed { index, candidate ->
            if (launchIntent(candidate, index)) {
                return true
            }
        }
        Timber.tag(batteryOptimizationDebugTag).w(
            "AndroidBatteryOptimization.request_disable_optimizations no_candidate_succeeded manufacturer=%s brand=%s",
            Build.MANUFACTURER,
            Build.BRAND,
        )
        return false
    }

    private fun launchIntent(
        intent: Intent,
        index: Int,
    ): Boolean {
        Timber.tag(batteryOptimizationDebugTag).i(
            "AndroidBatteryOptimization.launch_candidate start index=%s action=%s component=%s data=%s",
            index,
            intent.action,
            intent.component?.flattenToShortString(),
            intent.data,
        )
        return try {
            externalIntentLauncher.launch(intent)
            Timber.tag(batteryOptimizationDebugTag).i(
                "AndroidBatteryOptimization.launch_candidate success index=%s action=%s component=%s",
                index,
                intent.action,
                intent.component?.flattenToShortString(),
            )
            true
        } catch (exception: Exception) {
            Timber.tag(batteryOptimizationDebugTag).w(
                exception,
                "AndroidBatteryOptimization.launch_candidate failed index=%s action=%s component=%s data=%s exception=%s",
                index,
                intent.action,
                intent.component?.flattenToShortString(),
                intent.data,
                exception::class.java.simpleName,
            )
            when (exception) {
                is ActivityNotFoundException,
                is SecurityException -> Unit
                else -> Timber.w(exception, "Unexpected failure while launching battery optimization intent.")
            }
            false
        }
    }
}

/**
 * 这里按“厂商专用页 -> 标准 Android 电池页 -> 应用详情页”的顺序尝试，
 * 让荣耀/华为优先落到真正影响后台保活的启动管理页，其它设备继续走系统标准入口。
 */
internal fun createBatteryOptimizationIntents(
    manufacturer: String,
    brand: String,
    packageName: String,
): List<Intent> {
    return buildList {
        if (isHuaweiOrHonorFamily(manufacturer, brand)) {
            addAll(createHuaweiOrHonorBatteryIntents(packageName))
        }
        add(createBatteryOptimizationIntent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, packageName))
        add(createBatteryOptimizationIntent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        add(createBatteryOptimizationIntent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageName))
    }
}

private fun createHuaweiOrHonorBatteryIntents(packageName: String): List<Intent> = listOf(
    createBatteryOptimizationComponentIntent(
        packageName = "com.hihonor.systemmanager",
        className = "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
        targetPackageName = packageName,
    ),
    createBatteryOptimizationComponentIntent(
        packageName = "com.hihonor.systemmanager",
        className = "com.hihonor.systemmanager.optimize.process.ProtectActivity",
        targetPackageName = packageName,
    ),
    createBatteryOptimizationComponentIntent(
        packageName = "com.hihonor.systemmanager",
        className = "com.hihonor.systemmanager.appcontrol.activity.StartupAppControlActivity",
        targetPackageName = packageName,
    ),
    createBatteryOptimizationComponentIntent(
        packageName = "com.huawei.systemmanager",
        className = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
        targetPackageName = packageName,
    ),
    createBatteryOptimizationComponentIntent(
        packageName = "com.huawei.systemmanager",
        className = "com.huawei.systemmanager.optimize.process.ProtectActivity",
        targetPackageName = packageName,
    ),
    createBatteryOptimizationComponentIntent(
        packageName = "com.huawei.systemmanager",
        className = "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity",
        targetPackageName = packageName,
    ),
)

/**
 * 荣耀/华为系统页没有公开稳定的 deep link 协议，这里带上一组常见的包名参数做“尽量直达”：
 * 如果系统页识别这些 extras，就能直接落到当前应用详情；如果不识别，也会退化成打开入口页。
 */
private fun createBatteryOptimizationComponentIntent(
    packageName: String,
    className: String,
    targetPackageName: String,
): Intent {
    return Intent()
        .setComponent(ComponentName(packageName, className))
        .setData(("package:" + targetPackageName).toUri())
        .putExtra(Settings.EXTRA_APP_PACKAGE, targetPackageName)
        .putExtra(Intent.EXTRA_PACKAGE_NAME, targetPackageName)
        .putExtra("packageName", targetPackageName)
        .putExtra("package_name", targetPackageName)
        .putExtra("pkg_name", targetPackageName)
        .putExtra("pkgName", targetPackageName)
        .putExtra("app_package_name", targetPackageName)
        .putExtra("appPackageName", targetPackageName)
}

private fun createBatteryOptimizationIntent(
    action: String,
    packageName: String? = null,
): Intent {
    val intent = Intent(action)
    if (packageName != null) {
        intent.data = ("package:" + packageName).toUri()
    }
    return intent
}

internal fun requiresProactiveBatteryOptimizationBanner(
    manufacturer: String,
    brand: String,
): Boolean {
    val normalizedManufacturer = manufacturer.lowercase(Locale.ROOT)
    val normalizedBrand = brand.lowercase(Locale.ROOT)
    return normalizedManufacturer.contains("xiaomi") ||
        normalizedBrand.contains("xiaomi") ||
        normalizedManufacturer.contains("redmi") ||
        normalizedBrand.contains("redmi")
}

internal fun isHuaweiOrHonorFamily(
    manufacturer: String,
    brand: String,
): Boolean {
    val normalizedManufacturer = manufacturer.lowercase(Locale.ROOT)
    val normalizedBrand = brand.lowercase(Locale.ROOT)
    return normalizedManufacturer.contains("huawei") ||
        normalizedBrand.contains("huawei") ||
        normalizedManufacturer.contains("honor") ||
        normalizedBrand.contains("honor")
}
