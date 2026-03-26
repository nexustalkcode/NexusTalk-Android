/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.element.android.libraries.mediaviewer.api.local.LocalMedia
import kotlinx.parcelize.Parcelize

/**
 * 附件密封接口
 *
 * 表示消息中的附件内容。使用 @Immutable 注解标记为不可变，
 * 实现 Parcelable 接口以支持跨组件传递。
 *
 * 附件类型：
 * - [Media]: 媒体附件，包含图片或视频等媒体文件
 *
 * @see Parcelable Android可序列化接口
 * @see LocalMedia 本地媒体
 */
@Immutable
sealed interface Attachment : Parcelable {
    /**
     * 媒体附件
     *
     * 表示图片、视频等媒体类型的附件
     *
     * @property localMedia 本地媒体数据，包含媒体文件的URI和元信息
     */
    @Parcelize
    data class Media(val localMedia: LocalMedia) : Attachment
}
