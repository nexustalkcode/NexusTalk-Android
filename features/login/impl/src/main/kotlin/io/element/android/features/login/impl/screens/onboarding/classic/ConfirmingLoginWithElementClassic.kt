/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding.classic

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 表示“使用 Element Classic 登录”进入确认态。
 *
 * @property userId 从 Element Classic 检测到的用户 ID。
 */
class ConfirmingLoginWithElementClassic(
    val userId: UserId,
) : AsyncAction.Confirming
