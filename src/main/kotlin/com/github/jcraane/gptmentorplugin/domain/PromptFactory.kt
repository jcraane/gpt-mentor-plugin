package com.github.jcraane.gptmentorplugin.domain

import com.github.jcraane.gptmentorplugin.configuration.GptMentorSettingsState
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest

object PromptFactory {
    private val state: GptMentorSettingsState = GptMentorSettingsState.getInstance()

    fun explain(code: String) = BasicPrompt.ExplainCode(code, state.systemPromptExplainCode)
    fun improve(code: String) = BasicPrompt.ImproveCode(code, state.systemPromptImproveCode)
    fun review(code: String) = BasicPrompt.ReviewCode(code, state.systemPromptReviewCode)
    fun createUnitTest(code: String) = BasicPrompt.CreateUnitTest(code, state.systemPromptCreateUnitTest)
    fun addComments(code: String) = BasicPrompt.AddComments(code, state.systemPromptAddDocs)
    fun chat(messages: List<ChatGptRequest.Message>) = BasicPrompt.Chat(messages, state.systemPromptChat)
    fun promptFromSelection(code: String) = BasicPrompt.PromptFromSelection(code, "")
}
