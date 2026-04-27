/*
 * Copyright (c) 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.androidutils.diagnostics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface IncomingCallDiagnosticLogger {
    /**
     * 记录一条来电诊断事件。
     *
     * 这里刻意使用结构化字段，便于远程测试人员导出后判断事件到底进入了来电通知链路、
     * 普通消息通知链路，还是被系统/权限条件拦截。
     */
    suspend fun record(event: String, fields: Map<String, Any?> = emptyMap())

    /**
     * 通过系统分享面板导出来电诊断日志。
     *
     * 返回值只表示是否成功调起分享，不代表测试人员已经把文件发出。
     */
    suspend fun share(): Boolean
}

object NoOpIncomingCallDiagnosticLogger : IncomingCallDiagnosticLogger {
    override suspend fun record(event: String, fields: Map<String, Any?>) = Unit
    override suspend fun share(): Boolean = false
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultIncomingCallDiagnosticLogger(
    @ApplicationContext private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    private val buildMeta: BuildMeta,
) : IncomingCallDiagnosticLogger {
    private val lock = Any()
    private val lineTimestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US)
    private val fileTimestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    override suspend fun record(event: String, fields: Map<String, Any?>) {
        runCatchingExceptions {
            withContext(dispatchers.io) {
                synchronized(lock) {
                    val file = logFile()
                    ensureLogFileExistsLocked(file)
                    trimIfNeededLocked(file)
                    file.appendText(buildLine(event, fields), Charsets.UTF_8)
                }
            }
        }.onFailure {
            Timber.w(it, "Failed to write incoming call diagnostic event")
        }
    }

    override suspend fun share(): Boolean {
        return runCatchingExceptions {
            val exportFile = withContext(dispatchers.io) {
                synchronized(lock) {
                    val file = logFile()
                    ensureLogFileExistsLocked(file)
                    file.appendText(buildLine("diagnostic_log_export_requested"), Charsets.UTF_8)
                    val exportFile = File(exportDirectory(), "nexustalk-incoming-call-diagnostics-${fileTimestampFormat.format(Date())}.txt")
                    file.copyTo(exportFile, overwrite = true)
                }
            }
            withContext(dispatchers.main) {
                context.startActivity(createShareIntent(exportFile))
            }
        }.onFailure {
            Timber.e(it, "Failed to share incoming call diagnostic log")
        }.isSuccess
    }

    private fun logFile(): File {
        return File(logDirectory(), "incoming-call-diagnostics.log")
    }

    private fun logDirectory(): File {
        return File(context.cacheDir, "diagnostics").apply { mkdirs() }
    }

    private fun exportDirectory(): File {
        return File(context.cacheDir, "shared_diagnostics").apply { mkdirs() }
    }

    private fun ensureLogFileExistsLocked(file: File) {
        if (file.exists()) return
        file.parentFile?.mkdirs()
        file.writeText(buildHeader(), Charsets.UTF_8)
    }

    private fun trimIfNeededLocked(file: File) {
        if (file.length() <= maxLogFileBytes) return
        val text = file.readText(Charsets.UTF_8)
        file.writeText(
            buildString {
                append(buildHeader())
                append("# Older diagnostic lines were trimmed because the file exceeded ")
                append(maxLogFileBytes)
                append(" bytes.\n")
                append(text.takeLast(maxRetainedLogChars))
            },
            Charsets.UTF_8,
        )
    }

    private fun buildHeader(): String {
        return buildString {
            append("# NexusTalk incoming call diagnostics\n")
            append("# createdAt=\"")
            append(lineTimestampFormat.format(Date()))
            append("\"\n")
            append("# appId=\"")
            append(buildMeta.applicationId)
            append("\" buildType=\"")
            append(buildMeta.buildType)
            append("\" flavor=\"")
            append(buildMeta.flavorDescription)
            append("\"\n")
            append("# deviceManufacturer=\"")
            append(Build.MANUFACTURER)
            append("\" deviceModel=\"")
            append(Build.MODEL)
            append("\" androidRelease=\"")
            append(Build.VERSION.RELEASE)
            append("\" sdkInt=\"")
            append(Build.VERSION.SDK_INT)
            append("\"\n")
            append("# Note: this file records notification routing and Matrix ids only; message bodies are not written.\n")
        }
    }

    private fun buildLine(
        event: String,
        fields: Map<String, Any?> = emptyMap(),
    ): String {
        return buildString {
            append(lineTimestampFormat.format(Date()))
            append(" event=\"")
            append(event.escapeFieldValue())
            append('"')
            fields.forEach { (key, value) ->
                append(' ')
                append(key)
                append("=\"")
                append(value.safeFieldValue())
                append('"')
            }
            append('\n')
        }
    }

    private fun createShareIntent(file: File): Intent {
        val uri = file.toShareableUri()
        val shareIntent = Intent(Intent.ACTION_SEND)
            .setTypeAndNormalize(MimeTypes.PlainText)
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .putExtra(Intent.EXTRA_STREAM, uri)
            .putExtra(Intent.EXTRA_SUBJECT, "NexusTalk incoming call diagnostics")
        return Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun File.toShareableUri(): Uri {
        val authority = "${buildMeta.applicationId}.fileprovider"
        return FileProvider.getUriForFile(context, authority, this).normalizeScheme()
    }

    private fun Any?.safeFieldValue(): String {
        return when (this) {
            null -> "null"
            else -> toString().escapeFieldValue()
        }
    }

    private fun String.escapeFieldValue(): String {
        return replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
    }

    companion object {
        private const val maxLogFileBytes = 512 * 1024
        private const val maxRetainedLogChars = 256 * 1024
    }
}
