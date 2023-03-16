package com.github.jcraane.gptmentorplugin.ui.history.state

import com.github.jcraane.gptmentorplugin.openapi.JSON
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty

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
    val items: List<HistoryItem>,
)

@Serializable
data class HistoryItem(
    val title: String,
    val messages: List<HistoryMessage>,
)

@Serializable
data class HistoryMessage(
    val role: String,
    val content: String,
)
