/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.androidutils.browser

import android.util.Log
import android.webkit.ConsoleMessage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import timber.log.Timber

/**
 * WebView ConsoleMessage 日志接口。
 */
interface ConsoleMessageLogger {
    /**
     * 记录一条控制台日志。
     */
    fun log(
        tag: String,
        consoleMessage: ConsoleMessage,
    )
}

@ContributesBinding(AppScope::class)
/**
 * 默认的 WebView ConsoleMessage 日志实现。
 */
class DefaultConsoleMessageLogger : ConsoleMessageLogger {
    /**
     * 记录并脱敏一条控制台日志。
     */
    override fun log(
        tag: String,
        consoleMessage: ConsoleMessage,
    ) {
        val priority = when (consoleMessage.messageLevel()) {
            ConsoleMessage.MessageLevel.ERROR -> Log.ERROR
            ConsoleMessage.MessageLevel.WARNING -> Log.WARN
            else -> Log.DEBUG
        }

        val message = buildString {
            append(consoleMessage.sourceId())
            append(":")
            append(consoleMessage.lineNumber())
            append(" ")
            append(consoleMessage.message())
        }.redactSensitiveConsoleData()

        Timber.tag(tag).log(priority = priority, message = message)
    }
}

private val jsonSensitiveFieldRegex = Regex("""(?i)("(?:access_token|jwt|password|posthogApiKey|sentryDsn)"\s*:\s*")[^"]*(")""")
private val querySensitiveFieldRegex = Regex("""(?i)([?&](?:access_token|jwt|password|posthogApiKey|sentryDsn)=)[^&\s"'}]+""")
private val labeledSensitiveFieldRegex = Regex("""(?i)\b((?:access_token|jwt|password|posthogApiKey|sentryDsn):\s+)[^\s,"'}]+""")

/**
 * 对控制台日志里的敏感字段做脱敏。
 */
private fun String.redactSensitiveConsoleData(): String {
    var redacted = this
    redacted = jsonSensitiveFieldRegex.replace(redacted) { match ->
        "${match.groupValues[1]}<redacted>${match.groupValues[2]}"
    }
    redacted = querySensitiveFieldRegex.replace(redacted) { match ->
        "${match.groupValues[1]}<redacted>"
    }
    redacted = labeledSensitiveFieldRegex.replace(redacted) { match ->
        "${match.groupValues[1]}<redacted>"
    }
    return redacted
}
