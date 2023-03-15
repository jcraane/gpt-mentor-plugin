package com.github.jcraane.gptmentorplugin.configuration

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

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
    var systemPromptExplainCode: String = BasicPrompt.ExplainCode("").systemPrompt
    var systemPromptCreateUnitTest: String = BasicPrompt.CreateUnitTest("").systemPrompt
    var systemPromptImproveCode: String = BasicPrompt.ImproveCode("").systemPrompt
    var systemPromptReviewCode: String = BasicPrompt.ReviewCode("").systemPrompt
    var systemPromptAddDocs: String = BasicPrompt.AddComments("").systemPrompt
    var systemPromptChat: String = BasicPrompt.Chat(emptyList()).systemPrompt

    override fun getState(): GptMentorSettingsState {
        return this
    }

    override fun loadState(state: GptMentorSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance() = ServiceManager.getService(GptMentorSettingsState::class.java)
    }
}
