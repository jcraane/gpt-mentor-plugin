package dev.jamiecraane.gptmentorplugin.common

import kotlin.math.ceil

class BasicTokenizer : Tokenizer {
    override fun countTokens(text: String): Int {
        return ceil(text.trim().length / 4.0).toInt()
    }
}
