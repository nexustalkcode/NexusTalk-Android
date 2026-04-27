/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.battery

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.element.android.services.toolbox.api.intent.ExternalIntentLauncher
import io.element.android.services.toolbox.test.intent.FakeExternalIntentLauncher
import io.element.android.tests.testutils.lambda.lambdaRecorder
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AndroidBatteryOptimizationTest {
    @Test
    fun `requiresProactiveBatteryOptimizationBanner returns true for Xiaomi family`() {
        assertThat(requiresProactiveBatteryOptimizationBanner("Xiaomi", "2211133C")).isTrue()
        assertThat(requiresProactiveBatteryOptimizationBanner("Redmi", "note")).isTrue()
    }

    @Test
    fun `requiresProactiveBatteryOptimizationBanner returns false for other vendors`() {
        assertThat(requiresProactiveBatteryOptimizationBanner("Google", "Pixel")).isFalse()
    }

    @Test
    fun `isHuaweiOrHonorFamily returns true for Huawei and Honor`() {
        assertThat(isHuaweiOrHonorFamily("HUAWEI", "HUAWEI")).isTrue()
        assertThat(isHuaweiOrHonorFamily("HONOR", "HONOR")).isTrue()
    }

    @Test
    fun `isHuaweiOrHonorFamily returns false for other vendors`() {
        assertThat(isHuaweiOrHonorFamily("Google", "Pixel")).isFalse()
    }

    @Test
    fun `createBatteryOptimizationIntents uses Huawei specific candidates first on Honor devices`() {
        val intents = createBatteryOptimizationIntents(
            manufacturer = "HONOR",
            brand = "HONOR",
            packageName = "chat.haddpp.android.z",
        )
        assertThat(intents.first().component).isEqualTo(
            ComponentName(
                "com.hihonor.systemmanager",
                "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            )
        )
        assertThat(intents.first().data.toString()).isEqualTo("package:chat.haddpp.android.z")
        assertThat(intents.first().getStringExtra(Settings.EXTRA_APP_PACKAGE)).isEqualTo("chat.haddpp.android.z")
        assertThat(intents.first().getStringExtra(Intent.EXTRA_PACKAGE_NAME)).isEqualTo("chat.haddpp.android.z")
        assertThat(intents.first().getStringExtra("packageName")).isEqualTo("chat.haddpp.android.z")
        assertThat(intents[6].action).isEqualTo(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        assertThat(intents[6].data.toString()).isEqualTo("package:chat.haddpp.android.z")
    }

    @Test
    fun `createBatteryOptimizationIntents uses standard android candidates on non Huawei devices`() {
        val intents = createBatteryOptimizationIntents(
            manufacturer = "Google",
            brand = "Pixel",
            packageName = "chat.haddpp.android.z",
        )
        assertThat(intents).hasSize(3)
        assertThat(intents.first().action).isEqualTo(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        assertThat(intents.first().data.toString()).isEqualTo("package:chat.haddpp.android.z")
    }

    @Test
    fun `isIgnoringBatteryOptimizations should return false`() {
        val sut = createAndroidBatteryOptimization()
        assertThat(sut.isIgnoringBatteryOptimizations()).isFalse()
    }

    @Test
    fun `requestDisablingBatteryOptimization uses standard android intent on non Huawei devices`() {
        val launchLambda = lambdaRecorder<Intent, Unit> { intent ->
            assertThat(intent.action).isEqualTo(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            assertThat(intent.data.toString()).isEqualTo("package:${InstrumentationRegistry.getInstrumentation().context.packageName}")
        }
        val externalIntentLauncher = FakeExternalIntentLauncher(launchLambda)
        val sut = createAndroidBatteryOptimization(
            externalIntentLauncher = externalIntentLauncher,
        )
        val result = sut.requestDisablingBatteryOptimization()
        launchLambda.assertions().isCalledOnce()
        assertThat(result).isTrue()
    }

    @Test
    fun `createBatteryOptimizationIntents includes Huawei and Honor launch management chain before android fallback`() {
        val intents = createBatteryOptimizationIntents(
            manufacturer = "HONOR",
            brand = "HONOR",
            packageName = "chat.haddpp.android.z",
        )
        assertThat(intents.map { it.component?.flattenToShortString() }).containsAtLeast(
            "com.hihonor.systemmanager/com.hihonor.systemmanager.appcontrol.activity.StartupAppControlActivity",
            "com.hihonor.systemmanager/com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            "com.hihonor.systemmanager/com.hihonor.systemmanager.optimize.process.ProtectActivity",
            "com.huawei.systemmanager/com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity",
            "com.huawei.systemmanager/com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            "com.huawei.systemmanager/com.huawei.systemmanager.optimize.process.ProtectActivity",
        )
        assertThat(intents.takeLast(3).map { it.action }).containsExactly(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        ).inOrder()
    }

    private fun createAndroidBatteryOptimization(
        externalIntentLauncher: ExternalIntentLauncher = FakeExternalIntentLauncher(),
    ): AndroidBatteryOptimization {
        return AndroidBatteryOptimization(
            context = InstrumentationRegistry.getInstrumentation().context,
            externalIntentLauncher = externalIntentLauncher,
        )
    }
}
