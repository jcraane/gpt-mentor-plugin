package dev.jamiecraane.gptmentorplugin.ui.chat.messages

import com.intellij.util.ui.HtmlPanel
import com.intellij.util.ui.UIUtil
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import org.jetbrains.annotations.Nls
import java.awt.Font

class MessagePanel : HtmlPanel() {


    private var msg = ""

    @Nls
    override fun getBody(): String {
        return if (msg.isNullOrEmpty()) "" else convertMarkdownToHTML(msg)
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

    private fun convertMarkdownToHTML(markdown: String) : String{
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parse(markdown)
        val renderer: HtmlRenderer = HtmlRenderer.builder().build()
        return renderer.render(document)
    }
}