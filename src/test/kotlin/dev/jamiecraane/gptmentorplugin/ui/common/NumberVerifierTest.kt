package dev.jamiecraane.gptmentorplugin.ui.common

import org.junit.Assert.*
import org.junit.Test

class NumberVerifierTest {
    @Test
    fun testVerifyValidInputDecimal() {
        val verifier = NumberVerifier(decimalField = true)
        assertTrue(verifier.verify("123"))
        assertTrue(verifier.verify("3.14159"))
        assertTrue(verifier.verify("0.01"))
        assertTrue(verifier.verify(".123"))
        assertTrue(verifier.verify("1."))
    }

    @Test
    fun testVerifyInvalidInputDecimal() {
        val verifier = NumberVerifier(decimalField = true)
        assertFalse(verifier.verify("abc"))
        assertFalse(verifier.verify("1.2.3"))
        assertFalse(verifier.verify("1..23"))
        assertFalse(verifier.verify("."))
        assertFalse(verifier.verify("1.2.3."))
        assertFalse(verifier.verify("1 2 3"))
    }

    @Test
    fun testVerifyValidInput() {
        val verifier = NumberVerifier(decimalField = false)
        assertTrue(verifier.verify("123"))
        assertTrue(verifier.verify("4567890"))
    }

    @Test
    fun testVerifyInvalidInput() {
        val verifier = NumberVerifier(decimalField = false)
        assertFalse(verifier.verify("abc"))
        assertFalse(verifier.verify("1.23"))
        assertFalse(verifier.verify(".123"))
        assertFalse(verifier.verify("1."))
        assertFalse(verifier.verify("1 2 3"))
    }
}
