package dev.jamiecraane.gptmentorplugin.ui.chat.messages

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JTextArea

class ChatBubble (msg : String, user : Boolean = true): JBPanel<ChatBubble>() {
    val isUser = user
    // responseComponent and questionComponent are mutually exclusive (one will be null)
    private val responseComponent: MessagePanel? = if (!isUser) MessagePanel() else null
    private val questionComponent: JTextArea? = if (isUser) JTextArea() else null


    init {
        val question : String = msg

        isOpaque = true
        background = if (user) JBColor(0xEAEEF7, 0x45494A) else JBColor(0xE0EEF7, 0x2d2f30)
        border = JBUI.Borders.empty(10, 10, 10, 3)
        layout = BorderLayout(JBUI.scale(7), 1)

        val centerPanel = JPanel(VerticalLayout(JBUI.scale(8)))
        centerPanel.isOpaque = false
        centerPanel.border = JBUI.Borders.emptyRight(10)
        centerPanel.add(createContentComponent(question))
        add(centerPanel, BorderLayout.CENTER)

    }

    fun createContentComponent(content :String) : Component {
        val component = if (isUser) questionComponent else responseComponent
        component!! // Assuming that isUser must be true or false making question and response mutually exclusive
        responseComponent?.contentType  = "text/html; charset=UTF-8"
        responseComponent?.updateMessage(content)
        questionComponent?.append(content)
        questionComponent?.lineWrap = true
        questionComponent?.wrapStyleWord = true

        component.isEditable = false
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, java.lang.Boolean.TRUE)
        component.isOpaque = false
        component.border = null
        component.revalidate()
        component.repaint()

        return component
    }

    fun appendMessage(data: String) {
        responseComponent?.appendMessage(data)
    }

}