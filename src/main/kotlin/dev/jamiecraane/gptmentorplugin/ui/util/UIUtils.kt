package dev.jamiecraane.gptmentorplugin.ui.util

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.ui.UIUtil

fun isInDarkMode(): Boolean {
    return if (SystemInfo.isMac) {
        // On macOS, the dark mode setting is global and affects all applications
        if (UIUtil.isUnderDarcula()) {
            true
        } else {
            false
        }
    } else {
        // On other platforms, the dark mode setting is per-application
        if (UIUtil.isUnderDarcula()) {
            true
        } else {
            false
        }
    }
}
