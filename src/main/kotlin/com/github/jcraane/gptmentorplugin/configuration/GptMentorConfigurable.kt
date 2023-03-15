package com.github.jcraane.gptmentorplugin.configuration

import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

class GptMentorConfigurable : Configurable {
    private lateinit var settingsPanel: JPanel
    private var openAiApiKey = JBPasswordField()

    private var explainCodePrompt = createTextArea()
    private var createUnitTestPrompt = createTextArea()
    private var improveCodePrompt = createTextArea()
    private var reviewCodePrompt = createTextArea()
    private var addDocsPrompt = createTextArea()
    private var chatPrompt = createTextArea()

    private val config: GptMentorSettingsState = GptMentorSettingsState.getInstance()

    private fun createTextArea()= JBTextArea().apply {
        lineWrap = true
    }


    override fun createComponent(): JComponent {
        settingsPanel = JPanel()
        return createFromConfig()
    }

    private fun createFromConfig(): JComponent {
        openAiApiKey.text = getPassword()
        explainCodePrompt.text = config.systemPromptExplainCode
        createUnitTestPrompt.text = config.systemPromptCreateUnitTest
        improveCodePrompt.text = config.systemPromptImproveCode
        reviewCodePrompt.text = config.systemPromptReviewCode
        addDocsPrompt.text = config.systemPromptAddDocs
        chatPrompt.text = config.systemPromptChat

        settingsPanel = panel {
            row {
                label(text = "OpenAI API Key")
                component(openAiApiKey)
            }
            row {
                label(text = "Explain Code Prompt")
                component(explainCodePrompt)
            }
            row {
                label(text = "Create Unit Test Prompt")
                component(createUnitTestPrompt)
            }
            row {
                label(text = "Improve Code Prompt")
                component(improveCodePrompt)
            }
            row {
                label(text = "Review Code Prompt")
                component(reviewCodePrompt)
            }
            row {
                label(text = "Add Docs Prompt")
                component(addDocsPrompt)
            }
            row {
                label(text = "Chat Prompt")
                component(chatPrompt)
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

        modified = modified || explainCodePrompt.text != config.systemPromptExplainCode
        modified = modified || createUnitTestPrompt.text != config.systemPromptCreateUnitTest
        modified = modified || improveCodePrompt.text != config.systemPromptImproveCode
        modified = modified || reviewCodePrompt.text != config.systemPromptReviewCode
        modified = modified || addDocsPrompt.text != config.systemPromptAddDocs
        modified = modified || chatPrompt.text != config.systemPromptChat

        return modified
    }

    override fun apply() {
        if (isModified.not()) {
            return
        }

        config.systemPromptExplainCode = explainCodePrompt.text
        config.systemPromptCreateUnitTest = createUnitTestPrompt.text
        config.systemPromptImproveCode = improveCodePrompt.text
        config.systemPromptImproveCode = reviewCodePrompt.text
        config.systemPromptAddDocs = addDocsPrompt.text
        config.systemPromptChat = chatPrompt.text

        GptMentorCredentialsManager.setPassword(openAiApiKey.text)
    }

    override fun getDisplayName(): String {
        return "GPT-Mentor"
    }
}
