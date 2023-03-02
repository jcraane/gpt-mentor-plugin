package com.github.jcraane.gptmentorplugin.configuration

import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import java.awt.Color
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "com.github.jcraane.gptmentorplugin.configuration.GptMentorSettingsState",
    storages = [Storage("GptMentorConfig.xml")]
)
class GptMentorSettingsState : PersistentStateComponent<GptMentorSettingsState> {
//    var openAiApiKey: String by GptMentorSettingsState()
    var openAiApiKey: String = "SECURED"

    override fun getState(): GptMentorSettingsState {
        return this
    }

    override fun loadState(state: GptMentorSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance() = ServiceManager.getService(GptMentorSettingsState::class.java)
    }

    /*operator fun getValue(thisRef: GptMentorSettingsState, property: KProperty<*>): String {
        return GptMentorCredentialsManager.getPassword() ?: ""
    }

    operator fun setValue(thisRef: GptMentorSettingsState, property: KProperty<*>, value: String?) {
        GptMentorCredentialsManager.setPassword(value)
    }*/
}
