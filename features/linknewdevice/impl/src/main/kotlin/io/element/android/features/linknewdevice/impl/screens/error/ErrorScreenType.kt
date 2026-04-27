/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.error

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.NodeInputs
import kotlinx.parcelize.Parcelize

@Immutable
/**
 * 设备关联流程中可能展示的错误页类型。
 */
sealed interface ErrorScreenType : NodeInputs, Parcelable {
    /** 用户主动取消了流程。 */
    @Parcelize
    data object Cancelled : ErrorScreenType

    /** 关联链接或校验过程已经过期。 */
    @Parcelize
    data object Expired : ErrorScreenType

    /** 两端显示的校验码不一致。 */
    @Parcelize
    data object Mismatch2Digits : ErrorScreenType

    /** 检测到不安全信道或潜在中间人攻击。 */
    @Parcelize
    data object InsecureChannelDetected : ErrorScreenType

    /** 对端拒绝了本次关联请求。 */
    @Parcelize
    data object Declined : ErrorScreenType

    /** 当前协议版本不受支持。 */
    @Parcelize
    data object ProtocolNotSupported : ErrorScreenType

    /** 当前环境不支持完成流程所需的 sliding sync。 */
    @Parcelize
    data object SlidingSyncNotAvailable : ErrorScreenType

    /** 兜底的未知错误页。 */
    @Parcelize
    data object UnknownError : ErrorScreenType
}
