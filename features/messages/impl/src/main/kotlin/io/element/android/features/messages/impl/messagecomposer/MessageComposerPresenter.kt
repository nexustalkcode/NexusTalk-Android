/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.Composer
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.features.location.api.LocationService
import io.element.android.features.messages.impl.MessagesNavigator
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.preview.error.sendAttachmentError
import io.element.android.features.messages.impl.draft.ComposerDraftService
import io.element.android.features.messages.impl.messagecomposer.suggestions.RoomAliasSuggestionsDataSource
import io.element.android.features.messages.impl.messagecomposer.suggestions.SuggestionsProcessor
import io.element.android.features.messages.impl.timeline.TimelineController
import io.element.android.features.messages.impl.utils.TextPillificationHelper
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkBuilder
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.api.room.IntentionalMention
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.draft.ComposerDraft
import io.element.android.libraries.matrix.api.room.draft.ComposerDraftType
import io.element.android.libraries.matrix.api.room.getDirectRoomMember
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.room.powerlevels.use
import io.element.android.libraries.matrix.api.timeline.TimelineException
import io.element.android.libraries.matrix.api.timeline.item.event.toEventOrTransactionId
import io.element.android.libraries.matrix.ui.messages.reply.InReplyToDetails
import io.element.android.libraries.matrix.ui.messages.reply.map
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaSenderFactory
import io.element.android.libraries.mediaviewer.api.local.LocalMediaFactory
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.libraries.push.api.notifications.conversations.NotificationConversationService
import io.element.android.libraries.textcomposer.mentions.MentionSpanProvider
import io.element.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.element.android.libraries.textcomposer.model.MarkdownTextEditorState
import io.element.android.libraries.textcomposer.model.Message
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.textcomposer.model.Suggestion
import io.element.android.libraries.textcomposer.model.TextEditorState
import io.element.android.libraries.textcomposer.model.rememberMarkdownTextEditorState
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import io.element.android.wysiwyg.compose.RichTextEditorState
import io.element.android.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import io.element.android.libraries.recentemojis.api.EmojibaseProvider
import io.element.android.libraries.recentemojis.api.GetRecentEmojis
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds
import io.element.android.libraries.core.mimetype.MimeTypes.Any as AnyMimeTypes

/**
 * 消息编辑器 Presenter
 *
 * 负责处理消息编辑器的业务逻辑，管理消息发送、附件处理、提及建议、草稿保存等功能。
 *
 * @property navigator 消息导航器
 * @property timelineController 时间线控制器
 * @property sessionCoroutineScope 会话协程作用域
 * @property room 已加入的房间
 * @property mediaPickerProvider 媒体选择器提供者
 * @property sessionPreferencesStore 会话偏好设置存储
 * @property localMediaFactory 本地媒体工厂
 * @property mediaSenderFactory 媒体发送器工厂
 * @property snackbarDispatcher 提示消息调度器
 * @property analyticsService 分析服务
 * @property locationService 位置服务
 * @property messageComposerContext 消息编辑器上下文
 * @property richTextEditorStateFactory 富文本编辑器状态工厂
 * @property roomAliasSuggestionsDataSource 房间别名建议数据源
 * @property permalinkParser 链接解析器
 * @property permalinkBuilder 链接构建器
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property draftService 草稿服务
 * @property mentionSpanProvider 提及跨度提供者
 * @property pillificationHelper 文本 pill 化辅助工具
 * @property suggestionsProcessor 建议处理器
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 * @property notificationConversationService 通知对话服务
 */
@Suppress("LargeClass")
@AssistedInject
class MessageComposerPresenter(
    @Assisted private val navigator: MessagesNavigator,
    @Assisted private val timelineController: TimelineController,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val room: JoinedRoom,
    private val mediaPickerProvider: PickerProvider,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val localMediaFactory: LocalMediaFactory,
    mediaSenderFactory: MediaSenderFactory,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val analyticsService: AnalyticsService,
    private val locationService: LocationService,
    private val messageComposerContext: DefaultMessageComposerContext,
    private val richTextEditorStateFactory: RichTextEditorStateFactory,
    private val roomAliasSuggestionsDataSource: RoomAliasSuggestionsDataSource,
    private val permalinkParser: PermalinkParser,
    private val permalinkBuilder: PermalinkBuilder,
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    private val draftService: ComposerDraftService,
    private val mentionSpanProvider: MentionSpanProvider,
    private val pillificationHelper: TextPillificationHelper,
    private val suggestionsProcessor: SuggestionsProcessor,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
    private val notificationConversationService: NotificationConversationService,
    private val emojibaseProvider: EmojibaseProvider,
    private val getRecentEmojis: GetRecentEmojis,
) : Presenter<MessageComposerState> {
    /**
     * Presenter 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param timelineController 时间线控制器
         * @param navigator 消息导航器
         * @return MessageComposerPresenter 实例
         */
        fun create(timelineController: TimelineController, navigator: MessagesNavigator): MessageComposerPresenter
    }

    private val mediaSender = mediaSenderFactory.create(timelineMode = timelineController.mainTimelineMode())

    private val cameraPermissionPresenter = permissionsPresenterFactory.create(Manifest.permission.CAMERA)
    private var pendingEvent: MessageComposerEvent? = null
    private val suggestionSearchTrigger = MutableStateFlow<Suggestion?>(null)

    // 用于在测试中禁用某些 UI 相关元素
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var isTesting: Boolean = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var showTextFormatting: Boolean by mutableStateOf(false)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var showEmojiPicker: Boolean by mutableStateOf(false)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var recentEmojis: ImmutableList<String> = persistentListOf()

    @SuppressLint("UnsafeOptInUsageError")
    @Composable
    override fun present(): MessageComposerState {
        val localCoroutineScope = rememberCoroutineScope()

        val roomInfo by room.roomInfoFlow.collectAsState()

        val richTextEditorState = richTextEditorStateFactory.remember()
        if (isTesting) {
            richTextEditorState.isReadyToProcessActions = true
        }
        val markdownTextEditorState = rememberMarkdownTextEditorState(initialText = null, initialFocus = false)

        val cameraPermissionState = cameraPermissionPresenter.present()

        val canShareLocation = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            canShareLocation.value = locationService.isServiceAvailable()
        }

        val galleryMediaPicker = mediaPickerProvider.registerGalleryPicker { uri, mimeType ->
            handlePickedMedia(uri, mimeType)
        }
        val filesPicker = mediaPickerProvider.registerFilePicker(AnyMimeTypes) { uri, mimeType ->
            handlePickedMedia(uri, mimeType ?: MimeTypes.OctetStream)
        }
        val cameraPhotoPicker = mediaPickerProvider.registerCameraPhotoPicker { uri ->
            handlePickedMedia(uri, MimeTypes.Jpeg)
        }
        val cameraVideoPicker = mediaPickerProvider.registerCameraVideoPicker { uri ->
            handlePickedMedia(uri, MimeTypes.Mp4)
        }
        val isFullScreen = rememberSaveable {
            mutableStateOf(false)
        }
        var showAttachmentSourcePicker: Boolean by remember { mutableStateOf(false) }

        val sendTypingNotifications by remember {
            sessionPreferencesStore.isSendTypingNotificationsEnabled()
        }.collectAsState(initial = true)

        LaunchedEffect(cameraPermissionState.permissionGranted) {
            if (cameraPermissionState.permissionGranted) {
                when (pendingEvent) {
                    is MessageComposerEvent.PickAttachmentSource.PhotoFromCamera -> cameraPhotoPicker.launch()
                    is MessageComposerEvent.PickAttachmentSource.VideoFromCamera -> cameraVideoPicker.launch()
                    else -> Unit
                }
                pendingEvent = null
            }
        }

        val suggestions = remember { mutableStateListOf<ResolvedSuggestion>() }
        ResolveSuggestionsEffect(suggestions)

        DisposableEffect(Unit) {
            // 当编辑器释放时声明用户不再打字
            onDispose {
                sessionCoroutineScope.launch {
                    if (sendTypingNotifications) {
                        room.typingNotice(false)
                    }
                }
            }
        }

        val textEditorState by rememberUpdatedState(
            if (showTextFormatting) {
                TextEditorState.Rich(richTextEditorState, roomInfo.isEncrypted == true)
            } else {
                TextEditorState.Markdown(markdownTextEditorState, roomInfo.isEncrypted == true)
            }
        )

        LaunchedEffect(Unit) {
            val draft = draftService.loadDraft(
                roomId = room.roomId,
                // TODO 支持线程中的草稿
                threadRoot = null,
                isVolatile = false
            )
            if (draft != null) {
                applyDraft(draft, markdownTextEditorState, richTextEditorState)
            }
        }

        /**
         * 处理事件
         *
         * @param event 事件
         */
        fun handleEvent(event: MessageComposerEvent) {
            when (event) {
                MessageComposerEvent.ToggleFullScreenState -> isFullScreen.value = !isFullScreen.value
                MessageComposerEvent.CloseSpecialMode -> {
                    if (messageComposerContext.composerMode.isEditing) {
                        localCoroutineScope.launch {
                            resetComposer(markdownTextEditorState, richTextEditorState, fromEdit = true)
                        }
                    } else {
                        messageComposerContext.composerMode = MessageComposerMode.Normal
                    }
                }
                is MessageComposerEvent.SendMessage -> {
                    sessionCoroutineScope.sendMessage(
                        markdownTextEditorState = markdownTextEditorState,
                        richTextEditorState = richTextEditorState,
                    )
                }
                is MessageComposerEvent.SendUri -> {
                    val inReplyToEventId = (messageComposerContext.composerMode as? MessageComposerMode.Reply)?.eventId
                    sessionCoroutineScope.sendAttachment(
                        attachment = Attachment.Media(
                            localMedia = localMediaFactory.createFromUri(
                                uri = event.uri,
                                mimeType = null,
                                name = null,
                                formattedFileSize = null
                            ),
                        ),
                        inReplyToEventId = inReplyToEventId,
                    )

                    // 重置编辑器因为附件已发送
                    messageComposerContext.composerMode = MessageComposerMode.Normal
                }
                is MessageComposerEvent.SetMode -> {
                    localCoroutineScope.setMode(event.composerMode, markdownTextEditorState, richTextEditorState)
                }
                MessageComposerEvent.AddAttachment -> localCoroutineScope.launch {
                    showAttachmentSourcePicker = true
                }
                MessageComposerEvent.DismissAttachmentMenu -> showAttachmentSourcePicker = false
                MessageComposerEvent.PickAttachmentSource.FromGallery -> localCoroutineScope.launch {
                    showAttachmentSourcePicker = false
                    galleryMediaPicker.launch()
                }
                MessageComposerEvent.PickAttachmentSource.FromFiles -> localCoroutineScope.launch {
                    showAttachmentSourcePicker = false
                    filesPicker.launch()
                }
                MessageComposerEvent.PickAttachmentSource.PhotoFromCamera -> localCoroutineScope.launch {
                    showAttachmentSourcePicker = false
                    if (cameraPermissionState.permissionGranted) {
                        cameraPhotoPicker.launch()
                    } else {
                        pendingEvent = event
                        cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                    }
                }
                MessageComposerEvent.PickAttachmentSource.VideoFromCamera -> localCoroutineScope.launch {
                    showAttachmentSourcePicker = false
                    if (cameraPermissionState.permissionGranted) {
                        cameraVideoPicker.launch()
                    } else {
                        pendingEvent = event
                        cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                    }
                }
                MessageComposerEvent.PickAttachmentSource.Location -> {
                    showAttachmentSourcePicker = false
                    // 导航到位置选择屏幕在视图层处理
                }
                MessageComposerEvent.PickAttachmentSource.Poll -> {
                    showAttachmentSourcePicker = false
                    // 导航到创建投票屏幕在视图层处理
                }
                is MessageComposerEvent.ToggleTextFormatting -> {
                    showAttachmentSourcePicker = false
                    localCoroutineScope.toggleTextFormatting(event.enabled, markdownTextEditorState, richTextEditorState)
                }
                is MessageComposerEvent.Error -> {
                    analyticsService.trackError(event.error)
                }
                is MessageComposerEvent.TypingNotice -> {
                    if (sendTypingNotifications) {
                        localCoroutineScope.launch {
                            room.typingNotice(event.isTyping)
                        }
                    }
                }
                is MessageComposerEvent.SuggestionReceived -> {
                    suggestionSearchTrigger.value = event.suggestion
                }
                is MessageComposerEvent.InsertSuggestion -> {
                    localCoroutineScope.launch {
                        if (showTextFormatting) {
                            when (val suggestion = event.resolvedSuggestion) {
                                is ResolvedSuggestion.AtRoom -> {
                                    richTextEditorState.insertAtRoomMentionAtSuggestion()
                                }
                                is ResolvedSuggestion.Member -> {
                                    val text = suggestion.roomMember.userId.value
                                    val link = permalinkBuilder.permalinkForUser(suggestion.roomMember.userId).getOrNull() ?: return@launch
                                    richTextEditorState.insertMentionAtSuggestion(text = text, link = link)
                                }
                                is ResolvedSuggestion.Alias -> {
                                    val text = suggestion.roomAlias.value
                                    val link = permalinkBuilder.permalinkForRoomAlias(suggestion.roomAlias).getOrNull() ?: return@launch
                                    richTextEditorState.insertMentionAtSuggestion(text = text, link = link)
                                }
                            }
                        } else if (markdownTextEditorState.currentSuggestion != null) {
                            markdownTextEditorState.insertSuggestion(
                                resolvedSuggestion = event.resolvedSuggestion,
                                mentionSpanProvider = mentionSpanProvider,
                            )
                            suggestionSearchTrigger.value = null
                        }
                    }
                }
                MessageComposerEvent.SaveDraft -> {
                    val draft = createDraftFromState(markdownTextEditorState, richTextEditorState)
                    sessionCoroutineScope.updateDraft(draft, isVolatile = false)
                }
                is MessageComposerEvent.ToggleEmojiPicker -> {
                    showEmojiPicker = !showEmojiPicker
                    if (showEmojiPicker) {
                        // Load recent emojis when opening the picker
                        localCoroutineScope.launch {
                            recentEmojis = getRecentEmojis().getOrNull() ?: persistentListOf()
                        }
                    }
                }
                is MessageComposerEvent.InsertEmoji -> {
                    localCoroutineScope.launch {
                        insertEmoji(event.emoji, markdownTextEditorState, richTextEditorState)
                    }
                    showEmojiPicker = false
                }
            }
        }

        val resolveMentionDisplay = remember {
            { text: String, url: String ->
                val mentionSpan = mentionSpanProvider.getMentionSpanFor(text, url)
                if (mentionSpan != null) {
                    TextDisplay.Custom(mentionSpan)
                } else {
                    TextDisplay.Plain
                }
            }
        }

        val resolveAtRoomMentionDisplay = remember {
            {
                val mentionSpan = mentionSpanProvider.createEveryoneMentionSpan()
                TextDisplay.Custom(mentionSpan)
            }
        }

        return MessageComposerState(
            textEditorState = textEditorState,
            isFullScreen = isFullScreen.value,
            mode = messageComposerContext.composerMode,
            showAttachmentSourcePicker = showAttachmentSourcePicker,
            showTextFormatting = showTextFormatting,
            showEmojiPicker = showEmojiPicker,
            emojibaseStore = emojibaseProvider.emojibaseStore,
            recentEmojis = recentEmojis,
            canShareLocation = canShareLocation.value,
            suggestions = suggestions.toImmutableList(),
            resolveMentionDisplay = resolveMentionDisplay,
            resolveAtRoomMentionDisplay = resolveAtRoomMentionDisplay,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 解析建议效果
     *
     * 处理用户输入时的 @ 提及建议搜索和显示逻辑。
     *
     * @param suggestions 建议列表状态
     */
    @OptIn(FlowPreview::class)
    @Composable
    private fun ResolveSuggestionsEffect(
        suggestions: SnapshotStateList<ResolvedSuggestion>,
    ) {
        LaunchedEffect(Unit) {
            val currentUserId = room.sessionId

            /**
             * 检查是否可以发送 @房间 提及
             */
            suspend fun canSendRoomMention(): Boolean {
                val userCanSendAtRoom = room.roomPermissions().use(false) { perms ->
                    perms.canOwnUserTriggerRoomNotification()
                }
                return !room.isDm() && userCanSendAtRoom
            }

            // 当输入 @ 时立即触发搜索
            val mentionStartTrigger = suggestionSearchTrigger.filter { it?.text.isNullOrEmpty() }
            // 当用户更改 @ 后的文本时开始搜索，带有防抖以避免太多浪费的工作
            val mentionCompletionTrigger = suggestionSearchTrigger.debounce(0.3.seconds).filter { !it?.text.isNullOrEmpty() }

            val mentionTriggerFlow = merge(mentionStartTrigger, mentionCompletionTrigger)

            val roomAliasSuggestionsFlow = roomAliasSuggestionsDataSource
                .getAllRoomAliasSuggestions()
                .stateIn(this, SharingStarted.Lazily, emptyList())

            combine(mentionTriggerFlow, room.membersStateFlow, roomAliasSuggestionsFlow) { suggestion, roomMembersState, roomAliasSuggestions ->
                val result = suggestionsProcessor.process(
                    suggestion = suggestion,
                    roomMembersState = roomMembersState,
                    roomAliasSuggestions = roomAliasSuggestions,
                    currentUserId = currentUserId,
                    canSendRoomMention = ::canSendRoomMention,
                )
                suggestions.clear()
                suggestions.addAll(result)
            }
                .collect()
        }
    }

    /**
     * 发送消息
     *
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     */
    private fun CoroutineScope.sendMessage(
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
    ) = launch {
        val message = currentComposerMessage(markdownTextEditorState, richTextEditorState, withMentions = true)
        val capturedMode = messageComposerContext.composerMode
        // 立即重置编辑器
        resetComposer(markdownTextEditorState, richTextEditorState, fromEdit = capturedMode is MessageComposerMode.Edit)
        when (capturedMode) {
            is MessageComposerMode.Attachment,
            is MessageComposerMode.Normal -> timelineController.invokeOnCurrentTimeline {
                sendMessage(
                    body = message.markdown,
                    htmlBody = message.html,
                    intentionalMentions = message.intentionalMentions
                )
            }
            is MessageComposerMode.Edit -> {
                timelineController.invokeOnCurrentTimeline {
                    // 首先尝试在当前时间线中编辑消息
                    editMessage(capturedMode.eventOrTransactionId, message.markdown, message.html, message.intentionalMentions)
                        .onFailure { cause ->
                            val eventId = capturedMode.eventOrTransactionId.eventId
                            if (cause is TimelineException.EventNotFound && eventId != null) {
                                // 如果事件在时间线中找不到，直接尝试编辑消息
                                room.editMessage(eventId, message.markdown, message.html, message.intentionalMentions)
                            }
                        }
                }
            }
            is MessageComposerMode.EditCaption -> {
                timelineController.invokeOnCurrentTimeline {
                    editCaption(
                        capturedMode.eventOrTransactionId,
                        caption = message.markdown,
                        formattedCaption = message.html
                    )
                }
            }
            is MessageComposerMode.Reply -> {
                timelineController.invokeOnCurrentTimeline {
                    with(capturedMode) {
                        replyMessage(
                            body = message.markdown,
                            htmlBody = message.html,
                            intentionalMentions = message.intentionalMentions,
                            repliedToEventId = eventId,
                        )
                    }
                }
            }
        }

        val roomInfo = room.info()
        val roomMembers = room.membersStateFlow.value

        notificationConversationService.onSendMessage(
            sessionId = room.sessionId,
            roomId = roomInfo.id,
            roomName = roomInfo.name ?: roomInfo.id.value,
            roomIsDirect = roomInfo.isDm,
            roomAvatarUrl = roomInfo.avatarUrl ?: roomMembers.getDirectRoomMember(roomInfo = roomInfo, sessionId = room.sessionId)?.avatarUrl,
        )

        analyticsService.capture(
            Composer(
                inThread = capturedMode.inThread,
                isEditing = capturedMode.isEditing,
                isReply = capturedMode.isReply,
                // 当我们将发送其他类型的消息时设置正确的类型
                messageType = Composer.MessageType.Text,
            )
        )
    }

    /**
     * 发送附件
     *
     * @param attachment 附件
     * @param inReplyToEventId 回复的事件 ID（可选）
     */
    private fun CoroutineScope.sendAttachment(
        attachment: Attachment,
        inReplyToEventId: EventId?,
    ) = when (attachment) {
        is Attachment.Media -> {
            launch {
                sendMedia(
                    uri = attachment.localMedia.uri,
                    mimeType = attachment.localMedia.info.mimeType,
                    inReplyToEventId = inReplyToEventId,
                )
            }
        }
    }

    /**
     * 处理选择的媒体
     *
     * @param uri 媒体 URI
     * @param mimeType MIME 类型
     */
    private fun handlePickedMedia(
        uri: Uri?,
        mimeType: String? = null,
    ) {
        uri ?: return
        val localMedia = localMediaFactory.createFromUri(
            uri = uri,
            mimeType = mimeType,
            name = null,
            formattedFileSize = null
        )
        val mediaAttachment = Attachment.Media(localMedia)
        val inReplyToEventId = (messageComposerContext.composerMode as? MessageComposerMode.Reply)?.eventId
        navigator.navigateToPreviewAttachments(persistentListOf(mediaAttachment), inReplyToEventId)

        // 重置编辑器因为附件将在单独流程中发送
        messageComposerContext.composerMode = MessageComposerMode.Normal
    }

    /**
     * 发送媒体
     *
     * @param uri 媒体 URI
     * @param mimeType MIME 类型
     * @param inReplyToEventId 回复的事件 ID（可选）
     */
    private suspend fun sendMedia(
        uri: Uri,
        mimeType: String,
        inReplyToEventId: EventId?,
    ) = runCatchingExceptions {
        mediaSender.sendMedia(
            uri = uri,
            mimeType = mimeType,
            mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
            inReplyToEventId = inReplyToEventId,
        ).getOrThrow()
    }
        .onFailure { cause ->
            Timber.e(cause, "Failed to send attachment")
            if (cause is CancellationException) {
                throw cause
            } else {
                val snackbarMessage = SnackbarMessage(sendAttachmentError(cause))
                snackbarDispatcher.post(snackbarMessage)
            }
        }

    /**
     * 更新草稿
     *
     * @param draft 草稿
     * @param isVolatile 是否为临时草稿
     */
    private fun CoroutineScope.updateDraft(
        draft: ComposerDraft?,
        isVolatile: Boolean,
    ) = launch {
        draftService.updateDraft(
            roomId = room.roomId,
            draft = draft,
            isVolatile = isVolatile,
            // TODO 支持线程中的草稿
            threadRoot = null,
        )
    }

    /**
     * 应用草稿
     *
     * @param draft 草稿
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     */
    private suspend fun applyDraft(
        draft: ComposerDraft,
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
    ) {
        val htmlText = draft.htmlText
        val markdownText = draft.plainText
        if (htmlText != null) {
            showTextFormatting = true
            setText(htmlText, markdownTextEditorState, richTextEditorState, requestFocus = true)
        } else {
            showTextFormatting = false
            setText(markdownText, markdownTextEditorState, richTextEditorState, requestFocus = true)
        }
        when (val draftType = draft.draftType) {
            ComposerDraftType.NewMessage -> messageComposerContext.composerMode = MessageComposerMode.Normal
            is ComposerDraftType.Edit -> messageComposerContext.composerMode = MessageComposerMode.Edit(
                eventOrTransactionId = draftType.eventId.toEventOrTransactionId(),
                content = htmlText ?: markdownText
            )
            is ComposerDraftType.Reply -> {
                messageComposerContext.composerMode = MessageComposerMode.Reply(
                    replyToDetails = InReplyToDetails.Loading(draftType.eventId),
                    // 恢复草稿时始终渲染图片应该没问题
                    hideImage = false
                )
                timelineController.invokeOnCurrentTimeline {
                    val replyToDetails = loadReplyDetails(draftType.eventId).map(permalinkParser)
                    messageComposerContext.composerMode = MessageComposerMode.Reply(
                        replyToDetails = replyToDetails,
                        // 恢复草稿时始终渲染图片应该没问题
                        hideImage = false
                    )
                }
            }
        }
    }

    /**
     * 根据当前状态创建草稿
     *
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     * @return 草稿（如果没有内容则返回 null）
     */
    private fun createDraftFromState(
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
    ): ComposerDraft? {
        val message = currentComposerMessage(markdownTextEditorState, richTextEditorState, withMentions = false)
        val draftType = when (val mode = messageComposerContext.composerMode) {
            is MessageComposerMode.Attachment,
            is MessageComposerMode.Normal -> ComposerDraftType.NewMessage
            is MessageComposerMode.Edit -> {
                mode.eventOrTransactionId.eventId?.let { eventId -> ComposerDraftType.Edit(eventId) }
            }
            is MessageComposerMode.Reply -> ComposerDraftType.Reply(mode.eventId)
            is MessageComposerMode.EditCaption -> {
                // TODO 需要 SDK 中的新类型来保存标题
                null
            }
        }
        return if (draftType == null || message.markdown.isBlank()) {
            null
        } else {
            ComposerDraft(
                draftType = draftType,
                htmlText = message.html,
                plainText = message.markdown,
            )
        }
    }

    /**
     * 获取当前编辑器消息
     *
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     * @param withMentions 是否包含提及
     * @return 消息
     */
    private fun currentComposerMessage(
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
        withMentions: Boolean,
    ): Message {
        return if (showTextFormatting) {
            val html = richTextEditorState.messageHtml
            val markdown = richTextEditorState.messageMarkdown
            val mentions = richTextEditorState.mentionsState
                .takeIf { withMentions }
                ?.let { state ->
                    buildList {
                        if (state.hasAtRoomMention) {
                            add(IntentionalMention.Room)
                        }
                        for (userId in state.userIds) {
                            add(IntentionalMention.User(UserId(userId)))
                        }
                    }
                }
                .orEmpty()
            Message(html = html, markdown = markdown, intentionalMentions = mentions)
        } else {
            val markdown = markdownTextEditorState.getMessageMarkdown(permalinkBuilder)
            val mentions = if (withMentions) {
                markdownTextEditorState.getMentions()
            } else {
                emptyList()
            }
            Message(html = null, markdown = markdown, intentionalMentions = mentions)
        }
    }

    /**
     * 切换文本格式
     *
     * @param enabled 是否启用
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     */
    private fun CoroutineScope.toggleTextFormatting(
        enabled: Boolean,
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState
    ) = launch {
        showTextFormatting = enabled
        if (showTextFormatting) {
            val markdown = markdownTextEditorState.getMessageMarkdown(permalinkBuilder)
            richTextEditorState.setMarkdown(markdown)
            richTextEditorState.requestFocus()
            analyticsService.captureInteraction(Interaction.Name.MobileRoomComposerFormattingEnabled)
        } else {
            val markdown = richTextEditorState.messageMarkdown
            val markdownWithMentions = pillificationHelper.pillify(markdown, false)
            markdownTextEditorState.text.update(markdownWithMentions, true)
            // 给一些时间清除前一个编辑器的焦点
            delay(100)
            markdownTextEditorState.requestFocusAction()
        }
    }

    /**
     * 设置编辑器模式
     *
     * @param newComposerMode 新编辑器模式
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     */
    private fun CoroutineScope.setMode(
        newComposerMode: MessageComposerMode,
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
    ) = launch {
        val currentComposerMode = messageComposerContext.composerMode
        when (newComposerMode) {
            is MessageComposerMode.Edit -> {
                if (currentComposerMode.isEditing.not()) {
                    val draft = createDraftFromState(markdownTextEditorState, richTextEditorState)
                    updateDraft(draft, isVolatile = true).join()
                }
                setText(newComposerMode.content, markdownTextEditorState, richTextEditorState)
            }
            is MessageComposerMode.EditCaption -> {
                if (currentComposerMode.isEditing.not()) {
                    val draft = createDraftFromState(markdownTextEditorState, richTextEditorState)
                    updateDraft(draft, isVolatile = true).join()
                }
                setText(newComposerMode.content, markdownTextEditorState, richTextEditorState)
            }
            else -> {
                // 从编辑模式来时，只需清除编辑器，因为在这种情况下重置临时草稿会很奇怪
                if (currentComposerMode.isEditing) {
                    setText("", markdownTextEditorState, richTextEditorState)
                }
            }
        }
        messageComposerContext.composerMode = newComposerMode
    }

    /**
     * 插入 emoji 到编辑器
     *
     * @param emoji 要插入的 emoji 字符
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     */
    private suspend fun insertEmoji(
        emoji: String,
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
    ) {
        if (showTextFormatting) {
            // Rich text mode
            val currentText = richTextEditorState.messageMarkdown
            richTextEditorState.setMarkdown((currentText + emoji).take(MAX_MESSAGE_LENGTH))
        } else {
            // Markdown mode
            val currentText = markdownTextEditorState.text.value().toString()
            markdownTextEditorState.text.update((currentText + emoji).take(MAX_MESSAGE_LENGTH), true)
        }
    }

    /**
     * 重置编辑器
     *
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     * @param fromEdit 是否来自编辑模式
     */
    private suspend fun resetComposer(
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
        fromEdit: Boolean,
    ) {
        // 仅在来自编辑模式时使用临时草稿
        val draft = draftService.loadDraft(
            roomId = room.roomId,
            // TODO 支持线程中的草稿
            threadRoot = null,
            isVolatile = true
        ).takeIf { fromEdit }
        if (draft != null) {
            applyDraft(draft, markdownTextEditorState, richTextEditorState)
        } else {
            setText("", markdownTextEditorState, richTextEditorState)
            messageComposerContext.composerMode = MessageComposerMode.Normal
        }
    }

    /**
     * 设置文本内容
     *
     * @param content 文本内容
     * @param markdownTextEditorState Markdown 编辑器状态
     * @param richTextEditorState 富文本编辑器状态
     * @param requestFocus 是否请求焦点
     */
    private suspend fun setText(
        content: String,
        markdownTextEditorState: MarkdownTextEditorState,
        richTextEditorState: RichTextEditorState,
        requestFocus: Boolean = false,
    ) {
        if (showTextFormatting) {
            richTextEditorState.setHtml(content)
            if (requestFocus) {
                richTextEditorState.requestFocus()
            }
        } else {
            if (content.isEmpty()) {
                markdownTextEditorState.selection = IntRange.EMPTY
            }
            val pillifiedContent = pillificationHelper.pillify(content, false)
            markdownTextEditorState.text.update(pillifiedContent, true)
            if (requestFocus) {
                markdownTextEditorState.requestFocusAction()
            }
        }
    }
}
