/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import android.net.Uri
import androidx.core.net.toUri
import dev.zacsweers.metro.Inject
import timber.log.Timber

private const val CALL_INTENT_LOG_TAG = "CallIntentDataParser"

/**
 * 通话意图数据解析器
 *
 * 负责解析外部通话链接，支持多种 URL 格式：
 * - https://call.element.io/...
 * - element://call?url=...
 * - io.element.call:?url=...
 *
 * 解析后会验证 URL 的有效性，并添加必要的自定义参数以确保在嵌入式 WebView 中正确显示。
 *
 * @see android.net.Uri Android URI 类
 */
@Inject
class CallIntentDataParser {
    /** 有效的 HTTP 方案 */
    private val validHttpSchemes = sequenceOf("https")
    /** 已知的 Element Call 主机域名 */
    private val knownHosts = sequenceOf(
        "call.element.io",
    )

    /**
     * 解析通话链接
     *
     * 将输入的链接解析为有效的 Element Call URL。
     * 支持多种链接格式，并进行有效性验证。
     *
     * @param data 输入的链接字符串
     * @return 解析后的 Element Call URL，如果无效则返回 null
     */
    fun parse(data: String?): String? {
        val parsedUrl = data?.toUri() ?: return null
        val scheme = parsedUrl.scheme
        Timber.tag(CALL_INTENT_LOG_TAG).i(
            "Parsing call intent data: scheme=%s host=%s hasQuery=%s hasFragment=%s",
            scheme,
            parsedUrl.host,
            !parsedUrl.query.isNullOrEmpty(),
            !parsedUrl.fragment.isNullOrEmpty(),
        )
        val resolvedUri = when {
            scheme in validHttpSchemes -> parsedUrl
            scheme == "element" && parsedUrl.host == "call" -> {
                parsedUrl.getUrlParameter()
            }
            scheme == "io.element.call" && parsedUrl.host == null -> {
                parsedUrl.getUrlParameter()
            }
            // This should never be possible, but we still need to take into account the possibility
            else -> {
                Timber.tag(CALL_INTENT_LOG_TAG).w(
                    "Rejecting call intent data because scheme/host is unsupported: scheme=%s host=%s",
                    scheme,
                    parsedUrl.host,
                )
                null
            }
        }
        if (resolvedUri == null) {
            Timber.tag(CALL_INTENT_LOG_TAG).w(
                "Rejecting call intent data because no embedded URL could be resolved"
            )
            return null
        }
        if (resolvedUri.host !in knownHosts) {
            Timber.tag(CALL_INTENT_LOG_TAG).w(
                "Rejecting call intent data because host is not trusted: host=%s",
                resolvedUri.host,
            )
            return null
        }
        return resolvedUri.withCustomParameters().also {
            /*
             * 这里只记录解析结果的结构信息，不输出完整 URL，
             * 方便排查游客入会分支时确认是外链路径命中且参数已被补齐。
             */
            Timber.tag(CALL_INTENT_LOG_TAG).i(
                "Accepted call intent data: host=%s forceAppPrompt=%s forceConfineToRoom=%s",
                resolvedUri.host,
                it.contains("$APP_PROMPT_PARAMETER=false"),
                it.contains("$CONFINE_TO_ROOM_PARAMETER=true"),
            )
        }
    }

    /**
     * 从 URL 参数中获取实际 URL
     *
     * 处理 element:// 和 io.element.call:// 等自定义协议的链接，
     * 从中提取实际的 https URL。
     *
     * @return 提取出的 URL URI，如果不符合要求则返回 null
     */
    private fun Uri.getUrlParameter(): Uri? {
        return getQueryParameter("url")
            ?.let { urlParameter ->
                urlParameter.toUri().takeIf { uri ->
                    uri.scheme in validHttpSchemes && !uri.host.isNullOrBlank()
                }
            }
    }
}

/**
 * 确保 URI 在片段（fragment）中包含以下参数和值：
 * - appPrompt=false
 * - confineToRoom=true
 * 以确保在嵌入式 WebView 中正确渲染。
 *
 * @return 添加了自定义参数的 URL 字符串
 */
private fun Uri.withCustomParameters(): String {
    val builder = buildUpon()
    // Remove the existing query parameters
    builder.clearQuery()
    queryParameterNames.forEach {
        if (it == APP_PROMPT_PARAMETER || it == CONFINE_TO_ROOM_PARAMETER) return@forEach
        builder.appendQueryParameter(it, getQueryParameter(it))
    }
    // Remove the existing fragment parameters, and build the new fragment
    val currentFragment = fragment ?: ""
    // Reset the current fragment
    builder.fragment("")
    val queryFragmentPosition = currentFragment.lastIndexOf("?")
    val newFragment = if (queryFragmentPosition == -1) {
        // No existing query, build it.
        "$currentFragment?$APP_PROMPT_PARAMETER=false&$CONFINE_TO_ROOM_PARAMETER=true"
    } else {
        buildString {
            append(currentFragment.substring(0, queryFragmentPosition + 1))
            val queryFragment = currentFragment.substring(queryFragmentPosition + 1)
            // Replace the existing parameters
            val newQueryFragment = queryFragment
                .replace("$APP_PROMPT_PARAMETER=true", "$APP_PROMPT_PARAMETER=false")
                .replace("$CONFINE_TO_ROOM_PARAMETER=false", "$CONFINE_TO_ROOM_PARAMETER=true")
            append(newQueryFragment)
            // Ensure the parameters are there
            if (!newQueryFragment.contains("$APP_PROMPT_PARAMETER=false")) {
                if (newQueryFragment.isNotEmpty()) {
                    append("&")
                }
                append("$APP_PROMPT_PARAMETER=false")
            }
            if (!newQueryFragment.contains("$CONFINE_TO_ROOM_PARAMETER=true")) {
                append("&$CONFINE_TO_ROOM_PARAMETER=true")
            }
        }
    }
    // We do not want to encode the Fragment part, so append it manually
    return builder.build().toString() + "#" + newFragment
}

private const val APP_PROMPT_PARAMETER = "appPrompt"
private const val CONFINE_TO_ROOM_PARAMETER = "confineToRoom"
