/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.event

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.media3.common.MimeTypes
import io.element.android.libraries.matrix.api.media.MediaSource
import io.element.android.libraries.matrix.ui.components.A_BLUR_HASH

/**
 * 为贴纸消息内容预览提供样例数据。
 */
open class TimelineItemStickerContentProvider : PreviewParameterProvider<TimelineItemStickerContent> {
    override val values: Sequence<TimelineItemStickerContent>
        get() = sequenceOf(
            aTimelineItemStickerContent(),
            aTimelineItemStickerContent(aspectRatio = 1.0f),
            aTimelineItemStickerContent(aspectRatio = 1.5f),
            aTimelineItemStickerContent(blurhash = null),
        )
}

/**
 * 构造一份贴纸消息内容样例。
 */
fun aTimelineItemStickerContent(
    aspectRatio: Float = 0.5f,
    blurhash: String? = A_BLUR_HASH,
) = TimelineItemStickerContent(
    filename = "a sticker.gif",
    fileSize = 4 * 1024 * 1024L,
    caption = "a body",
    formattedCaption = null,
    isEdited = false,
    mediaSource = MediaSource(""),
    thumbnailSource = null,
    mimeType = MimeTypes.IMAGE_JPEG,
    blurhash = blurhash,
    width = null,
    height = 128,
    aspectRatio = aspectRatio,
    formattedFileSize = "4MB",
    fileExtension = "jpg"
)
