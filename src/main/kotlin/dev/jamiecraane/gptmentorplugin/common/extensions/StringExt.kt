package dev.jamiecraane.gptmentorplugin.common.extensions

fun String.addNewLinesIfNeeded(numNewLines: Int): String {
    val endIndex = this.indexOfLast { it != '\n' } + 1
    val endNewLines = this.substring(endIndex).count { it == '\n' }
    return if (endNewLines < numNewLines) {
        this + "\n".repeat(numNewLines - endNewLines)
    } else {
        this
    }
}
