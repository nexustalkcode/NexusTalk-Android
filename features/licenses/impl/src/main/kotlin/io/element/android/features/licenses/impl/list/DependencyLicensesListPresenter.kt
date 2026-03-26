/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.licenses.impl.LicensesProvider
import io.element.android.features.licenses.impl.model.DependencyLicenseItem
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.extensions.runCatchingExceptions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 依赖项许可证列表 Presenter
 *
 * 负责处理依赖项许可证列表的业务逻辑，包括：
 * - 从提供者加载许可证列表
 * - 实现过滤器功能
 * - 管理列表状态
 *
 * @property licensesProvider 许可证提供者
 * @see DependencyLicensesListState 许可证列表状态
 */
@Inject
class DependencyLicensesListPresenter(
    private val licensesProvider: LicensesProvider,
) : Presenter<DependencyLicensesListState> {
    /**
     * 创建视图状态
     *
     * @return DependencyLicensesListState 当前列表的状态
     */
    @Composable
    override fun present(): DependencyLicensesListState {
        var licenses by remember {
            mutableStateOf<AsyncData<ImmutableList<DependencyLicenseItem>>>(AsyncData.Loading())
        }
        var filteredLicenses by remember {
            mutableStateOf<AsyncData<ImmutableList<DependencyLicenseItem>>>(AsyncData.Loading())
        }
        var filter by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            runCatchingExceptions {
                licenses = AsyncData.Success(licensesProvider.provides().toImmutableList())
            }.onFailure {
                licenses = AsyncData.Failure(it)
            }
        }
        LaunchedEffect(filter, licenses.dataOrNull()) {
            val data = licenses.dataOrNull()
            val safeFilter = filter.trim()
            if (data != null && safeFilter.isNotEmpty()) {
                filteredLicenses = AsyncData.Success(data.filter {
                    it.safeName.contains(safeFilter, ignoreCase = true) ||
                        it.groupId.contains(safeFilter, ignoreCase = true) ||
                        it.artifactId.contains(safeFilter, ignoreCase = true)
                }.toImmutableList())
            } else {
                filteredLicenses = licenses
            }
        }

        fun handleEvent(event: DependencyLicensesListEvent) {
            when (event) {
                is DependencyLicensesListEvent.SetFilter -> {
                    filter = event.filter
                }
            }
        }

        return DependencyLicensesListState(
            licenses = filteredLicenses,
            filter = filter,
            eventSink = ::handleEvent,
        )
    }
}
