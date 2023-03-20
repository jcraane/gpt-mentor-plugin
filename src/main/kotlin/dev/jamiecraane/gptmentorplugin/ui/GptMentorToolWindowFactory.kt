package dev.jamiecraane.gptmentorplugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.UIUtil
import dev.jamiecraane.gptmentorplugin.ui.chat.ChatPanel
import dev.jamiecraane.gptmentorplugin.ui.history.HistoryPanel
import dev.jamiecraane.gptmentorplugin.ui.util.isInDarkMode
import org.intellij.lang.annotations.Language
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Desktop.*
import java.io.IOException
import java.net.URISyntaxException
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class GptMentorToolWindowFactory : ToolWindowFactory {
    private var helpPane = JTextPane().apply {
        contentType = "text/html"
        isEditable = false
        addHyperlinkListener { e ->
            if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    getDesktop().browse(e.url.toURI())
                } catch (ex: IOException) {
                    // Handle exception
                } catch (ex: URISyntaxException) {
                }
            }
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        val tabbedPane = JBTabbedPane().apply {
            val chatPanel = ChatPanel().apply { onAttach(project) }
            addTab("Chat", chatPanel)
            val historyPanel = HistoryPanel { historyItem ->
                chatPanel.presenter.loadChatFromHistory(historyItem)
                this.selectedIndex = TAB_CHAT
            }
            addTab("History", historyPanel)
            addTab("Help", createHelpPanel())
            addChangeListener {
                when (selectedIndex) {
                    TAB_CHAT -> {
                    }

                    TAB_HISTORY -> {
                        historyPanel.presenter.refreshHistory()
                    }

                    TAB_HELP -> {
                        updateText()
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        val content: Content = contentFactory.createContent(tabbedPane, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun updateText() {
        val helpTextColor = if (isInDarkMode()) {
            "#dddddd"
        } else {
            "#111111"
        }

        @Language("HTML")
        val helpText = """
                    <div style="font-family: Consolas; font-size: 12px; color: $helpTextColor;">
                    GPT-Mentor which is powered by Open AI is a plugin that helps you to improve your code. It can explain your code, 
                    improve your code, review your code, create unit tests and add comments to your code.<br><br>
                    It also enables you to create custom chats.<br><br>
                    The default shortcuts for the standard actions are:.<br><br>
                    - Explain Code: Ctrl + Alt + Shift + E<br>
                    - Improve Code: Ctrl + Alt + Shift + I<br>
                    - Review Code: Ctrl + Alt + Shift + R<br>
                    - Create Unit Test: Ctrl + Alt + Shift + T<br>
                    - Add Comments: Ctrl + Alt + Shift + C<br><br>

                    The history view displays the chat history. You can also remove messages from the history. Double click on a chat in the history view to open the chat in the editor.<br><br>

                    To start using it create an account and API key at: <a href="https://platform.openai.com/account/api-keys">https://platform.openai.com/account/api-keys</a><br><br>
                    
                    After you have created an API key you can add it in the settings of the plugin.<br><br>

                    To see the uptime of the OpenAI API visit: <a href="https://status.openai.com/uptime">https://status.openai.com/uptime</a>
                    </div>
                """.trimIndent()

        helpPane.text = helpText
    }

    private fun createHelpPanel(): Component {
        return JPanel().apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            layout = BorderLayout()

            val scrollPane = JBScrollPane(helpPane)
            add(scrollPane, BorderLayout.CENTER)

            UIUtil.isUnderDarcula()
        }
    }

    companion object {
        const val ID = "GPT-Mentor"
        private const val TAB_CHAT = 0
        private const val TAB_HISTORY = 1
        private const val TAB_HELP = 2
    }
}
