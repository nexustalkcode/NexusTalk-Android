/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.api.badge

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.element.android.libraries.designsystem.utils.CommonDrawables
import io.element.android.libraries.push.impl.R
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Global badge manager used to keep the launcher badge count in sync with the app unread count.
 *
 * Xiaomi devices require a real notification update for badges to refresh, so this singleton keeps
 * a dedicated silent notification for that compatibility path.
 */
object BadgeManager {
    private const val BADGE_BACKGROUND_REFRESH_DELAY_MILLIS = 500L
    private const val BADGE_NOTIFICATION_CHANNEL_ID = "BADGE_NOTIFICATION_CHANNEL_ID_V1"
    private const val XIAOMI_BADGE_NOTIFICATION_CHANNEL_ID = "XIAOMI_BADGE_NOTIFICATION_CHANNEL_ID_V1"
    private const val BADGE_NOTIFICATION_ID = 990_001
    private const val XIAOMI_BADGE_NOTIFICATION_ID = 990_002

    private val lastKnownBadgeCount = AtomicInteger(0)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingBackgroundRefresh: Runnable? = null

    enum class PhoneBrand {
        HUAWEI,
        HONOR,
        XIAOMI,
        OPPO,
        VIVO,
        SAMSUNG,
        SONY,
        LG,
        HTC,
        MEIZU,
        ONEPLUS,
        REALME,
        UNKNOWN,
    }

    fun setBadgeCount(context: Context, count: Int) {
        if (count < 0) return
        lastKnownBadgeCount.set(count)
        if (count == 0) {
            clearBadge(context)
            return
        }

        when (getPhoneBrand()) {
            PhoneBrand.HUAWEI -> setHuaweiBadge(context, count)
            PhoneBrand.HONOR -> setHonorBadge(context, count)
            PhoneBrand.XIAOMI -> setXiaomiBadge(context, count)
            PhoneBrand.OPPO -> setOppoBadge(context, count)
            PhoneBrand.VIVO -> setVivoBadge(context, count)
            PhoneBrand.SAMSUNG -> setSamsungBadge(context, count)
            PhoneBrand.SONY -> setSonyBadge(context, count)
            PhoneBrand.MEIZU -> setMeizuBadge(context, count)
            PhoneBrand.ONEPLUS -> setOnePlusBadge(context, count)
            PhoneBrand.REALME -> setRealmeBadge(context, count)
            PhoneBrand.LG,
            PhoneBrand.HTC,
            PhoneBrand.UNKNOWN -> setDefaultBadge(context, count)
        }
    }

    fun clearBadge(context: Context) {
        lastKnownBadgeCount.set(0)
        when (getPhoneBrand()) {
            PhoneBrand.HUAWEI -> setHuaweiBadge(context, 0)
            PhoneBrand.HONOR -> setHonorBadge(context, 0)
            PhoneBrand.XIAOMI -> setXiaomiBadge(context, 0)
            PhoneBrand.OPPO -> setOppoBadge(context, 0)
            PhoneBrand.VIVO -> setVivoBadge(context, 0)
            PhoneBrand.SAMSUNG -> setSamsungBadge(context, 0)
            PhoneBrand.SONY -> setSonyBadge(context, 0)
            PhoneBrand.MEIZU -> setMeizuBadge(context, 0)
            PhoneBrand.ONEPLUS -> setOnePlusBadge(context, 0)
            PhoneBrand.REALME -> setRealmeBadge(context, 0)
            PhoneBrand.LG,
            PhoneBrand.HTC,
            PhoneBrand.UNKNOWN -> setDefaultBadge(context, 0)
        }
    }

    fun scheduleRefreshFromLastKnownCount(context: Context, delayMillis: Long = BADGE_BACKGROUND_REFRESH_DELAY_MILLIS) {
        val applicationContext = context.applicationContext
        pendingBackgroundRefresh?.let(mainHandler::removeCallbacks)
        pendingBackgroundRefresh = Runnable {
            setBadgeCount(applicationContext, lastKnownBadgeCount.get())
        }.also { refreshRunnable ->
            mainHandler.postDelayed(refreshRunnable, delayMillis)
        }
    }

    fun isBadgeSupported(): Boolean {
        return when (getPhoneBrand()) {
            PhoneBrand.HUAWEI,
            PhoneBrand.HONOR,
            PhoneBrand.XIAOMI,
            PhoneBrand.OPPO,
            PhoneBrand.VIVO,
            PhoneBrand.SAMSUNG,
            PhoneBrand.SONY,
            PhoneBrand.MEIZU,
            PhoneBrand.ONEPLUS,
            PhoneBrand.REALME -> true
            PhoneBrand.LG,
            PhoneBrand.HTC,
            PhoneBrand.UNKNOWN -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }
    }

    fun getPhoneBrandName(): String {
        return when (getPhoneBrand()) {
            PhoneBrand.HUAWEI -> "HUAWEI"
            PhoneBrand.HONOR -> "HONOR"
            PhoneBrand.XIAOMI -> "XIAOMI"
            PhoneBrand.OPPO -> "OPPO"
            PhoneBrand.VIVO -> "VIVO"
            PhoneBrand.SAMSUNG -> "SAMSUNG"
            PhoneBrand.SONY -> "SONY"
            PhoneBrand.LG -> "LG"
            PhoneBrand.HTC -> "HTC"
            PhoneBrand.MEIZU -> "MEIZU"
            PhoneBrand.ONEPLUS -> "ONEPLUS"
            PhoneBrand.REALME -> "REALME"
            PhoneBrand.UNKNOWN -> Build.MANUFACTURER
        }
    }

    private fun getPhoneBrand(): PhoneBrand {
        return resolvePhoneBrand(
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
        )
    }

    internal fun resolvePhoneBrand(manufacturer: String, brand: String): PhoneBrand {
        val normalizedManufacturer = manufacturer.lowercase(Locale.ROOT)
        val normalizedBrand = brand.lowercase(Locale.ROOT)

        return when {
            normalizedManufacturer.contains("honor") || normalizedBrand.contains("honor") -> PhoneBrand.HONOR
            normalizedManufacturer.contains("huawei") || normalizedBrand.contains("huawei") -> PhoneBrand.HUAWEI
            normalizedManufacturer.contains("xiaomi") || normalizedBrand.contains("xiaomi") ||
                normalizedManufacturer.contains("redmi") || normalizedBrand.contains("redmi") -> PhoneBrand.XIAOMI
            normalizedManufacturer.contains("oppo") || normalizedBrand.contains("oppo") -> PhoneBrand.OPPO
            normalizedManufacturer.contains("vivo") || normalizedBrand.contains("vivo") ||
                normalizedManufacturer.contains("iqoo") || normalizedBrand.contains("iqoo") -> PhoneBrand.VIVO
            normalizedManufacturer.contains("samsung") || normalizedBrand.contains("samsung") -> PhoneBrand.SAMSUNG
            normalizedManufacturer.contains("sony") || normalizedBrand.contains("sony") -> PhoneBrand.SONY
            normalizedManufacturer.contains("lg") || normalizedBrand.contains("lg") -> PhoneBrand.LG
            normalizedManufacturer.contains("htc") || normalizedBrand.contains("htc") -> PhoneBrand.HTC
            normalizedManufacturer.contains("meizu") || normalizedBrand.contains("meizu") -> PhoneBrand.MEIZU
            normalizedManufacturer.contains("oneplus") || normalizedBrand.contains("oneplus") -> PhoneBrand.ONEPLUS
            normalizedManufacturer.contains("realme") || normalizedBrand.contains("realme") -> PhoneBrand.REALME
            else -> PhoneBrand.UNKNOWN
        }
    }

    private fun setHuaweiBadge(context: Context, count: Int) {
        val launcherClassName = getLauncherClassName(context)
        val extras = android.os.Bundle().apply {
            putString("package", context.packageName)
            putString("class", launcherClassName)
            putInt("badgenumber", count)
        }

        runCatching {
            context.contentResolver.call(
                Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                "change_badge",
                null,
                extras
            )
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    private fun setHonorBadge(context: Context, count: Int) {
        runCatching {
            setHuaweiBadge(context, count)
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    @Suppress("PrivateApi")
    private fun setXiaomiBadge(context: Context, count: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(XIAOMI_BADGE_NOTIFICATION_ID)

        if (count <= 0) {
            return
        }

        if (!canPostNotifications(context)) {
            return
        }

        createBadgeNotificationChannel(
            context = context,
            channelId = XIAOMI_BADGE_NOTIFICATION_CHANNEL_ID,
            importance = NotificationManager.IMPORTANCE_DEFAULT,
        )

        val notification = NotificationCompat.Builder(context, XIAOMI_BADGE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(CommonDrawables.ic_notification)
            .setContentTitle(getApplicationLabel(context))
            .setContentText(getBadgeContentText(context, count))
            .setNumber(count)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setAutoCancel(false)
            .setSilent(true)
            .build()

        runCatching {
            val field = notification.javaClass.getDeclaredField("extraNotification")
            field.isAccessible = true
            val extraNotification = field.get(notification)
            val method = extraNotification.javaClass.getDeclaredMethod("setMessageCount", Int::class.javaPrimitiveType)
            method.invoke(extraNotification, count)
        }

        notificationManager.notify(XIAOMI_BADGE_NOTIFICATION_ID, notification)
    }

    private fun setOppoBadge(context: Context, count: Int) {
        val launcherClassName = getLauncherClassName(context)
        val intent = Intent("com.oppo.unsettablebadge").apply {
            putExtra("packageName", context.packageName)
            putExtra("className", launcherClassName)
            putExtra("badgeCount", count)
            putExtra("count", count)
            putExtra("upgradeNumber", count)
        }
        runCatching {
            context.sendBroadcast(intent)
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    private fun setVivoBadge(context: Context, count: Int) {
        val intent = Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM").apply {
            putExtra("packageName", context.packageName)
            putExtra("className", getLauncherClassName(context))
            putExtra("notificationNum", count)
        }
        runCatching {
            context.sendBroadcast(intent)
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    private fun setSamsungBadge(context: Context, count: Int) {
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE").apply {
            putExtra("badge_count", count)
            putExtra("badge_count_package_name", context.packageName)
            putExtra("badge_count_class_name", getLauncherClassName(context))
        }
        runCatching {
            context.sendBroadcast(intent)
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    private fun setSonyBadge(context: Context, count: Int) {
        val intent = Intent("com.sonyericsson.home.action.UPDATE_BADGE").apply {
            putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.packageName)
            putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context))
            putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count.toString())
            putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", count > 0)
        }
        runCatching {
            context.sendBroadcast(intent)
        }.getOrElse {
            setDefaultBadge(context, count)
        }
    }

    private fun setMeizuBadge(context: Context, count: Int) {
        setDefaultBadge(context, count)
    }

    private fun setOnePlusBadge(context: Context, count: Int) {
        setDefaultBadge(context, count)
    }

    private fun setRealmeBadge(context: Context, count: Int) {
        setOppoBadge(context, count)
    }

    private fun setDefaultBadge(context: Context, count: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = NotificationManagerCompat.from(context)
        if (count <= 0) {
            notificationManager.cancel(BADGE_NOTIFICATION_ID)
            return
        }
        if (!canPostNotifications(context)) {
            return
        }

        createBadgeNotificationChannel(
            context = context,
            channelId = BADGE_NOTIFICATION_CHANNEL_ID,
            importance = NotificationManager.IMPORTANCE_LOW,
        )

        val notification = NotificationCompat.Builder(context, BADGE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(CommonDrawables.ic_notification)
            .setContentTitle(getApplicationLabel(context))
            .setContentText(getBadgeContentText(context, count))
            .setNumber(count)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setAutoCancel(false)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(BADGE_NOTIFICATION_ID, notification)
    }

    private fun createBadgeNotificationChannel(
        context: Context,
        channelId: String,
        importance: Int,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val existingChannel = notificationManager.getNotificationChannel(channelId)
        if (existingChannel != null) return

        val channel = NotificationChannel(
            channelId,
            getApplicationLabel(context),
            importance,
        ).apply {
            description = getApplicationLabel(context)
            setShowBadge(true)
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun getLauncherClassName(context: Context): String {
        return context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.component
            ?.className
            ?: context.packageName
    }

    private fun getApplicationLabel(context: Context): String {
        val applicationInfo = context.applicationInfo
        return context.packageManager.getApplicationLabel(applicationInfo).toString()
    }

    internal fun getBadgeContentText(context: Context, count: Int): String {
        // 使用静态 R 引用让资源成为编译期依赖，避免 getIdentifier 在资源裁剪、包名差异
        // 或模块合并场景下返回 0 后退化成纯数字。
        return context.resources.getQuantityString(R.plurals.notification_unread_notified_messages, count, count)
    }

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
}
