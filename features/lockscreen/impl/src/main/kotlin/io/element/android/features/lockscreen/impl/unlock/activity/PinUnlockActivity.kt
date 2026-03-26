/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.Inject
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.lockscreen.api.LockScreenLockState
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.features.lockscreen.impl.unlock.PinUnlockPresenter
import io.element.android.features.lockscreen.impl.unlock.PinUnlockView
import io.element.android.features.lockscreen.impl.unlock.di.PinUnlockBindings
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import kotlinx.coroutines.launch

/**
 * PIN 解锁 Activity
 *
 * 用于在应用外部进行 PIN 码解锁的 Activity。
 */
class PinUnlockActivity : AppCompatActivity() {
    internal companion object {
        /**
         * 创建启动 PIN 解锁 Activity 的意图
         *
         * @param context 上下文
         * @return 启动意图
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, PinUnlockActivity::class.java)
        }
    }

    /** PIN 解锁 Presenter */
    @Inject lateinit var presenter: PinUnlockPresenter
    /** 锁屏服务 */
    @Inject lateinit var lockScreenService: LockScreenService
    /** 应用偏好设置存储 */
    @Inject lateinit var appPreferencesStore: AppPreferencesStore
    /** 企业服务 */
    @Inject lateinit var enterpriseService: EnterpriseService
    /** 构建元数据 */
    @Inject lateinit var buildMeta: BuildMeta

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        bindings<PinUnlockBindings>().inject(this)
        setContent {
            val colors by remember {
                enterpriseService.semanticColorsFlow(sessionId = null)
            }.collectAsState(SemanticColorsLightDark.default)
            ElementThemeApp(
                appPreferencesStore = appPreferencesStore,
                compoundLight = colors.light,
                compoundDark = colors.dark,
                buildMeta = buildMeta,
            ) {
                val state = presenter.present()
                PinUnlockView(
                    state = state,
                    isInAppUnlock = false,
                )
            }
        }
        lifecycleScope.launch {
            lockScreenService.lockState.collect { state ->
                if (state == LockScreenLockState.Unlocked) {
                    finish()
                }
            }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
