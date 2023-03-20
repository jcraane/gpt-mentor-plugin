package dev.jamiecraane.gptmentorplugin.common.extensions

fun String.addNewLinesIfNeeded(numNewLines: Int): String {
    val currentNewLines = this.count { it == '\n' }
    return if (currentNewLines < numNewLines) {
        this + "\n".repeat(numNewLines - currentNewLines)
    } else {
        this
    }
}
