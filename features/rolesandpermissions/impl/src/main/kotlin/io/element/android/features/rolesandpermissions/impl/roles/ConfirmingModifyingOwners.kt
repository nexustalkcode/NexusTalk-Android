/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.roles

import io.element.android.libraries.architecture.AsyncAction

/**
 * 表示“修改 owner 列表”进入确认态。
 */
data object ConfirmingModifyingOwners : AsyncAction.Confirming
