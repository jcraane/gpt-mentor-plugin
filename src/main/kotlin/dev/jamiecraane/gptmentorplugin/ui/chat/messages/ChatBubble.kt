package dev.jamiecraane.gptmentorplugin.ui.chat.messages

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JEditorPane
import javax.swing.JPanel

class ChatBubble (msg : String, me : Boolean = true): JBPanel<ChatBubble>() {
    private val component = MessagePanel()



    init {
        var question : String = msg

        isOpaque = true
        background = if (me) JBColor(0xEAEEF7, 0x45494A) else JBColor(0xE0EEF7, 0x2d2f30 /*2d2f30*/)
        border = JBUI.Borders.empty(10, 10, 10, 3)
        layout = BorderLayout(JBUI.scale(7), 1)

        val centerPanel = JPanel(VerticalLayout(JBUI.scale(8)))
        centerPanel.isOpaque = false
        centerPanel.border = JBUI.Borders.emptyRight(10)
        centerPanel.add(createContentComponent(question))
        add(centerPanel, BorderLayout.CENTER)

    }

    fun createContentComponent(content :String) : Component {
        component.isEditable = false
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, java.lang.Boolean.TRUE)
        component.contentType = "text/html; charset=UTF-8"
        component.isOpaque = false
        component.border = null

        component.updateMessage(content)
        component.revalidate()
        component.repaint()
        return component
    }

    fun appendMessage(data: String) {
        component.updateMessage(component.text + data)
    }

}