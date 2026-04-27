/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.preferences.impl.R
import io.element.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.element.android.features.rageshake.api.preferences.RageshakePreferencesView
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.components.preferences.PreferenceCategory
import io.element.android.libraries.designsystem.components.preferences.PreferenceDropdown
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.components.preferences.PreferenceSwitch
import io.element.android.libraries.designsystem.components.preferences.PreferenceTextField
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.featureflag.ui.FeatureListView
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import io.element.android.libraries.matrix.api.tracing.TraceLogPack
import io.element.android.libraries.ui.strings.CommonStrings
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
/**
 * 渲染开发者设置页面。
 */
fun DeveloperSettingsView(
    state: DeveloperSettingsState,
    onOpenShowkase: () -> Unit,
    onPushHistoryClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.showLoader) {
        ProgressDialog()
    }
    BackHandler(
        enabled = !state.showLoader,
        onBack = onBackClick,
    )
    PreferencePage(
        modifier = modifier,
        onBackClick = {
            if (!state.showLoader) {
                onBackClick()
            }
        },
        title = stringResource(id = CommonStrings.common_developer_options)
    ) {
        PreferenceCategory(
            title = stringResource(R.string.screen_developer_settings_feature_flags_section_title),
        ) {
            FeatureListContent(state)
        }
        NotificationCategory(onPushHistoryClick)
        ElementCallCategory(state = state)

        PreferenceCategory(title = stringResource(R.string.screen_developer_settings_rust_sdk_section_title)) {
            PreferenceDropdown(
                title = stringResource(R.string.screen_developer_settings_tracing_log_level),
                supportingText = stringResource(R.string.screen_developer_settings_requires_app_reboot),
                selectedOption = state.tracingLogLevel.dataOrNull(),
                options = LogLevelItem.entries.toImmutableList(),
                onSelectOption = { logLevel ->
                    state.eventSink(DeveloperSettingsEvents.SetTracingLogLevel(logLevel))
                }
            )
        }
        PreferenceCategory(title = stringResource(R.string.screen_developer_settings_trace_log_packs_section_title)) {
            Text(
                text = stringResource(R.string.screen_developer_settings_requires_app_reboot),
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            for (logPack in TraceLogPack.entries) {
                PreferenceSwitch(
                    title = traceLogPackTitle(logPack),
                    isChecked = state.tracingLogPacks.contains(logPack),
                    onCheckedChange = { isChecked -> state.eventSink(DeveloperSettingsEvents.ToggleTracingLogPack(logPack, isChecked)) }
                )
            }
        }

        PreferenceCategory(title = stringResource(R.string.screen_developer_settings_showkase_section_title)) {
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.screen_developer_settings_open_showkase_browser))
                },
                onClick = onOpenShowkase
            )
        }
        RageshakePreferencesView(
            state = state.rageshakeState,
        )
        if (state.isEnterpriseBuild) {
            PreferenceCategory(title = stringResource(R.string.screen_developer_settings_theme_section_title)) {
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.screen_developer_settings_change_brand_color))
                    },
                    onClick = {
                        state.eventSink(DeveloperSettingsEvents.SetShowColorPicker(true))
                    }
                )
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.screen_developer_settings_reset_brand_color))
                    },
                    onClick = {
                        state.eventSink(DeveloperSettingsEvents.ChangeBrandColor(null))
                    }
                )
            }
        }
        val crashTestError = stringResource(R.string.screen_developer_settings_crash_test_error)
        PreferenceCategory(title = stringResource(R.string.screen_developer_settings_crash_section_title)) {
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.screen_developer_settings_crash_app))
                },
                onClick = { error(crashTestError) }
            )
        }
        val cache = state.cacheSize
        PreferenceCategory(title = stringResource(R.string.screen_developer_settings_cache_section_title)) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.screen_developer_settings_database_sizes)) },
                supportingContent = {
                    if (state.databaseSizes.isLoading()) {
                        Text(stringResource(R.string.screen_developer_settings_computing))
                    } else {
                        val dbSizes = state.databaseSizes.dataOrNull()
                        if (dbSizes != null && dbSizes.isNotEmpty()) {
                            Column {
                                for ((dbName, size) in dbSizes) {
                                    Text("$dbName: $size")
                                }
                            }
                        } else {
                            Text(stringResource(R.string.screen_developer_settings_unknown))
                        }
                    }
                }
            )
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.screen_developer_settings_vacuum_stores))
                },
                onClick = {
                    state.eventSink(DeveloperSettingsEvents.VacuumStores)
                }
            )
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.screen_developer_settings_clear_cache))
                },
                trailingContent = if (state.cacheSize.isLoading() || state.clearCacheAction.isLoading()) {
                    ListItemContent.Custom {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .progressSemantics()
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    ListItemContent.Text(cache.dataOrNull().orEmpty())
                },
                onClick = {
                    if (state.clearCacheAction.isLoading().not()) {
                        state.eventSink(DeveloperSettingsEvents.ClearCache)
                    }
                }
            )
        }
    }
    ColorPickerDialog(
        show = state.showColorPicker,
        type = ColorPickerType.Classic(
            showAlphaBar = false,
        ),
        onDismissRequest = {
            state.eventSink(DeveloperSettingsEvents.SetShowColorPicker(false))
        },
        onPickedColor = {
            state.eventSink(DeveloperSettingsEvents.ChangeBrandColor(it))
        },
    )
}

@Composable
/**
 * 渲染 Element Call 相关开发者设置分区。
 */
private fun ElementCallCategory(
    state: DeveloperSettingsState,
) {
    PreferenceCategory(title = stringResource(R.string.screen_developer_settings_element_call_section_title)) {
        val callUrlState = state.customElementCallBaseUrlState

        val supportingText = if (callUrlState.baseUrl.isNullOrEmpty()) {
            stringResource(R.string.screen_advanced_settings_element_call_base_url_description)
        } else {
            callUrlState.baseUrl
        }
        PreferenceTextField(
            headline = stringResource(R.string.screen_advanced_settings_element_call_base_url),
            value = callUrlState.baseUrl,
            placeholder = "https://.../room",
            supportingText = supportingText,
            validation = callUrlState.validator,
            onValidationErrorMessage = stringResource(R.string.screen_advanced_settings_element_call_base_url_validation_error),
            displayValue = { value -> !value.isNullOrEmpty() },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false, keyboardType = KeyboardType.Uri),
            onChange = { state.eventSink(DeveloperSettingsEvents.SetCustomElementCallBaseUrl(it)) }
        )
    }
}

@Composable
/**
 * 返回 trace log pack 的显示标题。
 */
private fun traceLogPackTitle(logPack: TraceLogPack): String {
    return when (logPack) {
        TraceLogPack.EVENT_CACHE -> stringResource(R.string.screen_developer_settings_trace_log_pack_event_cache)
        TraceLogPack.SEND_QUEUE -> stringResource(R.string.screen_developer_settings_trace_log_pack_send_queue)
        TraceLogPack.TIMELINE -> stringResource(R.string.screen_developer_settings_trace_log_pack_timeline)
        TraceLogPack.NOTIFICATION_CLIENT -> stringResource(R.string.screen_developer_settings_trace_log_pack_notification_client)
        TraceLogPack.SYNC_PROFILING -> stringResource(R.string.screen_developer_settings_trace_log_pack_sync_profiling)
        TraceLogPack.LATEST_EVENTS -> stringResource(R.string.screen_developer_settings_trace_log_pack_latest_events)
    }
}

@Composable
/**
 * 渲染通知相关开发者分区。
 */
private fun NotificationCategory(onPushHistoryClick: () -> Unit) {
    PreferenceCategory(title = stringResource(id = R.string.screen_notification_settings_title)) {
        ListItem(
            headlineContent = {
                Text(stringResource(R.string.troubleshoot_notifications_entry_point_push_history_title))
            },
            onClick = onPushHistoryClick,
        )
    }
}

@Composable
/**
 * 渲染功能开关列表。
 */
private fun FeatureListContent(
    state: DeveloperSettingsState,
) {
    fun onFeatureEnabled(feature: FeatureUiModel, isEnabled: Boolean) {
        state.eventSink(DeveloperSettingsEvents.UpdateEnabledFeature(feature, isEnabled))
    }

    FeatureListView(
        features = state.features,
        onCheckedChange = ::onFeatureEnabled,
    )
}

@PreviewsDayNight
@Composable
internal fun DeveloperSettingsViewPreview(
    @PreviewParameter(DeveloperSettingsStateProvider::class) state: DeveloperSettingsState
) = ElementPreview {
    DeveloperSettingsView(
        state = state,
        onOpenShowkase = {},
        onPushHistoryClick = {},
        onBackClick = {}
    )
}
