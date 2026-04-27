/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.permalink

import android.net.Uri
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.replacePrefix
import io.element.android.libraries.matrix.api.permalink.MatrixToConverter
import timber.log.Timber

private const val permalinkDebugTag = "PermalinkDebug"

/**
 * Mapping of an input URI to a matrix.to compliant URI.
 */
@ContributesBinding(AppScope::class)
class DefaultMatrixToConverter : MatrixToConverter {
    /**
     * Try to convert a URL from an element web instance or from a client permalink to a matrix.to url.
     * To be successfully converted, URL path should contain one of the [SUPPORTED_PATHS].
     * Examples:
     * - https://riot.im/develop/#/room/#element-android:matrix.org  ->  https://matrix.to/#/#element-android:matrix.org
     * - https://app.element.io/#/room/#element-android:matrix.org   ->  https://matrix.to/#/#element-android:matrix.org
     * - https://www.example.org/#/room/#element-android:matrix.org  ->  https://matrix.to/#/#element-android:matrix.org
     * Also convert links coming from the matrix.to website:
     * - nexustalk://room/#element-android:matrix.org                ->  https://matrix.to/#/#element-android:matrix.org
     * - nexustalk://user/@alice:matrix.org                          ->  https://matrix.to/#/@alice:matrix.org
     */
    override fun convert(uri: Uri): Uri? {
        // 这里记录原始 URI 的拆解结果，方便判断 H5 传来的 scheme/host/path/query 是否和应用预期一致。
        Timber.tag(permalinkDebugTag).i(
            "MatrixToConverter.convert input=%s scheme=%s host=%s path=%s encodedPath=%s query=%s fragment=%s",
            uri,
            uri.scheme,
            uri.host,
            uri.path,
            uri.encodedPath,
            uri.query,
            uri.fragment,
        )
        val uriString = uri.toString()
            // Handle links coming from the matrix.to website.
            .replacePrefix(MATRIX_TO_CUSTOM_SCHEME_BASE_URL, "https://app.element.io/#/")
        // 这里必须固定归一化到 matrix.to 规范 permalink，而不是品牌站点的分享基址。
        // 否则后续 parseMatrixEntityFrom() 无法稳定识别 room/user 链接，最终会退化成 FallbackLink。
        val canonicalBaseUrl = MATRIX_TO_CANONICAL_BASE_URL
        Timber.tag(permalinkDebugTag).i("MatrixToConverter.convert normalizedUriString=%s canonicalBaseUrl=%s", uriString, canonicalBaseUrl)

        return when {
            // URL is already a matrix.to
            uriString.startsWith(canonicalBaseUrl) -> uri.also {
                Timber.tag(permalinkDebugTag).i("MatrixToConverter.convert alreadyNormalized=%s", it)
            }
            // Web or client url
            SUPPORTED_PATHS.any { it in uriString } -> {
                val path = SUPPORTED_PATHS.first { it in uriString }
                (canonicalBaseUrl + uriString.substringAfter(path)).toUri().also {
                    Timber.tag(permalinkDebugTag).i("MatrixToConverter.convert matchedPath=%s output=%s", path, it)
                }
            }
            // URL is not supported
            else -> null.also {
                Timber.tag(permalinkDebugTag).w("MatrixToConverter.convert unsupported input=%s", uri)
            }
        }
    }

    companion object {
        private const val MATRIX_TO_CUSTOM_SCHEME_BASE_URL = "nexustalk://"
        private const val MATRIX_TO_CANONICAL_BASE_URL = "https://matrix.to/#/"
        private val SUPPORTED_PATHS = listOf(
            "/#/room/",
            "/#/user/",
            "/#/group/"
        )
    }
}
