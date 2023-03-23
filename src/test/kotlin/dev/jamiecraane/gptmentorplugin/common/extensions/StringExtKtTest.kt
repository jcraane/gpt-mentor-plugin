package dev.jamiecraane.gptmentorplugin.common.extensions

import org.junit.Assert.*
import org.junit.Test

class StringExtKtTest {
    @Test
    fun `addNewLinesIfNeeded appends newlines when needed`() {
        val str = "Hello"
        assert(str.addNewLinesIfNeeded(1) == "Hello\n")
        assert(str.addNewLinesIfNeeded(2) == "Hello\n\n")
        assert(str.addNewLinesIfNeeded(3) == "Hello\n\n\n")
    }

    @Test
    fun `addNewLinesIfNeeded does not change string when no newlines needed`() {
        val str = "Hello\nWorld\n"
        assert(str.addNewLinesIfNeeded(0) == "Hello\nWorld\n")
        assert(str.addNewLinesIfNeeded(1) == "Hello\nWorld\n")
        assert(str.addNewLinesIfNeeded(2) == "Hello\nWorld\n\n")
    }

    @Test
    fun `addNewLinesIfNeeded works with empty string`() {
        val str = ""
        assert(str.addNewLinesIfNeeded(0) == "")
        assert(str.addNewLinesIfNeeded(1) == "\n")
        assert(str.addNewLinesIfNeeded(2) == "\n\n")
    }
}
