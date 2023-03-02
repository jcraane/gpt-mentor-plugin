package com.github.jcraane.gptmentorplugin.security

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe


object GptMentorCredentialsManager {
    private const val SERVICE_NAME = "MyPluginPasswordManager"

    fun getPassword(): String? {
        return try {
            PasswordSafe.instance.getPassword(openAiApiKey)
        } catch (e: Exception) {
            null
        }
    }

    fun setPassword(password: String?) {
        PasswordSafe.instance.setPassword(openAiApiKey, password)
    }

    const val OPEN_AI_API_KEY = "OPEN_AI_API_KEY"
    private val openAiApiKey = CredentialAttributes(
        SERVICE_NAME,
        OPEN_AI_API_KEY,
    )
}
