/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View

/**
 * View 扩展函数：截取屏幕截图
 *
 * 对 View 进行截图，使用 PixelCopy API（在 Android O 及以上）
 * 或 Canvas 绘制（在较低版本）来实现。
 *
 * @param bitmapCallback 截图完成后的回调函数，传递 ImageResult 结果
 */
fun View.screenshot(bitmapCallback: (ImageResult) -> Unit) {
    try {
        val handler = Handler(Looper.getMainLooper())
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(
                (this.context as Activity).window,
                clipBounds,
                bitmap,
                {
                    when (it) {
                        PixelCopy.SUCCESS -> {
                            bitmapCallback.invoke(ImageResult.Success(bitmap))
                        }
                        else -> {
                            bitmapCallback.invoke(ImageResult.Error(Exception(it.toString())))
                        }
                    }
                },
                handler
            )
        } else {
            handler.post {
                val canvas = Canvas(bitmap)
                    .apply {
                        translate(-clipBounds.left.toFloat(), -clipBounds.top.toFloat())
                    }
                this.draw(canvas)
                canvas.setBitmap(null)
                bitmapCallback.invoke(ImageResult.Success(bitmap))
            }
        }
    } catch (e: Exception) {
        bitmapCallback.invoke(ImageResult.Error(e))
    }
}

/**
 * 截图结果密封接口
 *
 * 表示截图操作的可能结果，包含成功或失败状态。
 */
sealed interface ImageResult {
    /**
     * 截图失败
     *
     * 当截图操作失败时返回，包含异常信息。
     *
     * @param exception 失败原因异常
     */
    data class Error(val exception: Exception) : ImageResult

    /**
     * 截图成功
     *
     * 当截图操作成功时返回，包含位图数据。
     *
     * @param data 截取的位图数据
     */
    data class Success(val data: Bitmap) : ImageResult
}
