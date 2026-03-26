/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncData

/**
 * ViewFileState 预览参数提供者
 *
 * 提供 ViewFileState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 * 包含多种状态场景：未初始化、加载中、失败、成功等。
 *
 * @see ViewFileState 文件查看状态
 */
open class ViewFileStateProvider : PreviewParameterProvider<ViewFileState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 ViewFileState 序列
     */
    override val values: Sequence<ViewFileState>
        get() = sequenceOf(
            aViewFileState(),
            aViewFileState(lines = AsyncData.Loading()),
            aViewFileState(lines = AsyncData.Failure(Exception("A failure"))),
            aViewFileState(lines = AsyncData.Success(emptyList())),
            aViewFileState(
                name = "logcat.log",
                lines = AsyncData.Success(
                    listOf(
                        "Line 1",
                        "Line 2",
                        "Line 3 lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                            " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,",
                        "01-23 13:14:50.740 25818 25818 V verbose",
                        "01-23 13:14:50.740 25818 25818 D debug",
                        "01-23 13:14:50.740 25818 25818 I info",
                        "01-23 13:14:50.740 25818 25818 W warning",
                        "01-23 13:14:50.740 25818 25818 E error",
                        "01-23 13:14:50.740 25818 25818 A assertion",
                    )
                ),
                colorationMode = ColorationMode.Logcat,
            ),
            aViewFileState(
                name = "logs.2024-01-26",
                lines = AsyncData.Success(
                    listOf(
                        "Line 1",
                        "Line 2",
                        "Line 3 lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                            " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,",
                        "2024-01-26T10:22:26.947416Z TRACE trace",
                        "2024-01-26T10:22:26.947416Z DEBUG debug",
                        "2024-01-26T10:22:26.947416Z  INFO info",
                        "2024-01-26T10:22:26.947416Z  WARN warn",
                        "2024-01-26T10:22:26.947416Z ERROR error",
                    )
                ),
                colorationMode = ColorationMode.RustLogs,
            )
        )
}

/**
 * 创建示例文件状态
 *
 * @param name 文件名，默认为 "aName"
 * @param lines 文件内容异步数据，默认为未初始化
 * @param colorationMode 着色模式，默认为无
 * @return 示例 ViewFileState 实例
 */
fun aViewFileState(
    name: String = "aName",
    lines: AsyncData<List<String>> = AsyncData.Uninitialized,
    colorationMode: ColorationMode = ColorationMode.None,
) = ViewFileState(
    name = name,
    lines = lines,
    colorationMode = colorationMode,
    eventSink = {},
)
