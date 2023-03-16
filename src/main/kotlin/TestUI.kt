import com.github.jcraane.gptmentorplugin.openapi.JSON
import com.github.jcraane.gptmentorplugin.ui.history.state.History
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryMessage
import kotlin.reflect.KProperty

fun main() {
    val state = HistoryState2()

    println(state.history)

    state.history = History(
        items = listOf(
            HistoryItem(
                title = "Item 1",
                messages = listOf(
                    HistoryMessage("Sender 1", "Message 1"),
                    HistoryMessage("Recipient 1", "Reply 1"),
                    HistoryMessage("Sender 1", "Message 2"),
                )
            ),
            HistoryItem(
                title = "Item 2",
                messages = listOf(
                    HistoryMessage("Sender 2", "Message 1"),
                    HistoryMessage("Recipient 2", "Reply 1"),
                )
            ),
            HistoryItem(
                title = "Item 3",
                messages = listOf(
                    HistoryMessage("Sender 3", "Message 1"),
                )
            ),
        )
    )

    println(state.history)
}

private class HistoryState2 {
    var jsonBlob: String = JSON.encodeToString(History.serializer(), History(emptyList()))
    var history: History by this

    operator fun getValue(thisRef: Any?, property: KProperty<*>): History {
        return if (jsonBlob.isNotEmpty()) {
            JSON.decodeFromString(History.serializer(), jsonBlob)
        } else {
            History(emptyList())
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: History) {
        jsonBlob = JSON.encodeToString(History.serializer(), value)
    }
}
