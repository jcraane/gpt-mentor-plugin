package dev.jamiecraane.gptmentorplugin.configuration

import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_ADD_DOCS
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_CHAT
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_CREATE_UNIT_TEST
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_EXPLAIN
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_IMPROVE_CODE
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState.Companion.DEFAULT_PROMPT_REVIEW
import dev.jamiecraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextArea
import java.awt.*
import javax.swing.*


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

    private fun createTextArea() = JBTextArea().apply {
        lineWrap = true
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor(Color.BLACK, Color.WHITE), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )
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

        settingsPanel = JPanel()
        settingsPanel.layout = GridBagLayout()
        val c = GridBagConstraints()

        c.weightx = LABEL_WEIGHT
        c.weighty = ROW_WEIGHT
        c.fill = GridBagConstraints.BOTH
        c.anchor = GridBagConstraints.LINE_END
        c.insets = Insets(0, 0, GAP, GAP)

        val prompts = arrayListOf(
            explainCodePrompt,
            createUnitTestPrompt,
            improveCodePrompt,
            reviewCodePrompt,
            addDocsPrompt,
            chatPrompt
        )
        val labels = listOf<Component>(
            JLabel("Explain Code Prompt:", JLabel.TRAILING),
            JLabel("Create Unit Test Prompt:", JLabel.TRAILING),
            JLabel("Improve Code Prompt:", JLabel.TRAILING),
            JLabel("Review Code Prompt:", JLabel.TRAILING),
            JLabel("Add Docs Prompt:", JLabel.TRAILING),
            JLabel("Chat Prompt:", JLabel.TRAILING)
        )

        c.gridy = 0
        c.gridx = 0
        settingsPanel.add(JLabel("OpenAI API Key:", JLabel.TRAILING).apply { preferredSize = Dimension(100, 0) }, c)
        c.gridx = 1
        settingsPanel.add(openAiApiKey, c)

        var gridY = 0
        for (promptIndex in prompts.indices) {
            c.gridy = ++gridY
            settingsPanel.add(labels[promptIndex], c)

            c.gridx = 1
            c.weightx = COMPONENT_WEIGHT
            c.anchor = GridBagConstraints.LINE_START
            c.insets = Insets(0, GAP, GAP, 0)

            settingsPanel.add(prompts[promptIndex], c)

            val resetButton = JButton("Reset")
            resetButton.preferredSize = Dimension(RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)
            resetButton.addActionListener {
                resetPrompt(promptIndex)
            }
            c.gridy = ++gridY
            c.gridx = 1
            c.weightx = RESET_BUTTON_WEIGHT
            c.insets = Insets(0, GAP, GAP, 0)
            val buttonPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(resetButton)
                add(Box.createHorizontalGlue())
            }
            settingsPanel.add(buttonPanel, c)

            c.gridx = 0
            c.weightx = LABEL_WEIGHT
            c.insets = Insets(0, 0, GAP, GAP)
        }

        return settingsPanel
    }

    private fun resetPrompt(i: Int) {
        when (i) {
            0 -> explainCodePrompt.text = DEFAULT_PROMPT_EXPLAIN
            1 -> createUnitTestPrompt.text = DEFAULT_PROMPT_CREATE_UNIT_TEST
            2 -> improveCodePrompt.text = DEFAULT_PROMPT_IMPROVE_CODE
            3 -> reviewCodePrompt.text = DEFAULT_PROMPT_REVIEW
            4 -> addDocsPrompt.text = DEFAULT_PROMPT_ADD_DOCS
            5 -> chatPrompt.text = DEFAULT_PROMPT_CHAT
        }
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
        config.systemPromptReviewCode = reviewCodePrompt.text
        config.systemPromptAddDocs = addDocsPrompt.text
        config.systemPromptChat = chatPrompt.text

        GptMentorCredentialsManager.setPassword(openAiApiKey.text)
    }

    override fun getDisplayName(): String {
        return "GPT-Mentor"
    }

    companion object {
        private const val LABEL_WEIGHT = 0.3
        private const val COMPONENT_WEIGHT = 0.7
        private const val ROW_WEIGHT = 1.0
        private const val GAP = 10
        private const val RESET_BUTTON_WEIGHT = 0.2
        private const val RESET_BUTTON_WIDTH = 60
        private const val RESET_BUTTON_HEIGHT = 30
    }
}
