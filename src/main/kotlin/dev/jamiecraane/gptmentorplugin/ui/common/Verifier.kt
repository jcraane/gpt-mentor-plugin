package dev.jamiecraane.gptmentorplugin.ui.common

interface Verifier {
    fun verify(text: String): Boolean
}
