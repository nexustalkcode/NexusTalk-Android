/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package config

object BuildTimeConfig {
    const val APPLICATION_ID = "chat.haddpp.android.z"
    const val APPLICATION_NAME = "NexusTalk"
    const val GOOGLE_APP_ID_RELEASE = "1:363522161055:android:2ba979a8d1ff892a9165aa"
    const val GOOGLE_APP_ID_DEBUG = "1:363522161055:android:2ba979a8d1ff892a9165aa"
    const val GOOGLE_APP_ID_NIGHTLY = "1:363522161055:android:2ba979a8d1ff892a9165aa"

    val METADATA_HOST_REVERSED: String? = null
    val URL_WEBSITE: String? = "https://nexustalk.space"
    val URL_LOGO: String? = "https://nexustalk.space/static/img/mobile-icon.png"
    val URL_COPYRIGHT: String? = "https://element.io/copyright"
    val URL_ACCEPTABLE_USE: String? = "https://nexustalk.space/item/user-agreement.html"
    val URL_PRIVACY: String? = "https://nexustalk.space/item/privacy.html"
    val URL_POLICY: String? = "https://nexustalk.space/item/privacy.html"


//    val URL_WEBSITE: String? = "https://element.io"
//    val URL_LOGO: String? = "https://element.io/mobile-icon.png"
//    val URL_COPYRIGHT: String? = "https://element.io/copyright"
//    val URL_ACCEPTABLE_USE: String? = "https://element.io/acceptable-use-policy-terms"
//    val URL_PRIVACY: String? = "https://element.io/privacy"
//    val URL_POLICY: String? = "https://element.io/privacy"

//    val URL_WEBSITE: String? = "https://schildi.chat"
//    val URL_LOGO: String? = "https://schildi.chat/img/icon-next.png"
//    val URL_COPYRIGHT: String? = "https://element.io/copyright"
//    val URL_ACCEPTABLE_USE: String? = "https://schildi.chat/next/privacy//"
//    val URL_PRIVACY: String? = "https://schildi.chat/next/privacy//"
//    val URL_POLICY: String? = "https://schildi.chat/next/privacy//"



    val SERVICES_MAPTILER_BASE_URL: String? = null
    val SERVICES_MAPTILER_APIKEY: String? = null
    val SERVICES_MAPTILER_LIGHT_MAPID: String? = null
    val SERVICES_MAPTILER_DARK_MAPID: String? = null
    val SERVICES_POSTHOG_HOST: String? = null
    val SERVICES_POSTHOG_APIKEY: String? = null
    val SERVICES_SENTRY_DSN: String? = null
    val SERVICES_SENTRY_DSN_RUST: String? = null
    val BUG_REPORT_URL: String? = null
    val BUG_REPORT_APP_NAME: String? = null

    const val PUSH_CONFIG_INCLUDE_FIREBASE = true
    const val PUSH_CONFIG_INCLUDE_UNIFIED_PUSH = true
}
