/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.RoomScope

@ContributesNode(RoomScope::class)
@AssistedInject
/**
 * 角色与权限主页节点。
 */
class RolesAndPermissionsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: RolesAndPermissionsPresenter,
) : Node(buildContext, plugins = plugins), RolesAndPermissionsNavigator {
    /**
     * 主页向上抛出的导航回调。
     */
    interface Callback : Plugin, RolesAndPermissionsNavigator {
        override fun openAdminList()
        override fun openModeratorList()
        override fun openEditPermissions()

        override fun onBackClick() {}
    }

    private val callback: Callback = callback()

    @Stable
    /**
     * 在默认导航回调基础上补充统一的返回逻辑。
     */
    private val navigator = object : RolesAndPermissionsNavigator by callback {
        override fun onBackClick() {
            navigateUp()
        }
    }

    /**
     * 渲染角色与权限主页。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RolesAndPermissionsView(
            state = state,
            rolesAndPermissionsNavigator = navigator,
            modifier = modifier,
        )
    }
}

/**
 * 角色与权限页内部导航接口。
 */
interface RolesAndPermissionsNavigator {
    fun onBackClick() {}
    fun openAdminList() {}
    fun openModeratorList() {}
    fun openEditPermissions() {}
}
