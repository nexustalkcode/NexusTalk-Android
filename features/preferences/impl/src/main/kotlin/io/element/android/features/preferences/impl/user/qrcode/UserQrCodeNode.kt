/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.qrcode

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 用户二维码页面 Node
 *
 * 负责显示用户二维码页面，允许用户分享自己的二维码以添加好友。
 *
 * @property presenter 用户二维码 Presenter
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class UserQrCodeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: UserQrCodePresenter,
) : Node(buildContext, plugins = plugins),
    UserQrCodeNavigator {
    /**
     * 输入数据类
     */
    data class Inputs(
        val matrixUser: MatrixUser
    ) : NodeInputs

    /**
     * 页面回调接口
     */
    interface Callback : Plugin {
        /** 完成操作 */
        fun onDone()
    }

    private val inputs: Inputs = inputs()
    private val callback: Callback = callback()

    init {
        presenter.setData(inputs.matrixUser, this)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val context = LocalContext.current

        val state = presenter.present()
        UserQrCodeView(
            state = state,
            onBackClick = { close() },
            onShareQrCode = { bitmap ->
                shareQrCode(context = context, bitmap = bitmap)
            },
            modifier = modifier
        )
    }

    /**
     * 分享二维码图片
     *
     * @param context 上下文
     * @param bitmap 已捕获的分享卡片位图
     */
    private fun shareQrCode(
        context: Context,
        bitmap: Bitmap
    ) {
        val uri = saveBitmapToCacheAndShare(context, bitmap)
        if (uri != null) {
            shareImage(context, uri)
        }
    }

    override fun close() = callback.onDone()
}
