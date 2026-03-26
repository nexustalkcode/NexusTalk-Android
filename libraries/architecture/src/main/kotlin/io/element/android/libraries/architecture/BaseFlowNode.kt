/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.combined.plus
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.navigation.transition.TransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import io.element.android.libraries.architecture.overlay.Overlay

/**
 * 这是一个 [ParentNode] 的抽象基类，它包含一个 [BackStack] 导航栈和一个 [Overlay] 覆盖层。
 * 用于表示应用程序中的流程（Flow），例如登录流程、设置流程等。
 *
 * 设计理念：
 * - 使用 BackStack 管理页面导航，支持标准的入栈/出栈操作
 * - 使用 Overlay 显示临时性UI（如对话框、加载指示器），不影响导航历史
 * - 使用 PermanentNavModel 持久化显示的节点（如底部导航栏）
 *
 * @param NavTarget 导航目标的类型，用于标识不同的页面或状态
 * @param backstack 用于管理页面栈的导航模型
 * @param buildContext 构建上下文，包含节点构建所需的信息
 * @param plugins 插件列表，用于扩展节点功能
 * @param overlay 覆盖层控制器，用于管理临时性UI的显示
 * @param permanentNavModel 持久化导航模型，用于始终显示的节点
 * @param childKeepMode 子节点的保留模式，决定子节点在离开屏幕时的行为
 */
@Stable
abstract class BaseFlowNode<NavTarget : Any>(
    val backstack: BackStack<NavTarget>,
    buildContext: BuildContext,
    plugins: List<Plugin>,
    val overlay: Overlay<NavTarget> = Overlay(null),
    val permanentNavModel: PermanentNavModel<NavTarget> = PermanentNavModel(emptySet(), null),
    childKeepMode: ChildEntry.KeepMode = ChildEntry.KeepMode.KEEP,
) : ParentNode<NavTarget>(
    navModel = overlay + backstack + permanentNavModel,
    buildContext = buildContext,
    plugins = plugins,
    childKeepMode = childKeepMode,
) {
    /**
     * 节点构建完成后的回调方法。
     *
     * 功能说明：
     * 1. 记录当前流程节点的创建日志
     * 2. 监听所有子节点的附加事件
     * 3. 为非 BaseFlowNode 类型的子节点记录生命周期日志
     *
     * 注意事项：
     * - BaseFlowNode 类型的子节点由其父节点记录日志，避免重复
     * - 此方法在节点完成构建后立即执行
     */
    override fun onBuilt() {
        super.onBuilt()
        lifecycle.logLifecycle(this::class.java.simpleName)
        whenChildAttached<Node> { _, child ->
            // BackStack 中的节点将由其父节点记录日志
            // 这里只记录非 BaseFlowNode 类型的子节点
            if (child !is BaseFlowNode<*>) {
                child.lifecycle.logLifecycle(child::class.java.simpleName)
            }
        }
    }
}

/**
 * 显示 BackStack 中的子节点的Composable函数。
 *
 * 功能说明：
 * - 这是 BaseFlowNode 的扩展函数，用于在Composable中渲染导航栈中的当前页面
 * - 自动管理子节点的创建、销毁和状态保留
 * - 支持自定义过渡动画处理器
 *
 * @param modifier 修饰符，用于调整布局属性
 * @param transitionHandler 过渡动画处理器，控制页面切换时的动画效果
 *        默认使用滑动动画（rememberBackstackSlider），提供流畅的页面过渡体验
 *        滑动动画的刚度设置为 Spring.StiffnessMediumLow，使动画既不过于急促也不过于缓慢
 *
 * 使用示例：
 * ```kotlin
 * BaseFlowNode.BackstackView()
 * // 或带自定义修饰符
 * BaseFlowNode.BackstackView(modifier = Modifier.fillMaxSize())
 * ```
 */
@Composable
inline fun <reified NavTarget : Any> BaseFlowNode<NavTarget>.BackstackView(
    modifier: Modifier = Modifier,
    transitionHandler: TransitionHandler<NavTarget, BackStack.State> = rememberBackstackSlider(
        transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
    ),
) {
    Children(
        modifier = modifier,
        navModel = backstack,
        transitionHandler = transitionHandler,
    )
}

/**
 * 显示 Overlay 覆盖层中的子节点的Composable函数。
 *
 * 功能说明：
 * - 用于在页面顶层显示临时性UI元素，如对话框、Toast、加载指示器等
 * - 覆盖层中的节点不影响导航历史，用户操作不会被记录到BackStack中
 * - 使用淡入淡出动画（rememberBackstackFader）提供柔和的显示效果
 *
 * 与 BackstackView 的区别：
 * - Overlay 不会创建导航历史记录
 * - Overlay 可以叠加在多个页面之上
 * - Overlay 适合显示全局性的临时UI
 *
 * @param modifier 修饰符，用于调整布局属性
 * @param transitionHandler 过渡动画处理器
 *        默认使用淡入淡出动画（rememberBackstackFader），提供平滑的显示/隐藏效果
 */
@Composable
inline fun <reified NavTarget : Any> BaseFlowNode<NavTarget>.OverlayView(
    modifier: Modifier = Modifier,
    transitionHandler: TransitionHandler<NavTarget, BackStack.State> = rememberBackstackFader(),
) {
    Children(
        modifier = modifier,
        navModel = overlay,
        transitionHandler = transitionHandler,
    )
}

/**
 * 将 BackStack 视图和 Overlay 视图组合在同一个 Box 布局中的Composable函数。
 *
 * 布局层次结构（从底层到顶层）：
 * 1. 底层：BackStack 视图（页面内容）
 * 2. 中间层：Overlay 视图（临时性UI，如对话框）
 * 3. 顶层：自定义内容（通过 content 参数传入）
 *
 * 功能说明：
 * - 提供一个组合容器，同时渲染导航页面和覆盖层
 * - 三个层次按顺序叠加，覆盖层会显示在页面内容之上
 * - 适用于需要同时显示页面内容和临时UI的场景
 *
 * 使用示例：
 * ```kotlin
 * BackstackWithOverlayBox {
 *     // 在页面和覆盖层之上添加自定义内容
 *     CustomTopContent()
 * }
 * ```
 *
 * @param modifier 修饰符，用于调整整个容器的布局属性
 * @param content 顶层自定义内容的Composable函数，显示在 Overlay 之上
 */
@Composable
inline fun <reified NavTarget : Any> BaseFlowNode<NavTarget>.BackstackWithOverlayBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier) {
        BackstackView()
        OverlayView()
        content()
    }
}
