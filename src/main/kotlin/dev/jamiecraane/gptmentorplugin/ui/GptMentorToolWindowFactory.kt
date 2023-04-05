package dev.jamiecraane.gptmentorplugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import dev.jamiecraane.gptmentorplugin.openapi.RealOpenApi
import dev.jamiecraane.gptmentorplugin.ui.chat.ChatPanel
import dev.jamiecraane.gptmentorplugin.ui.history.HistoryPanel
import dev.jamiecraane.gptmentorplugin.ui.main.MainPresenter
import dev.jamiecraane.gptmentorplugin.ui.main.MainView
import dev.jamiecraane.gptmentorplugin.ui.main.Tab
import dev.jamiecraane.gptmentorplugin.ui.util.isInDarkMode
import org.intellij.lang.annotations.Language
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Desktop.*
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTextPane

class GptMentorToolWindowFactory : ToolWindowFactory, MainView {
    private val tabbedPane: JBTabbedPane = JBTabbedPane()
    private val presenter = MainPresenter(this)

    private var helpPane = JTextPane().apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        contentType = "text/html"
        isEditable = false
        addHyperlinkListener { e ->
            presenter.openUrl(e)
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        with(tabbedPane) {
            val chatPanel = ChatPanel(presenter).apply { onAttach(project) }
            addTab(Tab.CHAT.label, chatPanel)
            val historyPanel = HistoryPanel { historyItem ->
                chatPanel.presenter.loadChatFromHistory(historyItem)
            }
            addTab(Tab.HISTORY.label, historyPanel)
            addTab(Tab.HELP.label, createHelpPanel())
            addChangeListener {
                when (selectedIndex) {
                    Tab.CHAT.code -> {
                    }

                    Tab.HISTORY.code -> {
                        historyPanel.presenter.refreshHistory()
                    }

                    Tab.HELP.code -> {
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
                    
                    Each action uses a custom system prompt to instruct ChatGPT how to behave. Those prompts can be adjusted in the settings of the plugin if required.<br><br>

                    The history view displays the chat history. You can also remove messages from the history.<br> 
                    - Double-click on a chat in the history view to open the chat in the editor.<br>
                    - Select one or multiple elements and use the backspace or context menu to delete items from the history<br>
                    - Rename history items with the context menu or Shift+F6<br><br>

                    Right-click on a file in the tree view to add the contents of the file to the chat window.<br><br>
                     
                    Select Commits in the VCS log and use right-click -> Add Commits To Prompt to send the commit messages to the chat window.<br><br>

                    To start using it create an account and API key at: <a href="https://platform.openai.com/account/api-keys">https://platform.openai.com/account/api-keys</a><br><br>
                    
                    After you have created an API key you can add it in the settings of the plugin.<br><br>

                    To see the uptime of the OpenAI API visit: <a href="https://status.openai.com/uptime">https://status.openai.com/uptime</a>
                    </div>
                """.trimIndent()

        helpPane.text = helpText
    }

    private fun createHelpPanel(): Component {
        return JPanel().apply {
            layout = BorderLayout()
            val scrollPane = JBScrollPane(helpPane)
            add(scrollPane, BorderLayout.CENTER)
        }
    }

    override fun selectTab(tab: Tab) {
        tabbedPane.selectedIndex = tab.code
    }

    companion object {
        const val ID = "GPT-Mentor"
        private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(RealOpenApi::class.java)
    }
}
