package dev.jamiecraane.gptmentorplugin.ui.main

enum class Tab(
    val code: Int,
    val label: String,
) {
    CHAT(0, "Chat"),
    HISTORY(1, "History"),
    HELP(2, "Help"),
}
