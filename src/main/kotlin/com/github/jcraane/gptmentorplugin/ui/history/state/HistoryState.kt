package com.github.jcraane.gptmentorplugin.ui.history.state

import com.github.jcraane.gptmentorplugin.openapi.JSON
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.ui.chat.ChatContext
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty

/**
 * This class is a persistent state component that stores the history of a certain action.
 *
 * It is annotated with @State, which is used to define the name and the storage location of the state.
 *
 * @property jsonBlob A JSON string that represents the state of the component.
 * @property history A property delegate that initializes the 'history' property with the value of 'this'.
 *
 * This class implements the PersistentStateComponent interface, which allows it to save and load its state.
 *
 * The 'getValue' and 'setValue' operators are used to access the 'history' property.
 *
 * @see PersistentStateComponent
 */
@State(
    name = "HistoryState",
    storages = [Storage("HistoryState.xml")]
)
class HistoryState : PersistentStateComponent<HistoryState> {
    var jsonBlob: String = "{}"

    var history: History by this

    override fun getState(): HistoryState? {
        return this
    }

    override fun loadState(state: HistoryState) {
        XmlSerializerUtil.copyBean(state, this)
    }

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

    companion object {
        fun getInstance() = ServiceManager.getService(HistoryState::class.java)
    }
}

@Serializable
data class History(
    val items: List<HistoryItem> = emptyList(),
)

@Serializable
data class HistoryItem(
    val id: String,
    val title: String,
    val messages: List<HistoryMessage> = emptyList(),
) {
    companion object {
        const val NO_TITLE_PLACEHOLDER = "No title"
        const val MAX_TITLE_LENGTH = 60

        fun from(chatContext: ChatContext): HistoryItem {
            val request = chatContext.chat.createRequest()
            return HistoryItem(
                id = chatContext.chatId,
                title = request.title,
                messages = request.messages.map {
                    HistoryMessage(
                        content = it.content,
                        role = it.role.code,
                    )
                }
            )
        }

        private val ChatGptRequest.title: String
            get() {
                val firstMessage = messages
                    .firstOrNull { it.role == ChatGptRequest.Message.Role.USER }?.content ?: ""
                    .replace("\n", "")
                    .replace(Regex("\\s+"), " ")

                return when {
                    firstMessage.isBlank() -> NO_TITLE_PLACEHOLDER
                    firstMessage.length <= MAX_TITLE_LENGTH -> firstMessage
                    else -> firstMessage.substring(0, MAX_TITLE_LENGTH).padEnd(MAX_TITLE_LENGTH + 3, '.')
                }
            }
    }
}

@Serializable
data class HistoryMessage(
    val role: String,
    val content: String,
)
