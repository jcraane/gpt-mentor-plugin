import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.Dimension
import javax.swing.*

fun main() {
    val frame = JFrame("My Swing Panel")

    val promptTextArea = JTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one!"
    ).apply {
        lineWrap = true
        maximumSize = Dimension(800, 150)
    }

    val promptPanel = createVerticalBoxPanel()
    promptPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    createPromptLabelPanel().also { promptPanel.add(it) }
    promptPanel.add(Box.createVerticalStrut(10))
    val promptScrollPane = JScrollPane(promptTextArea).apply {
        maximumSize = Dimension(800, 150)
    }
    promptPanel.add(promptScrollPane)
//    promptPanel.add(promptTextArea)


    val panel = createHorizontalBoxPanel()
    // add button to pnel
    val button = JButton("Click me")
    panel.add(button)
    panel.add(Box.createHorizontalStrut(5).apply { maximumSize = Dimension(5, 5) })
    //add another button
    val button2 = JButton("Click me 2")
    panel.add(button2)

    promptPanel.add(panel)
    frame.add(promptPanel)
    frame.pack()
    frame.setSize(800, 600)
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
}

private fun createVerticalBoxPanel(): JPanel {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    return panel
}

private fun createPromptLabelPanel(): JPanel {
    val panel = createHorizontalBoxPanel()
    panel.add(JLabel("Prompt: "))
    panel.add(Box.createHorizontalGlue())
    return panel
}


private fun createHorizontalBoxPanel(): JPanel {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
    return panel
}

