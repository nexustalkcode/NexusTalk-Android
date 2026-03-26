/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup

/**
 * A hybrid component that combines RadioButton logic with Checkbox visual style.
 * - Logic: Single selection behavior (like RadioButton)
 * - Visual: Checkmark style (like CheckBox when selected)
 */
@Composable
fun RadioCheckbox(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = compoundRadioCheckboxColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    androidx.compose.material3.Checkbox(
        checked = selected,
        onCheckedChange = { onClick?.invoke() },
        modifier = modifier.minimumInteractiveComponentSize(),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
private fun compoundRadioCheckboxColors(): CheckboxColors {
    return CheckboxDefaults.colors(
        checkedColor = ElementTheme.colors.iconPrimary,
        uncheckedColor = ElementTheme.colors.borderInteractivePrimary,
        checkmarkColor = ElementTheme.materialColors.onPrimary,
        disabledUncheckedColor = ElementTheme.colors.borderDisabled,
        disabledCheckedColor = ElementTheme.colors.iconDisabled,
    )
}

@Preview(group = PreviewGroup.Toggles)
@Composable
internal fun RadioCheckboxPreview() = ElementThemedPreview(vertical = false) {
    var checked by remember { mutableStateOf(false) }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RadioCheckbox(selected = checked, enabled = true, onClick = { checked = !checked })
            RadioCheckbox(selected = checked, enabled = false, onClick = { checked = !checked })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RadioCheckbox(selected = !checked, enabled = true, onClick = { checked = !checked })
            RadioCheckbox(selected = !checked, enabled = false, onClick = { checked = !checked })
        }
    }
}
