package dev.jamiecraane.gptmentorplugin.common.extensions

import java.awt.Component
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

/**
 * Adds a key listener to this component that calls the specified action when a key is pressed.
 *
 * @param action the action to be performed when a key is pressed
 *
 * @return the key listener that was added to this component. Can be used to remove the listener when it is no longer needed.
 */
fun Component.onKeyPressed(action: (KeyEvent?) -> Unit): KeyListener {
    val listener = object : java.awt.event.KeyAdapter() {
        override fun keyPressed(e: KeyEvent?) {
            action(e)
        }
    }
    this.addKeyListener(listener)
    return listener
}
