/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset

import android.app.Activity
import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.securebackup.impl.reset.password.ResetIdentityPasswordNode
import io.element.android.features.securebackup.impl.reset.root.ResetIdentityRootNode
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.encryption.IdentityOidcResetHandle
import io.element.android.libraries.matrix.api.encryption.IdentityPasswordResetHandle
import io.element.android.libraries.matrix.api.encryption.IdentityResetHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

private const val resetIdentityTraceTag = "ResetIdentityTrace"
private const val maxOidcResetRetryCount = 1
private const val oidcResetRetryDelayMillis = 750L

/**
 * 重置身份流程节点
 *
 * 负责管理整个身份重置流程的节点。
 * 使用 BackStack 管理重置根页面和密码重置页面。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property resetIdentityFlowManager 重置身份流程管理器
 * @property sessionCoroutineScope 会话协程作用域
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ResetIdentityFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val resetIdentityFlowManager: ResetIdentityFlowManager,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) : BaseFlowNode<ResetIdentityFlowNode.NavTarget>(
    backstack = BackStack(initialElement = NavTarget.Root, savedStateMap = buildContext.savedStateMap),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 重置身份流程回调接口
     */
    interface Callback : Plugin {
        /** 重置完成回调 */
        fun onDone()
    }

    private val callback: Callback = callback()

    /**
     * 导航目标密封接口
     */
    sealed interface NavTarget : Parcelable {
        /** 根页面 */
        @Parcelize
        data object Root : NavTarget

        /** 密码重置页面 */
        @Parcelize
        data object ResetPassword : NavTarget
    }

    /** 当前 Activity 实例 */
    private lateinit var activity: Activity

    /** 是否为深色主题 */
    private var darkTheme: Boolean = false

    /** 重置任务 */
    private var resetJob: Job? = null

    /**
     * 浏览器回流期间如果遇到瞬时断连，就在下一次 onStart 时补一次轮询，
     * 避免网页已确认但 App 因连接中断提前把流程判成失败。
     */
    private var shouldRetryOidcResetOnNextStart = false

    /** 记录已经消耗掉的 OIDC 自动重试次数，当前只允许一次。 */
    private var oidcResetRetryCount = 0

    override fun onBuilt() {
        super.onBuilt()

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                sessionCoroutineScope.launch {
                    val currentHandle = resetIdentityFlowManager.currentHandleFlow.value.dataOrNull()
                    Timber.tag(resetIdentityTraceTag).i(
                        "ResetIdentityFlowNode.onStart currentHandle=%s resetJob=%s shouldRetry=%s retryCount=%s",
                        currentHandle.debugDescription(),
                        resetJob.debugDescription(),
                        shouldRetryOidcResetOnNextStart,
                        oidcResetRetryCount,
                    )

                    if (currentHandle is IdentityOidcResetHandle) {
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onStart preserve existing OIDC handle")
                        // OIDC 重置依赖浏览器回流；这里不能像普通流程一样一回前台就销毁 handle，
                        // 否则网页已完成确认但 SDK 还没来得及刷新验证状态时，流程会被我们自己中断。
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onStart preserve existing OIDC handle")
                    } else {
                        cancelResetJob()
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onStart cancelResetJob finished")
                    }

                    resetIdentityFlowManager.whenResetIsDone {
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.whenResetIsDone callback.onDone")
                        callback.onDone()
                    }

                    if (currentHandle is IdentityOidcResetHandle && shouldRetryOidcResetOnNextStart && resetJob?.isActive != true) {
                        if (oidcResetRetryCount < maxOidcResetRetryCount) {
                            oidcResetRetryCount += 1
                            shouldRetryOidcResetOnNextStart = false
                            Timber.tag(resetIdentityTraceTag).i(
                                "ResetIdentityFlowNode.onStart retrying resetOidc retryCount=%s",
                                oidcResetRetryCount,
                            )
                            delay(oidcResetRetryDelayMillis)
                            launchOidcResetJob(currentHandle, origin = "onStartRetry")
                        } else {
                            shouldRetryOidcResetOnNextStart = false
                            Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onStart retry skipped because retry budget exhausted")
                        }
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                // Make sure we cancel the reset job when the node is destroyed, just in case
                sessionCoroutineScope.launch {
                    Timber.tag(resetIdentityTraceTag).i(
                        "ResetIdentityFlowNode.onDestroy currentHandle=%s resetJob=%s",
                        resetIdentityFlowManager.currentHandleFlow.value.debugDescription(),
                        resetJob.debugDescription(),
                    )
                    cancelResetJob()
                }
            }
        })
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Root -> {
                val callback = object : ResetIdentityRootNode.Callback {
                    override fun onContinue() {
                        // 这里记录用户主动开始重置的时机，方便和浏览器返回后的状态变化串联。
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onContinue")
                        // 重新开始重置时要把自动重试状态清零，避免上一次浏览器回流的失败影响本次流程。
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onContinue")
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.onContinue")
                        oidcResetRetryCount = 0
                        shouldRetryOidcResetOnNextStart = false
                        sessionCoroutineScope.startReset()
                    }
                }
                createNode<ResetIdentityRootNode>(buildContext, listOf(callback))
            }
            is NavTarget.ResetPassword -> {
                val handle = resetIdentityFlowManager.currentHandleFlow.value.dataOrNull() as? IdentityPasswordResetHandle ?: error("No password handle found")
                createNode<ResetIdentityPasswordNode>(
                    buildContext,
                    listOf(ResetIdentityPasswordNode.Inputs(handle))
                )
            }
        }
    }

    private fun CoroutineScope.startReset() = launch {
        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.startReset collecting handle flow")
        resetIdentityFlowManager.getResetHandle()
            .collectLatest { state ->
                Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.startReset state=%s", state.debugDescription())
                when (state) {
                    is AsyncData.Failure -> {
                        cancelResetJob()
                        Timber.e(state.error, "Could not load the reset identity handle.")
                    }
                    is AsyncData.Success -> {
                        when (val handle = state.data) {
                            null -> {
                                Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.startReset handle=null reset already done")
                            }
                            is IdentityOidcResetHandle -> {
                                Timber.tag(resetIdentityTraceTag).i(
                                    "ResetIdentityFlowNode.startReset opening custom tab url=%s",
                                    handle.url,
                                )
                                activity.openUrlInChromeCustomTab(null, darkTheme, handle.url)
                                launchOidcResetJob(handle, origin = "initialStart")
                            }
                            is IdentityPasswordResetHandle -> {
                                Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.startReset navigateToResetPassword")
                                backstack.push(NavTarget.ResetPassword)
                            }
                        }
                    }
                    else -> Unit
                }
            }
    }

    private suspend fun cancelResetJob() {
        Timber.tag(resetIdentityTraceTag).i(
            "ResetIdentityFlowNode.cancelResetJob beforeCancel currentHandle=%s resetJob=%s shouldRetry=%s retryCount=%s",
            resetIdentityFlowManager.currentHandleFlow.value.dataOrNull().debugDescription(),
            resetJob.debugDescription(),
            shouldRetryOidcResetOnNextStart,
            oidcResetRetryCount,
        )
        resetJob?.cancel()
        resetJob = null
        shouldRetryOidcResetOnNextStart = false
        oidcResetRetryCount = 0
        resetIdentityFlowManager.cancel()
        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.cancelResetJob finished")
    }

    private fun CoroutineScope.launchOidcResetJob(
        handle: IdentityOidcResetHandle,
        origin: String,
    ) {
        resetJob?.cancel()
        resetJob = launch {
            Timber.tag(resetIdentityTraceTag).i(
                "ResetIdentityFlowNode.resetJob started origin=%s retryCount=%s",
                origin,
                oidcResetRetryCount,
            )
            handle.resetOidc()
                .onSuccess {
                    shouldRetryOidcResetOnNextStart = false
                    Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.resetJob completed successfully origin=%s", origin)
                }
                .onFailure { failure ->
                    if (failure.shouldRetryOidcReset() && oidcResetRetryCount < maxOidcResetRetryCount) {
                        shouldRetryOidcResetOnNextStart = true
                        // 浏览器关闭瞬间常见的 ConnectionAborted 往往发生在网页已确认、App 重新切回前台的边界，
                        // 此时保留 handle 并等 onStart 后补一次轮询，比立即判定失败更接近真实用户路径。
                        shouldRetryOidcResetOnNextStart = true
                        Timber.tag(resetIdentityTraceTag).w(
                            failure,
                            "ResetIdentityFlowNode.resetJob transient failure origin=%s retryCount=%s",
                            origin,
                            oidcResetRetryCount,
                        )
                    } else {
                        shouldRetryOidcResetOnNextStart = false
                        Timber.tag(resetIdentityTraceTag).e(
                            failure,
                            "ResetIdentityFlowNode.resetJob completed with failure result origin=%s retryCount=%s",
                            origin,
                            oidcResetRetryCount,
                        )
                    }
                }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        // Workaround to get the current activity
        if (!this::activity.isInitialized) {
            activity = requireNotNull(LocalActivity.current)
        }
        darkTheme = !ElementTheme.isLightTheme
        val startResetState by resetIdentityFlowManager.currentHandleFlow.collectAsState()
        if (startResetState.isLoading()) {
            ProgressDialog(
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
                onDismissRequest = {
                    sessionCoroutineScope.launch {
                        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowNode.ProgressDialog dismissed by user")
                        cancelResetJob()
                    }
                }
            )
        }

        BackstackView(modifier)
    }
}

private fun Job?.debugDescription(): String {
    if (this == null) return "null"
    return "active=$isActive cancelled=$isCancelled completed=$isCompleted"
}

private fun AsyncData<IdentityResetHandle?>.debugDescription(): String = when (this) {
    is AsyncData.Uninitialized -> "Uninitialized"
    is AsyncData.Loading -> "Loading"
    is AsyncData.Success -> "Success(data=${data.debugDescription()})"
    is AsyncData.Failure -> "Failure(error=${error::class.simpleName})"
}

internal fun Throwable.shouldRetryOidcReset(): Boolean {
    return generateSequence(this as Throwable?) { it.cause }
        .mapNotNull { it?.message }
        .any { message -> message.contains("ConnectionAborted", ignoreCase = true) }
}

private fun IdentityResetHandle?.debugDescription(): String {
    return this?.let { it::class.simpleName } ?: "null"
}
