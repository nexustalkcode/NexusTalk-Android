/*
 * Copyright (c) 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.api.badge

import android.content.Context
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BadgeManagerTest {
    @Test
    fun `badge content text uses unread message plural resource`() {
        val context = RuntimeEnvironment.getApplication() as Context

        // 这里验证 BadgeManager 不再依赖运行时资源名查找；只要资源被错误移除或不可见，
        // 静态 R 引用会在编译或测试阶段暴露问题。
        assertThat(BadgeManager.getBadgeContentText(context, 1)).isEqualTo("1 unread notified message")
        assertThat(BadgeManager.getBadgeContentText(context, 2)).isEqualTo("2 unread notified messages")
    }
}
