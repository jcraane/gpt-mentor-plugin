package com.github.jcraane.gptmentorplugin.configuration

import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import javax.swing.JComponent
import javax.swing.JPanel

class GptMentorConfigurable : Configurable {
    private lateinit var settingsPanel: JPanel
    private var openAiApiKey = JBPasswordField()

    override fun createComponent(): JComponent {
        settingsPanel = JPanel()
        return createFromConfig()
    }

    private fun createFromConfig(): JComponent {
        openAiApiKey.text = getPassword()

        settingsPanel = panel {
            row {
                label(text = "OpenAI API Key")
                component(openAiApiKey)
            }
        }
        return settingsPanel
    }

    private fun getPassword() = GptMentorCredentialsManager.getPassword() ?: "YOUR_API_KEY"

    override fun isModified(): Boolean {
        var modified = false
        val text = openAiApiKey.text
        if (text.isNotBlank()) {
            modified = modified || text != getPassword()
        }
        return modified
    }

    override fun apply() {
        if (isModified.not()) {
            return
        }

        GptMentorCredentialsManager.setPassword(openAiApiKey.text)
    }

    override fun getDisplayName(): String {
        return "GPT-Mentor"
    }
}
