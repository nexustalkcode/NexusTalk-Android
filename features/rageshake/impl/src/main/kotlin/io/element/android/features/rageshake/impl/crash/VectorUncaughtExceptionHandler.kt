/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.crash

import android.os.Build
import io.element.android.libraries.core.data.tryOrNull
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Vector 未捕获异常处理器
 *
 * 捕获应用中的未处理异常，将其保存以便后续报告，
 * 并调用系统默认的异常处理机制。
 *
 * @property preferencesCrashDataStore 崩溃数据存储
 */
class VectorUncaughtExceptionHandler(
    private val preferencesCrashDataStore: PreferencesCrashDataStore,
) : Thread.UncaughtExceptionHandler {
    /**
     * 之前的异常处理器
     *
     * 保存系统默认的异常处理器，以便在处理完后调用。
     */
    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * 激活异常处理器
     *
     * 将此处理器设置为默认的未捕获异常处理器。
     */
    fun activate() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 未捕获的异常处理
     *
     * 当发生未捕获的异常时，收集设备信息并保存崩溃数据，
     * 然后调用系统默认的异常处理器。
     *
     * @param thread 发生异常的线程
     * @param throwable 异常对象
     */
    @Suppress("PrintStackTrace")
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Timber.v("Uncaught exception: $throwable")
        val bugDescription = buildString {
            val appName = "ElementX"
            // append(appName + " Build : " + versionCodeProvider.getVersionCode() + "\n")
            append("$appName Version : 1.0") // ${versionProvider.getVersion(longFormat = true)}\n")
            // append("SDK Version : ${Matrix.getSdkVersion()}\n")
            append("Phone : " + Build.MODEL.trim() + " (" + Build.VERSION.INCREMENTAL + " " + Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + ")\n")
            append("Memory statuses \n")
            var freeSize = 0L
            var totalSize = 0L
            var usedSize = -1L
            tryOrNull {
                val info = Runtime.getRuntime()
                freeSize = info.freeMemory()
                totalSize = info.totalMemory()
                usedSize = totalSize - freeSize
            }
            append("usedSize   " + usedSize / 1_048_576L + " MB\n")
            append("freeSize   " + freeSize / 1_048_576L + " MB\n")
            append("totalSize   " + totalSize / 1_048_576L + " MB\n")
            append("Thread: ")
            append(thread.name)
            append(", Exception: ")
            val sw = StringWriter()
            val pw = PrintWriter(sw, true)
            throwable.printStackTrace(pw)
            append(sw.buffer.toString())
        }
        Timber.e("FATAL EXCEPTION $bugDescription")
        preferencesCrashDataStore.setCrashData(bugDescription)
        // Show the classical system popup
        previousHandler?.uncaughtException(thread, throwable)
    }
}
