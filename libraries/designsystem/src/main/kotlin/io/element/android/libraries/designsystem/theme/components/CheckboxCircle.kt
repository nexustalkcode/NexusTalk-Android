/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup

// Designs in https://www.figma.com/file/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?type=design&mode=design&t=qb99xBP5mwwCtGkN-1

private val CheckboxSize: Dp = 24.dp
private const val IndeterminateLineWidthFraction = 0.4f

@Composable
fun CheckboxCircle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hasError: Boolean = false,
    indeterminate: Boolean = false,
    colors: CheckboxColors = if (hasError) compoundErrorCheckBoxColors() else compoundCheckBoxColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var indeterminateState by remember { mutableStateOf(indeterminate) }
    val state = if (!checked && indeterminateState) ToggleableState.Indeterminate else ToggleableState(checked)
    val boxColor = when {
        !enabled && state == ToggleableState.Indeterminate -> colors.disabledIndeterminateBoxColor
        !enabled && state == ToggleableState.On -> colors.disabledCheckedBoxColor
        !enabled -> colors.disabledUncheckedBoxColor
        state == ToggleableState.On || state == ToggleableState.Indeterminate -> colors.checkedBoxColor
        else -> colors.uncheckedBoxColor
    }
    val uncheckedRingColor = when {
        !enabled -> colors.disabledUncheckedBorderColor
        else -> colors.uncheckedBorderColor
    }
    val checkmarkColor = when (state) {
        ToggleableState.On, ToggleableState.Indeterminate -> colors.checkedCheckmarkColor
        else -> colors.uncheckedCheckmarkColor
    }

    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = {
                indeterminateState = false
                onCheckedChange(!checked)
            },
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactionSource,
            indication = null,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggleModifier)
            .minimumInteractiveComponentSize()
            .then(
                Modifier
                    .size(CheckboxSize)
                    .clip(CircleShape)
                    .then(
                        if (state == ToggleableState.Off) {
                            Modifier
                                .background(Color.Transparent, CircleShape)
                                .border(BorderStroke(2.dp, uncheckedRingColor), CircleShape)
                        } else {
                            Modifier.background(boxColor, CircleShape)
                        }
                    )
            ),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            ToggleableState.On -> {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = checkmarkColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            ToggleableState.Indeterminate -> {
                IndeterminateLine(color = checkmarkColor)
            }
            ToggleableState.Off -> { }
        }
    }
}

@Composable
private fun IndeterminateLine(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size((CheckboxSize.value * IndeterminateLineWidthFraction).dp,1.dp)
            .background(color)
    )
}

@Composable
private fun compoundCheckBoxColors(): CheckboxColors {
    return CheckboxDefaults.colors(
        checkedColor = ElementTheme.colors.iconPrimary,
        uncheckedColor = ElementTheme.colors.borderInteractivePrimary,
        checkmarkColor = ElementTheme.materialColors.onPrimary,
        disabledUncheckedColor = ElementTheme.colors.borderDisabled,
        disabledCheckedColor = ElementTheme.colors.iconDisabled,
        disabledIndeterminateColor = ElementTheme.colors.iconDisabled,
    )
}

@Composable
private fun compoundErrorCheckBoxColors(): CheckboxColors {
    return CheckboxDefaults.colors(
        checkedColor = ElementTheme.materialColors.error,
        uncheckedColor = ElementTheme.materialColors.error,
        checkmarkColor = ElementTheme.materialColors.onPrimary,
        disabledUncheckedColor = ElementTheme.colors.borderDisabled,
        disabledCheckedColor = ElementTheme.colors.iconDisabled,
        disabledIndeterminateColor = ElementTheme.colors.iconDisabled,
    )
}

@Preview(group = PreviewGroup.Toggles)
@Composable
internal fun CheckboxCirclesPreview() = ElementThemedPreview(vertical = false) {
    Column {
        // Unchecked
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(onCheckedChange = {}, enabled = true, checked = false)
            CheckboxCircle(onCheckedChange = {}, enabled = false, checked = false)
        }
        // Checked
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(onCheckedChange = {}, enabled = true, checked = true)
            CheckboxCircle(onCheckedChange = {}, enabled = false, checked = true)
        }
        // Indeterminate
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(onCheckedChange = {}, enabled = true, checked = false, indeterminate = true)
            CheckboxCircle(onCheckedChange = {}, enabled = false, checked = false, indeterminate = true)
        }
        // Error
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(hasError = true, onCheckedChange = {}, checked = false)
            CheckboxCircle(hasError = true, onCheckedChange = {}, enabled = false, checked = false)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(hasError = true, onCheckedChange = {}, enabled = true, checked = true)
            CheckboxCircle(hasError = true, onCheckedChange = {}, enabled = false, checked = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CheckboxCircle(onCheckedChange = {}, enabled = true, checked = false, indeterminate = true, hasError = true)
            CheckboxCircle(onCheckedChange = {}, enabled = false, checked = false, indeterminate = true, hasError = true)
        }
    }
}
