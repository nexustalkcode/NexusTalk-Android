package io.element.android.libraries.designsystem.components.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.ext.angledGradient
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.IconSource

/**
 * 渐变按钮组件
 *
 * @param text 按钮文字
 * @param onClick 点击事件
 * @param modifier 修饰符
 * @param size 按钮尺寸，遵循 [ButtonSize]
 * @param enabled 是否启用
 * @param cornerRadius 按钮圆角
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    showProgress: Boolean = false,
    leadingIcon: IconSource? = null,
    cornerRadius: Dp = 50.dp,
) {
    val alpha = if (enabled) 1f else 0.5f
    val hasStartDrawable = showProgress || leadingIcon != null
    val contentPadding = size.toGradientContentPadding(hasStartDrawable = hasStartDrawable)

    Row(
        modifier = modifier
            .heightIn(min = size.toMinHeight())
            .clip(RoundedCornerShape(cornerRadius))
            .angledGradient(
                colorStops = arrayOf(
                    0f to ElementTheme.colors.gradientBrandStop1.copy(alpha = alpha),
                    1f to ElementTheme.colors.gradientBrandStop2.copy(alpha = alpha)
                ), degrees = 89f
            )
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = {
                        if (showProgress.not()) {
                            onClick.invoke()
                        }
                    })
                } else Modifier
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            showProgress -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .progressSemantics()
                        .size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            leadingIcon != null -> {
                androidx.compose.material.Icon(
                    painter = leadingIcon.getPainter(),
                    contentDescription = leadingIcon.contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Text(
            text = text,
            color = Color.White,
            style = ElementTheme.typography.fontBodyLgMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun ButtonSize.toMinHeight() = when (this) {
    ButtonSize.Small -> 32.dp
    ButtonSize.Medium,
    ButtonSize.MediumLowPadding -> 40.dp
    ButtonSize.Large,
    ButtonSize.LargeLowPadding -> 48.dp
}

private fun ButtonSize.toGradientContentPadding(hasStartDrawable: Boolean): PaddingValues = when (this) {
    ButtonSize.Small -> {
        if (hasStartDrawable) {
            PaddingValues(start = 8.dp, top = 5.dp, end = 16.dp, bottom = 5.dp)
        } else {
            PaddingValues(start = 16.dp, top = 5.dp, end = 16.dp, bottom = 5.dp)
        }
    }
    ButtonSize.Medium -> {
        if (hasStartDrawable) {
            PaddingValues(start = 16.dp, top = 10.dp, end = 24.dp, bottom = 10.dp)
        } else {
            PaddingValues(start = 24.dp, top = 10.dp, end = 24.dp, bottom = 10.dp)
        }
    }
    ButtonSize.MediumLowPadding -> PaddingValues(horizontal = 4.dp, vertical = 10.dp)
    ButtonSize.Large -> {
        if (hasStartDrawable) {
            PaddingValues(start = 24.dp, top = 13.dp, end = 32.dp, bottom = 13.dp)
        } else {
            PaddingValues(start = 32.dp, top = 13.dp, end = 32.dp, bottom = 13.dp)
        }
    }
    ButtonSize.LargeLowPadding -> PaddingValues(horizontal = 4.dp, vertical = 13.dp)
}

/**
 * 渐变按钮预览
 */
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun GradientButtonPreview() {
    ElementPreview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Large 按钮
            GradientButton(
                text = "Large 按钮",
                onClick = {},
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth()
            )

            // Medium 按钮
            GradientButton(
                text = "Medium 按钮",
                onClick = {},
                size = ButtonSize.Medium,
                modifier = Modifier.fillMaxWidth(),
                showProgress = true
            )

            // Small 按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    text = "Small",
                    onClick = {},
                    size = ButtonSize.Small,
                    modifier = Modifier.weight(1f)
                )
                GradientButton(
                    text = "关注",
                    onClick = {},
                    size = ButtonSize.Small,
                    leadingIcon = IconSource.Vector(CompoundIcons.ShareAndroid()),
                    modifier = Modifier.weight(1f)
                )
            }

            // 自定义圆角按钮
            GradientButton(
                text = "自定义圆角 (12dp)",
                onClick = {},
                size = ButtonSize.Large,
                cornerRadius = 12.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 禁用状态
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "禁用状态：",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    text = "Large",
                    onClick = {},
                    size = ButtonSize.Large,
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                GradientButton(
                    text = "Medium",
                    onClick = {},
                    size = ButtonSize.Medium,
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

