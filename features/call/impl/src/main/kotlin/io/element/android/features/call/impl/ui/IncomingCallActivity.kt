/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.content.ContentResolver
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.Inject
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.call.impl.di.CallBindings
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.call.impl.utils.CallState
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class IncomingCallActivity : AppCompatActivity() {
    companion object {
        private val incomingCallVibrationPattern = longArrayOf(0L, 1_000L, 1_000L)
        const val EXTRA_NOTIFICATION_DATA = "EXTRA_NOTIFICATION_DATA"
    }

    @Inject
    lateinit var elementCallEntryPoint: ElementCallEntryPoint

    @Inject
    lateinit var activeCallManager: ActiveCallManager

    @Inject
    lateinit var appPreferencesStore: AppPreferencesStore

    @Inject
    lateinit var enterpriseService: EnterpriseService

    @Inject
    lateinit var buildMeta: BuildMeta

    @AppCoroutineScope
    @Inject lateinit var appCoroutineScope: CoroutineScope

    private var incomingCallMediaPlayer: MediaPlayer? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindings<CallBindings>().inject(this)

        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val notificationData = intent?.let { IntentCompat.getParcelableExtra(it, EXTRA_NOTIFICATION_DATA, CallNotificationData::class.java) }
            ?: (activeCallManager.activeCall.value?.callState as? CallState.Ringing)?.notificationData
        if (notificationData != null) {
            setContent {
                val colors by remember {
                    enterpriseService.semanticColorsFlow(sessionId = notificationData.sessionId)
                }.collectAsState(SemanticColorsLightDark.default)
                ElementThemeApp(
                    appPreferencesStore = appPreferencesStore,
                    compoundLight = colors.light,
                    compoundDark = colors.dark,
                    buildMeta = buildMeta,
                ) {
                    IncomingCallScreen(
                        notificationData = notificationData,
                        onAnswer = ::onAnswer,
                        onCancel = ::onCancel,
                    )
                }
            }
        } else {
            finish()
            return
        }

        activeCallManager.activeCall
            .filter { it?.callState !is CallState.Ringing }
            .onEach { finish() }
            .launchIn(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(true)
        }
        startIncomingCallAlert()
    }

    override fun onStop() {
        stopIncomingCallAlert()
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(false)
        }
        super.onStop()
    }

    override fun onDestroy() {
        stopIncomingCallAlert()
        super.onDestroy()
    }

    private fun onAnswer(notificationData: CallNotificationData) {
        stopIncomingCallAlert()
        elementCallEntryPoint.startCall(CallType.RoomCall(notificationData.sessionId, notificationData.roomId))
    }

    private fun onCancel() {
        stopIncomingCallAlert()
        val activeCall = activeCallManager.activeCall.value ?: return
        appCoroutineScope.launch {
            activeCallManager.hangUpCall(callType = activeCall.callType)
        }
    }

    private fun startIncomingCallAlert() {
        startIncomingCallSound()
        startIncomingCallVibration()
    }

    private fun stopIncomingCallAlert() {
        incomingCallMediaPlayer?.run {
            runCatching { stop() }
            release()
        }
        incomingCallMediaPlayer = null
        vibrator?.cancel()
    }

    private fun startIncomingCallSound() {
        if (incomingCallMediaPlayer != null) return
        incomingCallMediaPlayer = runCatching {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                isLooping = true
                setDataSource(this@IncomingCallActivity, incomingCallSoundUri())
                prepare()
                start()
            }
        }.onFailure {
            Timber.e(it, "Failed to start incoming call sound")
        }.getOrNull()
    }

    private fun startIncomingCallVibration() {
        val vibrator = vibrator ?: return
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createWaveform(incomingCallVibrationPattern, 0))
    }

    private fun incomingCallSoundUri(): Uri {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName)
            .appendPath("raw")
            .appendPath("video_request")
            .build()
    }
}
