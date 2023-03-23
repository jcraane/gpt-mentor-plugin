package dev.jamiecraane.gptmentorplugin.common

class BasicTokenizer : Tokenizer {
    override fun countTokens(text: String): Int {
        return Math.ceil(text.trim().length / 4.0).toInt()
    }
}
