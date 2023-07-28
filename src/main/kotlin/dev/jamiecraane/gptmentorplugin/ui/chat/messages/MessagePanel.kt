package dev.jamiecraane.gptmentorplugin.ui.chat.messages

import com.intellij.util.ui.HtmlPanel
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.Nls
import java.awt.Font

class MessagePanel : HtmlPanel() {


    private var msg = ""

    @Nls
    override fun getBody(): String {
        return if (msg.isNullOrEmpty()) "" else msg
    }
    override fun getBodyFont(): Font {
        return UIUtil.getLabelFont()
    }

    fun updateMessage(updateMessage: String) {
        msg = updateMessage
        update()
    }

    fun appendMessage(newData: String){
        updateMessage(msg + newData)
    }
}