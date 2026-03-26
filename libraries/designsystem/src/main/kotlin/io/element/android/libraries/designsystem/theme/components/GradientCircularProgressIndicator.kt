/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup
import kotlin.math.min

@Composable
fun GradientCircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    trackColor: Color = ProgressIndicatorDefaults.circularDeterminateTrackColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val coercedProgress = progress().coerceIn(0f, 1f)
    GradientCircularProgressIndicatorCanvas(
        modifier = modifier.progressSemantics(coercedProgress),
        indicatorBrush = rememberBrandGradientSweepBrush(),
        startAngle = -90f,
        sweepAngle = coercedProgress * 360f,
        trackColor = trackColor,
        strokeWidth = strokeWidth,
        strokeCap = strokeCap,
    )
}

@Composable
fun GradientCircularProgressIndicator(
    modifier: Modifier = Modifier,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    if (LocalInspectionMode.current) {
        GradientCircularProgressIndicator(
            progress = { 0.75f },
            modifier = modifier,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap,
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "GradientCircularProgressIndicator")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_250, easing = LinearEasing)
        ),
        label = "GradientCircularProgressIndicatorRotation"
    )
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 35f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "GradientCircularProgressIndicatorSweep"
    )

    GradientCircularProgressIndicatorCanvas(
        modifier = modifier.progressSemantics(),
        indicatorBrush = rememberBrandGradientSweepBrush(),
        startAngle = rotation - 90f,
        sweepAngle = sweepAngle,
        trackColor = trackColor,
        strokeWidth = strokeWidth,
        strokeCap = strokeCap,
    )
}

@Composable
private fun rememberBrandGradientSweepBrush(): Brush {
    val brandStart = ElementTheme.colors.gradientBrandStop1
    val brandEnd = ElementTheme.colors.gradientBrandStop2
    return remember(brandStart, brandEnd) {
        Brush.sweepGradient(
            colors = listOf(brandStart, brandEnd, brandStart)
        )
    }
}

@Composable
private fun GradientCircularProgressIndicatorCanvas(
    modifier: Modifier,
    indicatorBrush: Brush,
    startAngle: Float,
    sweepAngle: Float,
    trackColor: Color,
    strokeWidth: Dp,
    strokeCap: StrokeCap,
) {
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val diameter = min(size.width, size.height)
        if (diameter <= strokeWidthPx) return@Canvas

        val arcTopLeft = Offset(
            x = (size.width - diameter) / 2f + strokeWidthPx / 2f,
            y = (size.height - diameter) / 2f + strokeWidthPx / 2f,
        )
        val arcSize = Size(
            width = diameter - strokeWidthPx,
            height = diameter - strokeWidthPx,
        )

        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt),
        )

        drawArc(
            brush = indicatorBrush,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidthPx, cap = strokeCap),
        )
    }
}

@Preview(group = PreviewGroup.Progress)
@Composable
internal fun GradientCircularProgressIndicatorPreview() = ElementThemedPreview(vertical = false) {
    Column(
        modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Indeterminate")
        GradientCircularProgressIndicator(modifier = Modifier.size(32.dp))

        Text("Fixed progress")
        GradientCircularProgressIndicator(
            progress = { 0.65f },
            modifier = Modifier.size(32.dp),
        )
    }
}
