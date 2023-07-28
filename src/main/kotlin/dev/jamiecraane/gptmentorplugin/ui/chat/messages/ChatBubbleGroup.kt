package dev.jamiecraane.gptmentorplugin.ui.chat.messages

import com.intellij.openapi.ui.NullableComponent
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import javax.swing.JPanel


class ChatBubbleGroup : JBPanel<ChatBubbleGroup>(), NullableComponent {

    val myList = JPanel(VerticalLayout(JBUI.scale(10)))
    var lastComponent: ChatBubble? = null

    override fun isNull(): Boolean {
        TODO("Not yet implemented")
    }

    fun add(message_component : ChatBubble) {
        myList.add(message_component)
        lastComponent = message_component
        updateLayout()
        updateUI()
    }

    fun updateLayout(){
        val layout = myList.layout
        val componentCount = myList.componentCount
        for (i in 0 until componentCount) {
            layout.removeLayoutComponent(myList.getComponent(i))
            layout.addLayoutComponent(null, myList.getComponent(i))
        }
    }

    fun newBubble(text: String) {
        ChatBubble(text).also { myList.add(it) }
    }
}