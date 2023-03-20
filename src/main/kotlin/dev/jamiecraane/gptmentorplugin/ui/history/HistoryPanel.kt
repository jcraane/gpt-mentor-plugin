package dev.jamiecraane.gptmentorplugin.ui.history

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryItem
import dev.jamiecraane.gptmentorplugin.ui.history.state.PluginStateHistoryRepository
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.*
import javax.swing.*

/**
 * JPanel that displays a list of history items using a {@link JBList} and a custom {@link ListCellRenderer}.
 * Provides functionality to delete and rename items on right-click and double-click actions, respectively.
 * Implements the {@link HistoryView} interface to receive updates from the {@link HistoryPresenter} which manages the
 * interaction with the {@link PluginStateHistoryRepository} to retrieve and store the history items.
 *
 * @param onChatSelected a lambda function that is called when a history item is double-clicked, and passes the selected
 *                        item as a parameter.
 * @see HistoryView
 * @see HistoryPresenter
 * @see PluginStateHistoryRepository
 */
class HistoryPanel(
    private val mouseAdapter: MouseAdapter = object : MouseAdapter() {},
    private val keyAdapter: KeyAdapter = object : KeyAdapter() {},
    private val onChatSelected: (HistoryItem) -> Unit,
) : JPanel(),
    HistoryView,
    MouseListener by mouseAdapter,
    KeyListener by keyAdapter {
    val presenter = HistoryPresenter(this, PluginStateHistoryRepository())

    private val historyList = JBList<HistoryItem>().apply {
        cellRenderer = HistoryItemRenderer()
        addMouseListener(this@HistoryPanel)
        addKeyListener(this@HistoryPanel)
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (e == null) {
            return
        }

        if (e.clickCount == 2) {
            val selected = historyList.selectedValue
            onChatSelected(selected)
        } else if (SwingUtilities.isRightMouseButton(e)) {
            val selectedIndices = historyList.selectedIndices
            if (selectedIndices.size == 1) {
                val popupMenu = historyList.addPopupMenuWithDeleteAction()

                val renameItem = JMenuItem("Rename")
                renameItem.addActionListener {
                    historyList.showEditTextField(selectedIndices)
                }
                popupMenu.add(renameItem)

                popupMenu.show(e.component, e.x, e.y)
            } else if (selectedIndices.isNotEmpty()) {
                val popupMenu = historyList.addPopupMenuWithDeleteAction()
                popupMenu.show(e.component, e.x, e.y)
            }
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_BACK_SPACE) {
            val selectedValues = historyList.selectedValuesList
            presenter.deleteAll(selectedValues)
        } else if (e?.keyCode == KeyEvent.VK_F6 && e.isShiftDown) {
            historyList.showEditTextField(historyList.selectedIndices)
        }
    }

    private fun JBList<HistoryItem>.addPopupMenuWithDeleteAction(): JPopupMenu {
        val popupMenu = JPopupMenu()
        val deleteItem = JMenuItem("Delete")
        deleteItem.addActionListener {
            val selectedValues = selectedValuesList
            presenter.deleteAll(selectedValues)
        }
        popupMenu.add(deleteItem)
        return popupMenu
    }

    private fun JBList<HistoryItem>.showEditTextField(selectedIndices: IntArray) {
        val selectedBounds = getCellBounds(selectedIndices.first(), selectedIndices.first())
        val selectedItem = selectedValue
        val textField = JTextField(selectedItem.title)
        textField.bounds = selectedBounds
        add(textField)
        textField.requestFocusInWindow()
        textField.caretPosition = textField.text.length
        repaint()

        textField.addActionListener {
            val newValue = textField.text
            presenter.rename(selectedItem, newValue)
            remove(textField)
            repaint()
        }

        textField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                remove(textField)
                repaint()
            }
        })
    }

    init {
        layout = BorderLayout()
        add(historyList, BorderLayout.CENTER)
        presenter.refreshHistory()
    }

    override fun showHistory(history: List<HistoryItem>) {
        historyList.setListData(history.toTypedArray())
    }

    private class HistoryItemRenderer : JBLabel(), ListCellRenderer<HistoryItem> {
        init {
            isOpaque = true
        }

        override fun getListCellRendererComponent(
            list: JList<out HistoryItem>?,
            value: HistoryItem?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean,
        ): Component {
            text = value?.title // use the title property to display the name
            background = if (isSelected) UIManager.getColor("List.selectionBackground") else UIManager.getColor("List.background")
            foreground = if (isSelected) UIManager.getColor("List.selectionForeground") else UIManager.getColor("List.foreground")
            return this
        }
    }
}
