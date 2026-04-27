/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.leave

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 为离开 Space 页面预览提供样例状态。
 */
class LeaveSpaceStateProvider : PreviewParameterProvider<LeaveSpaceState> {
    override val values: Sequence<LeaveSpaceState>
        get() = sequenceOf(
            aLeaveSpaceState(),
            aLeaveSpaceState(
                spaceName = null,
                selectableSpaceRooms = AsyncData.Success(persistentListOf()),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                displayName = "A long space name that should be truncated",
                                worldReadable = true,
                            ),
                            isLastAdmin = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                joinRule = JoinRule.Private,
                            ),
                            isSelected = false,
                        ),
                    )
                )
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastAdmin = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                joinRule = JoinRule.Private,
                            ),
                            isSelected = true,
                        ),
                    )
                )
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastAdmin = true,
                        ),
                    )
                ),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastAdmin = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(),
                            isLastAdmin = true,
                        ),
                    )
                ),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    List(10) { aSelectableSpaceRoom() }.toImmutableList()
                ),
                leaveSpaceAction = AsyncAction.Loading,
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    List(10) { aSelectableSpaceRoom() }.toImmutableList()
                ),
                leaveSpaceAction = AsyncAction.Failure(Exception("An error")),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Failure(Exception("An error")),
            ),
            aLeaveSpaceState(
                isLastAdmin = true,
            ),
        )
}

/**
 * 构造一份离开 Space 页面样例状态。
 */
fun aLeaveSpaceState(
    spaceName: String? = "Space name",
    isLastAdmin: Boolean = false,
    selectableSpaceRooms: AsyncData<ImmutableList<SelectableSpaceRoom>> = AsyncData.Uninitialized,
    leaveSpaceAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = LeaveSpaceState(
    spaceName = spaceName,
    isLastAdmin = isLastAdmin,
    selectableSpaceRooms = selectableSpaceRooms,
    leaveSpaceAction = leaveSpaceAction,
    eventSink = { }
)

/**
 * 构造一条可选择离开的 Space 房间样例。
 */
fun aSelectableSpaceRoom(
    spaceRoom: SpaceRoom = aSpaceRoom(),
    isLastAdmin: Boolean = false,
    isSelected: Boolean = false,
) = SelectableSpaceRoom(
    spaceRoom = spaceRoom,
    isLastAdmin = isLastAdmin,
    isSelected = isSelected,
)
