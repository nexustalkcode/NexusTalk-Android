/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package extension

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Checks that Element Call embedded web build output exists (configuration-cache friendly).
 */
abstract class ValidateElementCallEmbeddedDistTask : DefaultTask() {
    @get:Input
    abstract val indexHtmlPath: Property<String>

    @TaskAction
    fun validate() {
        val index = File(indexHtmlPath.get())
        check(index.isFile) {
            buildString {
                appendLine("Missing Element Call embedded web build (index.html not found).")
                appendLine("Expected: ${index.absolutePath}")
                appendLine(
                    "Run: cd element-call && corepack prepare yarn@4.7.0 --activate && yarn install && " +
                        "(cd vendor/matrix-js-sdk && yarn install && yarn build); " +
                        "then: yarn exec vite build --config vite-embedded.config.ts"
                )
            }
        }
    }
}
