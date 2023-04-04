package dev.jamiecraane.gptmentorplugin.ui.common

import com.intellij.ui.components.JBTextField
import java.awt.event.KeyEvent

class ConstraintTextField(private val verifier: Verifier = NumberVerifier(decimalField = false)) : JBTextField() {
    override fun processKeyEvent(e: KeyEvent) {
        val c = e.keyChar
        val text = text + c
        if (verifier.verify(text)) {
            super.processKeyEvent(e)
        } else {
            e.consume()
        }
    }
}
