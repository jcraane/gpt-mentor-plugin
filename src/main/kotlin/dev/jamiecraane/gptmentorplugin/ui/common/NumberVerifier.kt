package dev.jamiecraane.gptmentorplugin.ui.common

class NumberVerifier(private val decimalField: Boolean = false) : Verifier {
    override fun verify(text: String): Boolean {
        if (text.isEmpty()) {
            return true
        }
        if (decimalField) {
            return text.matches(Regex("^[-]?\\d*\\.?\\d+\\.?$"))
        } else {
            return text.matches(Regex("^\\d+$"))
        }
    }
}
