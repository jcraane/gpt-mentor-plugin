package dev.jamiecraane.gptmentorplugin.common.extensions

import org.junit.Assert.*
import org.junit.Test

class StringExtKtTest {
    @Test
    fun `test addNewLinesIfNeeded`() {
        val string = "Hello\nWorld\n"
        assertEquals("Hello\nWorld\n\n", string.addNewLinesIfNeeded(3))
        assertEquals("Hello\nWorld\n", string.addNewLinesIfNeeded(2))
        assertEquals("Hello\nWorld\n", string.addNewLinesIfNeeded(1))

        val emptyString = ""
        assertEquals("\n\n\n", emptyString.addNewLinesIfNeeded(3))
        assertEquals("\n\n", emptyString.addNewLinesIfNeeded(2))
        assertEquals("\n", emptyString.addNewLinesIfNeeded(1))
    }
}
