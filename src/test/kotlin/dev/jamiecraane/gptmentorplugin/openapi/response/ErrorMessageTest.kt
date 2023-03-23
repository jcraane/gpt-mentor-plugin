package dev.jamiecraane.gptmentorplugin.openapi.response

import org.junit.Assert.*
import org.junit.Test

class ErrorMessageTest {
    @Test
    fun `error response with null error object is created successfully`() {
        val errorResponse = ErrorResponse(null)
        assert(errorResponse.error == null)
    }

    @Test
    fun `error response with non-null error object is created successfully`() {
        val errorMessage = ErrorMessage("test message", "test type", "test param", "test code")
        val errorResponse = ErrorResponse(errorMessage)
        assert(errorResponse.error == errorMessage)
    }

    @Test
    fun `error response with empty message is created successfully`() {
        val errorMessage = ErrorMessage("", "test type", "test param", "test code")
        val errorResponse = ErrorResponse(errorMessage)
        assert(errorResponse.error == errorMessage)
    }

    @Test
    fun `error response with empty type is created successfully`() {
        val errorMessage = ErrorMessage("test message", "", "test param", "test code")
        val errorResponse = ErrorResponse(errorMessage)
        assert(errorResponse.error == errorMessage)
    }

    @Test
    fun `error response with empty param is created successfully`() {
        val errorMessage = ErrorMessage("test message", "test type", "", "test code")
        val errorResponse = ErrorResponse(errorMessage)
        assert(errorResponse.error == errorMessage)
    }

    @Test
    fun `error response with empty code is created successfully`() {
        val errorMessage = ErrorMessage("test message", "test type", "test param", "")
        val errorResponse = ErrorResponse(errorMessage)
        assert(errorResponse.error == errorMessage)
    }

}
