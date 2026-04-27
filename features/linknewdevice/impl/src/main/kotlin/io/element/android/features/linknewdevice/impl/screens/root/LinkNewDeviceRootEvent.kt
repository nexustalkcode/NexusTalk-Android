/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.root

/**
 * 新设备关联根页面可能触发的用户事件。
 */
sealed interface LinkNewDeviceRootEvent {
    /** 开始关联移动端设备。 */
    data object LinkMobileDevice : LinkNewDeviceRootEvent
    /** 关闭当前弹窗或提示。 */
    data object CloseDialog : LinkNewDeviceRootEvent
}
