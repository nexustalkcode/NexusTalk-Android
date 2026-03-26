/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import io.element.android.libraries.core.extensions.runCatchingExceptions

/**
 * 密封接口，用于建模异步操作的状态。
 *
 * 这个接口提供了对异步操作生命周期的完整建模：
 * - Uninitialized: 操作尚未开始
 * - Loading: 操作正在进行中
 * - Success: 操作成功完成
 * - Failure: 操作失败
 *
 * 通过密封接口，我们可以利用 Kotlin 的模式匹配（when 表达式）
 * 来安全地处理所有可能的状态，确保所有情况都被正确处理。
 *
 * @param T 操作成功时返回的数据类型
 */
@Stable
sealed interface AsyncData<out T> {
    /**
     * 表示操作失败的状态。
     *
     * 当异步操作在执行过程中抛出异常时，会进入此状态。
     * 通过保留上一次成功的数据（prevData），可以在显示错误时
     * 仍然向用户展示部分有效信息，提升用户体验。
     *
     * @param T 操作期望返回的数据类型
     * @property error 导致操作失败的异常对象
     * @property prevData 上一次成功执行返回的数据（如果有的话）
     */
    data class Failure<out T>(
        val error: Throwable,
        val prevData: T? = null,
    ) : AsyncData<T>

    /**
     * 表示操作正在进行中的状态。
     *
     * 当异步操作开始执行但尚未完成时，会进入此状态。
     * 同样保留上一次成功的数据，可以在加载新数据时
     * 向用户展示之前的内容，避免界面空白。
     *
     * @param T 操作期望返回的数据类型
     * @property prevData 上一次成功执行返回的数据（用于显示旧数据）
     */
    data class Loading<out T>(
        val prevData: T? = null,
    ) : AsyncData<T>

    /**
     * 表示操作成功完成的状态。
     *
     * 当异步操作正常执行完毕并返回结果时，会进入此状态。
     * 这是最理想的状态，表示数据已经准备好可以使用。
     *
     * @param T 操作返回的数据类型
     * @property data 操作成功返回的数据
     */
    data class Success<out T>(
        val data: T,
    ) : AsyncData<T>

    /**
     * 表示操作尚未初始化的状态。
     *
     * 这是异步操作的初始状态，表示操作还没有被触发执行。
     * 常用于页面首次加载时，或者数据已经被清除的情况。
     */
    data object Uninitialized : AsyncData<Nothing>

    /**
     * 获取操作返回的数据。
     *
     * 根据当前状态返回对应的数据：
     * - Success: 返回成功数据
     * - Failure: 返回失败前的数据（如果存在）
     * - Loading: 返回加载前的数据（如果存在）
     * - Uninitialized: 返回 null
     *
     * @return 操作返回的数据，如果不可用则返回 null
     * @note 注意：此方法可能在操作未成功时返回过期数据
     */
    fun dataOrNull(): T? = when (this) {
        is Failure -> prevData
        is Loading -> prevData
        is Success -> data
        Uninitialized -> null
    }

    /**
     * 获取操作失败的错误信息。
     *
     * @return 导致操作失败的 Throwable 对象，如果操作未失败则返回 null
     */
    fun errorOrNull(): Throwable? = when (this) {
        is Failure -> error
        else -> null
    }

    /**
     * 判断当前状态是否为失败状态。
     *
     * @return 如果是 Failure 状态返回 true，否则返回 false
     */
    fun isFailure(): Boolean = this is Failure<T>

    /**
     * 判断当前状态是否为加载中状态。
     *
     * @return 如果是 Loading 状态返回 true，否则返回 false
     */
    fun isLoading(): Boolean = this is Loading<T>

    /**
     * 判断当前状态是否为成功状态。
     *
     * @return 如果是 Success 状态返回 true，否则返回 false
     */
    fun isSuccess(): Boolean = this is Success<T>

    /**
     * 判断当前状态是否为未初始化状态。
     *
     * @return 如果是 Uninitialized 状态返回 true，否则返回 false
     */
    fun isUninitialized(): Boolean = this == Uninitialized

    /**
     * 判断操作是否已经完成（无论成功或失败）。
     *
     * @return 如果是 Success 或 Failure 状态返回 true，否则返回 false
     * @note 这个方法可用于判断是否可以显示最终结果
     */
    fun isReady() = isSuccess() || isFailure()
}

/**
 * 在 [MutableState] 上执行的扩展函数，用于安全地执行异步操作并自动更新状态。
 *
 * 这个函数封装了异步操作的执行流程：
 * 1. 将状态设置为 Loading（保留上一次的数据用于显示）
 * 2. 执行传入的代码块
 * 3. 根据执行结果更新状态为 Success 或 Failure
 * 4. 返回 Result 对象供调用者使用
 *
 * 使用此方法可以确保 UI 始终反映当前的加载状态，
 * 同时提供错误处理和数据持久化的能力。
 *
 * @param T 异步操作返回数据的类型
 * @param errorTransform 可选的错误转换函数，用于将原始异常转换为更友好的异常
 * @param block 要执行的异步操作代码块
 * @return 返回包含成功数据或失败的 Result 对象
 * @sample 示例：viewModel.someState.runCatchingUpdatingState { repository.fetchData() }
 * @see runUpdatingState 了解更多底层实现细节
 */
suspend inline fun <T> MutableState<AsyncData<T>>.runCatchingUpdatingState(
    errorTransform: (Throwable) -> Throwable = { it },
    block: () -> T,
): Result<T> = runUpdatingState(
    state = this,
    errorTransform = errorTransform,
    resultBlock = {
        runCatchingExceptions {
            block()
        }
    },
)

/**
 * 在可挂起函数上执行的扩展函数，用于安全地执行异步操作并自动更新状态。
 *
 * 这是 [MutableState.runCatchingUpdatingState] 的另一种调用形式，
 * 允许将代码块作为扩展接收者来调用，使代码更加流畅。
 *
 * @param T 异步操作返回数据的类型
 * @param state 要更新的状态对象
 * @param errorTransform 可选的错误转换函数，用于将原始异常转换为更友好的异常
 * @return 返回包含成功数据或失败的 Result 对象
 * @see MutableState.runCatchingUpdatingState
 */
suspend inline fun <T> (suspend () -> T).runCatchingUpdatingState(
    state: MutableState<AsyncData<T>>,
    errorTransform: (Throwable) -> Throwable = { it },
): Result<T> = runUpdatingState(
    state = state,
    errorTransform = errorTransform,
    resultBlock = {
        runCatchingExceptions {
            this()
        }
    },
)

/**
 * 在 [MutableState] 上执行的扩展函数，用于在执行异步操作时更新状态。
 *
 * 此函数是 [runUpdatingState] 的包装器，提供更便捷的调用方式。
 *
 * @param T 异步操作返回数据的类型
 * @param errorTransform 可选的错误转换函数，用于将原始异常转换为更友好的异常
 * @param resultBlock 返回 [Result] 的代码块
 * @return 返回包含成功数据或失败的 Result 对象
 * @see runUpdatingState 了解更多详细信息
 */
suspend inline fun <T> MutableState<AsyncData<T>>.runUpdatingState(
    errorTransform: (Throwable) -> Throwable = { it },
    resultBlock: () -> Result<T>,
): Result<T> = runUpdatingState(
    state = this,
    errorTransform = errorTransform,
    resultBlock = resultBlock,
)

/**
 * 核心状态更新函数，用于在执行异步操作时封装进度和返回值。
 *
 * 此函数管理异步操作的完整生命周期：
 * 1. **初始状态**：保存当前状态中的数据（如果有）
 * 2. **加载中**：将状态设置为 Loading，保留旧数据
 * 3. **执行操作**：调用 resultBlock 执行实际的异步操作
 * 4. **结果处理**：
 *    - 成功：将状态设置为 Success，保存新数据
 *    - 失败：将状态设置为 Failure，保存错误和旧数据
 *
 * 通过保留上一次成功的数据，即使在加载失败时，
 * UI 仍然可以展示有效信息，提供更好的用户体验。
 *
 * @param T 异步操作返回数据的类型
 * @param state 要更新的 MutableState 对象
 * @param errorTransform 可选的错误转换函数，用于将原始异常转换为更友好的异常
 * @param resultBlock 异步操作代码块，返回 Result 对象
 * @return 返回 resultBlock 执行产生的 Result 对象
 *
 * 使用示例：
 * ```
 * runUpdatingState(
 *     state = viewModel.uiState,
 *     errorTransform = { it.toUserFriendlyError() }
 * ) {
 *     repository.fetchData()
 * }
 * ```
 *
 * @note 当 AGP 版本问题修复后，将启用contracts以优化编译器内联
 */
@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
suspend inline fun <T> runUpdatingState(
    state: MutableState<AsyncData<T>>,
    errorTransform: (Throwable) -> Throwable = { it },
    resultBlock: suspend () -> Result<T>,
): Result<T> {
    // Restore when the issue with contracts and AGP 8.13.x is fixed
//    contract {
//        callsInPlace(resultBlock, InvocationKind.EXACTLY_ONCE)
//    }
    val prevData = state.value.dataOrNull()
    state.value = AsyncData.Loading(prevData = prevData)
    return resultBlock().fold(
        onSuccess = {
            state.value = AsyncData.Success(it)
            Result.success(it)
        },
        onFailure = {
            val error = errorTransform(it)
            state.value = AsyncData.Failure(
                error = error,
                prevData = prevData,
            )
            Result.failure(error)
        }
    )
}

/**
 * 对 [AsyncData] 中的数据进行转换的函数。
 *
 * 这个函数类似于 Kotlin 标准库中的 `map` 操作符，
 * 用于在保持异步状态不变的情况下转换数据。
 *
 * 它会遍历所有状态类型并进行相应的转换：
 * - Success: 对成功数据应用转换函数
 * - Failure: 对失败前的数据进行转换（如果存在）
 * - Loading: 对加载前的数据进行转换（如果存在）
 * - Uninitialized: 保持未初始化状态不变
 *
 * 使用此函数可以在不丢失异步状态的情况下，
 * 对数据进行任意转换，如格式化、过滤、映射等。
 *
 * @param T 原始数据类型
 * @param R 转换后的数据类型
 * @param transform 数据转换函数，将 T 类型转换为 R 类型
 * @return 包含转换后数据的新的 [AsyncData] 对象
 *
 * 使用示例：
 * ```
 * val userData: AsyncData<User> = ...
 * val userNameData: AsyncData<String> = userData.map { it.name }
 * ```
 *
 * @note 在 Failure 和 Loading 状态下，如果存在 prevData，
 *       转换函数也会应用到 prevData 上，以确保数据的一致性
 */
inline fun <T, R> AsyncData<T>.map(
    transform: (T) -> R,
): AsyncData<R> {
    return when (this) {
        is AsyncData.Failure -> AsyncData.Failure(
            error = error,
            prevData = prevData?.let { transform(prevData) }
        )
        is AsyncData.Loading -> AsyncData.Loading(prevData?.let { transform(prevData) })
        is AsyncData.Success -> AsyncData.Success(transform(data))
        AsyncData.Uninitialized -> AsyncData.Uninitialized
    }
}
