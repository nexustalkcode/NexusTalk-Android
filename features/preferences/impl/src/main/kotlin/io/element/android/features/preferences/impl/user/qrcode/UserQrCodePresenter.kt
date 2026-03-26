/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.qrcode

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 用户二维码页面 Presenter
 *
 * 负责提供用户二维码页面所需的数据。
 */
class UserQrCodePresenter @Inject constructor() : Presenter<UserQrCodeState> {
    private var navigator: UserQrCodeNavigator? = null
    private var matrixUser: MatrixUser? = null

    /**
     * 设置数据
     *
     * @param matrixUser Matrix 用户信息
     * @param navigator 导航器
     */
    fun setData(matrixUser: MatrixUser, navigator: UserQrCodeNavigator) {
        this.matrixUser = matrixUser
        this.navigator = navigator
    }

    @Composable
    override fun present(): UserQrCodeState {
        val currentMatrixUser = matrixUser
        return UserQrCodeState(
            matrixUser = currentMatrixUser ?: throw IllegalStateException("MatrixUser is required"),
            eventSink = { event ->
                when (event) {
                    is UserQrCodeEvent.BackClick -> {
                        navigator?.close()
                    }
                    is UserQrCodeEvent.ShareQrCode -> {
                        // 由 UserQrCodeNode 处理
                    }
                }
            }
        )
    }
}

/**
 * 用户二维码页面导航器接口
 */
interface UserQrCodeNavigator {
    /** 关闭页面 */
    fun close()
}
