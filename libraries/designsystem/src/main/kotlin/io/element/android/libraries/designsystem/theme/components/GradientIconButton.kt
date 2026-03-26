/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.ext.angledGradient
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup

@Composable
fun GradientIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit,
) {
    IconButton(
        modifier = modifier
            .clip(shape = shape)
            .angledGradient(
                colorStops = arrayOf(
                    0f to ElementTheme.colors.gradientBrandStop1,
                    1f to ElementTheme.colors.gradientBrandStop2
                ),
                degrees = 89f
            ),
        onClick = onClick, content = content
    )
}

@Preview(group = PreviewGroup.FABs)
@Composable
internal fun GradientIconButtonPreview() = ElementThemedPreview {
    Box(modifier = Modifier.padding(8.dp)) {
        GradientIconButton(onClick = {}) {
            Icon(imageVector = CompoundIcons.Close(), contentDescription = null)
        }
    }
}
