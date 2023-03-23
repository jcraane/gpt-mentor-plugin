package dev.jamiecraane.gptmentorplugin.common

interface Tokenizer {
    fun countTokens(text: String): Int
}
