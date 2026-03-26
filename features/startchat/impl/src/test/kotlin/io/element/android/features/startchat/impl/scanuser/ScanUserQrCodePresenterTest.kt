/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.permissions.api.aPermissionsState
import io.element.android.libraries.permissions.test.FakePermissionsPresenter
import io.element.android.libraries.permissions.test.FakePermissionsPresenterFactory
import io.element.android.tests.testutils.WarmUpRule
import io.element.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ScanUserQrCodePresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - try again restarts scanning after invalid qr code`() = runTest {
        val presenter = createScanUserQrCodePresenter()

        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.scanAction).isEqualTo(AsyncAction.Loading)

            initialState.eventSink(ScanUserQrCodeEvents.QrCodeScanned("invalid"))
            val failureState = awaitItem()
            assertThat(failureState.scanAction).isInstanceOf(AsyncAction.Failure::class.java)

            failureState.eventSink(ScanUserQrCodeEvents.TryAgain)
            val retryState = awaitItem()
            assertThat(retryState.scanAction).isEqualTo(AsyncAction.Loading)
        }
    }

    @Test
    fun `present - valid qr code returns success`() = runTest {
        val presenter = createScanUserQrCodePresenter()

        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(ScanUserQrCodeEvents.QrCodeScanned("@alice:sms.swfree.space"))

            val successState = awaitItem()
            assertThat(successState.scanAction).isEqualTo(AsyncAction.Success("@alice:sms.swfree.space"))
        }
    }
}

private fun createScanUserQrCodePresenter(
    permissionsPresenter: FakePermissionsPresenter = FakePermissionsPresenter(
        initialState = aPermissionsState(
            showDialog = false,
            permissionGranted = true,
        )
    ),
): ScanUserQrCodePresenter {
    return ScanUserQrCodePresenter(
        permissionsPresenterFactory = FakePermissionsPresenterFactory(permissionsPresenter),
    )
}
