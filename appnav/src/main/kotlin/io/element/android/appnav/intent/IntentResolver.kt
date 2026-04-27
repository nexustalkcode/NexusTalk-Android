/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav.intent

import android.content.Intent
import dev.zacsweers.metro.Inject
import io.element.android.features.login.api.LoginIntentResolver
import io.element.android.features.login.api.LoginParams
import io.element.android.libraries.deeplink.api.DeeplinkData
import io.element.android.libraries.deeplink.api.DeeplinkParser
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.oidc.api.OidcAction
import io.element.android.libraries.oidc.api.OidcIntentResolver
import timber.log.Timber

private const val permalinkDebugTag = "PermalinkDebug"

/**
 * App 启动或恢复时解析出的 Intent 目标。
 */
sealed interface ResolvedIntent {
    data class Navigation(val deeplinkData: DeeplinkData) : ResolvedIntent
    data class Oidc(val oidcAction: OidcAction) : ResolvedIntent
    data class Permalink(val permalinkData: PermalinkData) : ResolvedIntent
    data class Login(val params: LoginParams) : ResolvedIntent
    data class IncomingShare(val intent: Intent) : ResolvedIntent
}

@Inject
/**
 * 应用入口 Intent 解析器。
 *
 * 负责识别内部 deeplink、OIDC 回调、permalink、登录配置链接和系统分享。
 */
class IntentResolver(
    private val deeplinkParser: DeeplinkParser,
    private val loginIntentResolver: LoginIntentResolver,
    private val oidcIntentResolver: OidcIntentResolver,
    private val permalinkParser: PermalinkParser,
) {
    /**
     * 将系统 Intent 解析为应用内部可处理的目标。
     */
    fun resolve(intent: Intent): ResolvedIntent? {
        // 这组日志用于串起 H5 唤起后的第一跳，先确认系统到底把什么 Intent 交给了应用。
        Timber.tag(permalinkDebugTag).i(
            "IntentResolver.resolve action=%s data=%s categories=%s",
            intent.action,
            intent.dataString,
            intent.categories,
        )
        if (intent.canBeIgnored()) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve ignored launcher intent")
            return null
        }

        // Coming from a notification?
        val deepLinkData = deeplinkParser.getFromIntent(intent)
        if (deepLinkData != null) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve matched internal deeplink=%s", deepLinkData)
            return ResolvedIntent.Navigation(deepLinkData)
        }

        // Coming during login using Oidc?
        val oidcAction = oidcIntentResolver.resolve(intent)
        if (oidcAction != null) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve matched oidcAction=%s", oidcAction)
            return ResolvedIntent.Oidc(oidcAction)
        }

        val actionViewData = intent
            .takeIf { it.action == Intent.ACTION_VIEW }
            ?.dataString
        Timber.tag(permalinkDebugTag).i("IntentResolver.resolve actionViewData=%s", actionViewData)

        // Mobile configuration link clicked? (mobile.element.io)
        val mobileLoginData = actionViewData
            ?.let { loginIntentResolver.parse(it) }
        if (mobileLoginData != null) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve matched mobileLoginData=%s", mobileLoginData)
            return ResolvedIntent.Login(mobileLoginData)
        }

        // External link clicked? (matrix.to, element.io, etc.)
        val parsedPermalinkData = actionViewData
            ?.let { permalinkParser.parse(it) }
        Timber.tag(permalinkDebugTag).i("IntentResolver.resolve parsedPermalinkData=%s", parsedPermalinkData)
        if (parsedPermalinkData is PermalinkData.FallbackLink) {
            Timber.tag(permalinkDebugTag).w("IntentResolver.resolve permalink fallback uri=%s", parsedPermalinkData.uri)
        }
        val permalinkData = parsedPermalinkData
            ?.takeIf { it !is PermalinkData.FallbackLink }
        if (permalinkData != null) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve matched permalink=%s", permalinkData)
            return ResolvedIntent.Permalink(permalinkData)
        }

        if (intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
            Timber.tag(permalinkDebugTag).i("IntentResolver.resolve matched incoming share")
            return ResolvedIntent.IncomingShare(intent)
        }

        // Unknown intent
        Timber.tag(permalinkDebugTag).w("IntentResolver.resolve unknown intent")
        return null
    }
}

/**
 * 判断是否是可忽略的桌面启动 Intent。
 */
private fun Intent.canBeIgnored(): Boolean {
    return action == Intent.ACTION_MAIN &&
        categories?.contains(Intent.CATEGORY_LAUNCHER) == true
}
