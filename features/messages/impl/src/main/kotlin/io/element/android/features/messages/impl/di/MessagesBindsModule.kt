/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.messages.impl.crypto.historyvisible.HistoryVisibleState
import io.element.android.features.messages.impl.crypto.historyvisible.HistoryVisibleStatePresenter
import io.element.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.element.android.features.messages.impl.crypto.identity.IdentityChangeStatePresenter
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailurePresenter
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.element.android.features.messages.impl.link.LinkPresenter
import io.element.android.features.messages.impl.link.LinkState
import io.element.android.features.messages.impl.pinned.banner.PinnedMessagesBannerPresenter
import io.element.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.element.android.features.messages.impl.timeline.components.customreaction.CustomReactionPresenter
import io.element.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.element.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryPresenter
import io.element.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.element.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetPresenter
import io.element.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionPresenter
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.messages.impl.typing.TypingNotificationPresenter
import io.element.android.features.messages.impl.typing.TypingNotificationState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.RoomScope

/**
 * 消息模块绑定模块接口
 *
 * 定义消息功能模块中各个 Presenter 的依赖绑定关系。
 * 使用 @ContributesTo 注解贡献到 RoomScope，
 * 使用 @BindingContainer 注解标记为绑定容器。
 *
 * @see RoomScope 房间作用域
 * @see Presenter Presenter基类
 */
@ContributesTo(RoomScope::class)
@BindingContainer
interface MessagesBindsModule {
    /**
     * 绑定固定消息横幅 Presenter
     *
     * @return PinnedMessagesBannerPresenter 实例
     */
    @Binds
    fun bindPinnedMessagesBannerPresenter(presenter: PinnedMessagesBannerPresenter): Presenter<PinnedMessagesBannerState>

    /**
     * 绑定解决验证用户发送失败 Presenter
     *
     * @return ResolveVerifiedUserSendFailurePresenter 实例
     */
    @Binds
    fun bindResolveVerifiedUserSendFailurePresenter(presenter: ResolveVerifiedUserSendFailurePresenter): Presenter<ResolveVerifiedUserSendFailureState>

    /**
     * 绑定打字通知 Presenter
     *
     * @return TypingNotificationPresenter 实例
     */
    @Binds
    fun bindTypingNotificationPresenter(presenter: TypingNotificationPresenter): Presenter<TypingNotificationState>

    /**
     * 绑定时间线保护 Presenter
     *
     * @return TimelineProtectionPresenter 实例
     */
    @Binds
    fun bindTimelineProtectionPresenter(presenter: TimelineProtectionPresenter): Presenter<TimelineProtectionState>

    /**
     * 绑定链接 Presenter
     *
     * @return LinkPresenter 实例
     */
    @Binds
    fun bindLinkPresenter(presenter: LinkPresenter): Presenter<LinkState>

    /**
     * 绑定自定义反应 Presenter
     *
     * @return CustomReactionPresenter 实例
     */
    @Binds
    fun bindCustomReactionPresenter(presenter: CustomReactionPresenter): Presenter<CustomReactionState>

    /**
     * 绑定反应摘要 Presenter
     *
     * @return ReactionSummaryPresenter 实例
     */
    @Binds
    fun bindReactionSummaryPresenter(presenter: ReactionSummaryPresenter): Presenter<ReactionSummaryState>

    /**
     * 绑定已读回执底部表单 Presenter
     *
     * @return ReadReceiptBottomSheetPresenter 实例
     */
    @Binds
    fun bindReadReceiptBottomSheetPresenter(presenter: ReadReceiptBottomSheetPresenter): Presenter<ReadReceiptBottomSheetState>

    /**
     * 绑定身份变更状态 Presenter
     *
     * @return IdentityChangeStatePresenter 实例
     */
    @Binds
    fun bindIdentityChangeStatePresenter(presenter: IdentityChangeStatePresenter): Presenter<IdentityChangeState>

    /**
     * 绑定历史可见性状态 Presenter
     *
     * @return HistoryVisibleStatePresenter 实例
     */
    @Binds
    fun bindHistoryVisibleStatePresenter(presenter: HistoryVisibleStatePresenter): Presenter<HistoryVisibleState>
}
