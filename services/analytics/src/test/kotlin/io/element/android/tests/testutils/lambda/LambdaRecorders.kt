/*
 * Copyright (c) 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.tests.testutils.lambda

private data class ExpectedValue<T>(
    val value: T,
)

fun <T> value(value: T): Any? = ExpectedValue(value)

fun <T> lambdaError(): T {
    error("This fake lambda should be provided in tests")
}

/**
 * 这里只实现 analytics 测试当前真实用到的 0/1/3 参数 recorder，
 * 避免为了恢复整个 tests:testutils 模块而扩大本轮改动边界。
 */
fun <R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: () -> R,
): LambdaRecorder0<R> = LambdaRecorder0(ensureNeverCalled, block)

fun <P1, R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: (P1) -> R,
): LambdaRecorder1<P1, R> = LambdaRecorder1(ensureNeverCalled, block)

fun <P1, P2, P3, R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: (P1, P2, P3) -> R,
): LambdaRecorder3<P1, P2, P3, R> = LambdaRecorder3(ensureNeverCalled, block)

class LambdaRecorder0<R>(
    private val ensureNeverCalled: Boolean,
    private val block: () -> R,
) : () -> R {
    private val calls = mutableListOf<List<Any?>>()

    override fun invoke(): R {
        if (ensureNeverCalled) {
            throw AssertionError("Expected lambda to never be called")
        }
        calls.add(emptyList())
        return block()
    }

    fun assertions(): RecordedAssertions = RecordedAssertions(calls)
}

class LambdaRecorder1<P1, R>(
    private val ensureNeverCalled: Boolean,
    private val block: (P1) -> R,
) : (P1) -> R {
    private val calls = mutableListOf<List<Any?>>()

    override fun invoke(p1: P1): R {
        if (ensureNeverCalled) {
            throw AssertionError("Expected lambda to never be called")
        }
        calls.add(listOf(p1))
        return block(p1)
    }

    fun assertions(): RecordedAssertions = RecordedAssertions(calls)
}

class LambdaRecorder3<P1, P2, P3, R>(
    private val ensureNeverCalled: Boolean,
    private val block: (P1, P2, P3) -> R,
) : (P1, P2, P3) -> R {
    private val calls = mutableListOf<List<Any?>>()

    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
    ): R {
        if (ensureNeverCalled) {
            throw AssertionError("Expected lambda to never be called")
        }
        calls.add(listOf(p1, p2, p3))
        return block(p1, p2, p3)
    }

    fun assertions(): RecordedAssertions = RecordedAssertions(calls)
}

class RecordedAssertions(
    private val calls: List<List<Any?>>,
) {
    fun isCalledOnce(): RecordedAssertions {
        if (calls.size != 1) {
            throw AssertionError("Expected lambda to be called once, but was called ${calls.size} times")
        }
        return this
    }

    fun isNeverCalled(): RecordedAssertions {
        if (calls.isNotEmpty()) {
            throw AssertionError("Expected lambda to never be called, but was called ${calls.size} times")
        }
        return this
    }

    fun with(vararg expectedArguments: Any?): RecordedAssertions {
        val actualArguments = calls.singleOrNull()
            ?: throw AssertionError("Expected exactly one call before asserting arguments, but found ${calls.size}")
        val normalizedExpected = expectedArguments.map {
            when (it) {
                is ExpectedValue<*> -> it.value
                else -> it
            }
        }
        if (actualArguments != normalizedExpected) {
            throw AssertionError("Expected arguments $normalizedExpected but found $actualArguments")
        }
        return this
    }
}
