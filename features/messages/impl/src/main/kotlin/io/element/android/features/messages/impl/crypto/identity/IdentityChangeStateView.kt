/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.atomic.molecules.ComposerAlertLevel
import io.element.android.libraries.designsystem.atomic.molecules.ComposerAlertMolecule
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.api.encryption.identity.isAViolation
import io.element.android.libraries.matrix.ui.room.RoomMemberIdentityStateChange
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 身份变更状态视图
 *
 * 这是一个 Composable 函数，用于渲染身份变更警告提示。
 * 当房间内已验证成员的加密身份发生变更时，此组件会向用户显示相应的警告。
 *
 * 组件功能：
 * - 检测并显示 PIN 违规警告（用户密钥变更）
 * - 检测并显示验证违规警告（验证被撤销）
 * - 根据违规类型显示不同的提示信息和操作按钮
 * - 提供"了解更多"链接，引导用户到帮助文档
 *
 * 违规类型说明：
 * - [IdentityState.PinViolation]: 用户的加密密钥发生了变化，
 *   可能存在中间人攻击风险，需要用户确认是否信任新密钥
 * - [IdentityState.VerificationViolation]: 用户的验证状态被撤销，
 *   用户不再信任该成员，需要用户决定是否继续通信
 *
 * @param state 身份变更状态，包含成员身份状态变更列表和事件处理函数
 * @param onLinkClick 链接点击回调，用于处理"了解更多"链接的点击事件
 * @param modifier 可选的修饰符，用于自定义组件样式和布局
 *
 * @see IdentityChangeState 身份变更状态数据类
 * @see IdentityChangeEvent 身份变更事件
 */
@Composable
fun IdentityChangeStateView(
    state: IdentityChangeState,
    onLinkClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pick the first identity change that is a violation
    val identityChangeViolation = state.roomMemberIdentityStateChanges.firstOrNull {
        it.identityState.isAViolation()
    }
    when (identityChangeViolation?.identityState) {
        IdentityState.PinViolation -> ViolationAlert(
            identityChangeViolation = identityChangeViolation,
            onLinkClick = onLinkClick,
            textId = CommonStrings.crypto_identity_change_pin_violation_new,
            isCritical = false,
            submitTextId = CommonStrings.action_dismiss,
            onSubmitClick = { state.eventSink(IdentityChangeEvent.PinIdentity(identityChangeViolation.identityRoomMember.userId)) },
            modifier = modifier,
        )
        IdentityState.VerificationViolation -> ViolationAlert(
            identityChangeViolation = identityChangeViolation,
            onLinkClick = onLinkClick,
            textId = CommonStrings.crypto_identity_change_verification_violation_new,
            isCritical = true,
            submitTextId = CommonStrings.crypto_identity_change_withdraw_verification_action,
            onSubmitClick = { state.eventSink(IdentityChangeEvent.WithdrawVerification(identityChangeViolation.identityRoomMember.userId)) },
            modifier = modifier,
        )
        else -> Unit
    }
}

/**
 * 违规警告组件
 *
 * 这是一个私有 Composable 函数，用于渲染具体的身份违规警告。
 * 根据违规类型（PIN违规或验证违规）显示相应的警告内容和操作按钮。
 *
 * @param identityChangeViolation 身份变更违规信息
 * @param onLinkClick 链接点击回调
 * @param textId 警告文本资源ID
 * @param isCritical 是否为严重警告（验证违规为严重，PIN违规为普通）
 * @param submitTextId 提交按钮文本资源ID
 * @param onSubmitClick 提交按钮点击回调
 * @param modifier 可选的修饰符
 */
@Composable
private fun ViolationAlert(
    identityChangeViolation: RoomMemberIdentityStateChange,
    onLinkClick: (String, Boolean) -> Unit,
    @StringRes textId: Int,
    isCritical: Boolean,
    @StringRes submitTextId: Int,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ComposerAlertMolecule(
        modifier = modifier,
        avatar = identityChangeViolation.identityRoomMember.avatarData,
        content = buildAnnotatedString {
            val learnMoreStr = stringResource(CommonStrings.action_learn_more)
            val displayName = identityChangeViolation.identityRoomMember.displayNameOrDefault
            val userIdStr = stringResource(
                CommonStrings.crypto_identity_change_pin_violation_new_user_id,
                identityChangeViolation.identityRoomMember.userId,
            )
            val fullText = stringResource(textId, displayName, userIdStr, learnMoreStr)
            append(fullText)
            val userIdStartIndex = fullText.indexOf(userIdStr)
            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                ),
                start = userIdStartIndex,
                end = userIdStartIndex + userIdStr.length,
            )
            val learnMoreStartIndex = fullText.lastIndexOf(learnMoreStr)
            addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    color = ElementTheme.colors.textPrimary
                ),
                start = learnMoreStartIndex,
                end = learnMoreStartIndex + learnMoreStr.length,
            )
            addLink(
                url = LinkAnnotation.Url(
                    url = LearnMoreConfig.IDENTITY_CHANGE_URL,
                    linkInteractionListener = {
                        onLinkClick(LearnMoreConfig.IDENTITY_CHANGE_URL, true)
                    }
                ),
                start = learnMoreStartIndex,
                end = learnMoreStartIndex + learnMoreStr.length,
            )
        },
        submitText = stringResource(submitTextId),
        onSubmitClick = onSubmitClick,
        level = if (isCritical) ComposerAlertLevel.Critical else ComposerAlertLevel.Default,
    )
}

@PreviewsDayNight
@Composable
internal fun IdentityChangeStateViewPreview(
    @PreviewParameter(IdentityChangeStateProvider::class) state: IdentityChangeState,
) = ElementPreview {
    IdentityChangeStateView(
        state = state,
        onLinkClick = { _, _ -> },
    )
}
