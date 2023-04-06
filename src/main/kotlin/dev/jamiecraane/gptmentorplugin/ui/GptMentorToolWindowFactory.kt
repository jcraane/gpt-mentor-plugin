package dev.jamiecraane.gptmentorplugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.*
import dev.jamiecraane.gptmentorplugin.messagebus.COMMON_ACTIONS_TOPIC
import dev.jamiecraane.gptmentorplugin.messagebus.CommonActions
import dev.jamiecraane.gptmentorplugin.openapi.RealOpenApi
import dev.jamiecraane.gptmentorplugin.ui.chat.ChatPanel
import dev.jamiecraane.gptmentorplugin.ui.history.HistoryPanel
import dev.jamiecraane.gptmentorplugin.ui.main.MainPresenter
import dev.jamiecraane.gptmentorplugin.ui.main.MainView
import dev.jamiecraane.gptmentorplugin.ui.main.Tab
import dev.jamiecraane.gptmentorplugin.ui.util.isInDarkMode
import org.intellij.lang.annotations.Language
import java.awt.BorderLayout
import java.awt.Desktop.*
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextPane

//todo Refactor in a proper GptMentorToolWindow class decoupled from the factory. See tool_window project
class GptMentorToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val presenter = MainPresenter()
        presenter.onAttach(project)

        val helpPane = JTextPane().apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            contentType = "text/html"
            isEditable = false
            addHyperlinkListener { e ->
                presenter.openUrl(e)
            }
        }

        val contentFactory = ContentFactory.SERVICE.getInstance()

        toolWindow.contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun selectionChanged(event: ContentManagerEvent) {
                println("selectioChanged")
                when (event.content?.displayName) {
                    Tab.CHAT.label -> {
                    }

                    Tab.HISTORY.label -> {
                        (event.content.component as HistoryPanel).presenter.refreshHistory()
                    }

                    Tab.HELP.label -> {
                        updateText(helpPane)
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        })

        val chatPanel = ChatPanel(presenter).apply { onAttach(project) }
        val historyPanel = HistoryPanel { historyItem ->
            chatPanel.presenter.loadChatFromHistory(historyItem)
        }
        val helpPanel = createHelpPanel(helpPane)

        val chatContent = contentFactory.createContent(chatPanel, Tab.CHAT.label, false)
        toolWindow.contentManager.addContent(chatContent)

        val historyContent = contentFactory.createContent(historyPanel, Tab.HISTORY.label, false)
        historyPanel.presenter.refreshHistory()
        toolWindow.contentManager.addContent(historyContent)

        val helpContent = contentFactory.createContent(helpPanel, Tab.HELP.label, false)
        updateText(helpPane)
        toolWindow.contentManager.addContent(helpContent)

        project.messageBus.connect().subscribe(COMMON_ACTIONS_TOPIC, object : CommonActions {
            override fun selectTab(tab: Tab) {
                when (tab) {
                    Tab.CHAT -> {
                        toolWindow.contentManager.setSelectedContent(chatContent)
                    }

                    Tab.HISTORY -> {
                        toolWindow.contentManager.setSelectedContent(historyContent)
                    }

                    Tab.HELP -> {
                        toolWindow.contentManager.setSelectedContent(helpContent)
                    }
                }
            }
        })
    }

    private fun updateText(helpPane: JTextPane) {
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

    private fun createHelpPanel(helpPane: JTextPane): JComponent {
        return JPanel().apply {
            layout = BorderLayout()
            val scrollPane = JBScrollPane(helpPane)
            add(scrollPane, BorderLayout.CENTER)
        }
    }

    companion object {
        const val ID = "GPT-Mentor"
        private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(RealOpenApi::class.java)
    }
}
