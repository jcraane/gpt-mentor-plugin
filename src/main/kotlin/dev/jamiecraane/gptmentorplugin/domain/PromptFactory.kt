package dev.jamiecraane.gptmentorplugin.domain

import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest

class PromptFactory(private val state: GptMentorSettingsState) {
    fun explain(code: String) = BasicPrompt.ExplainCode(code, state.systemPromptExplainCode)
    fun improve(code: String) = BasicPrompt.ImproveCode(code, state.systemPromptImproveCode)
    fun review(code: String) = BasicPrompt.ReviewCode(code, state.systemPromptReviewCode)
    fun createUnitTest(code: String) = BasicPrompt.CreateUnitTest(code, state.systemPromptCreateUnitTest)
    fun addComments(code: String) = BasicPrompt.AddComments(code, state.systemPromptAddDocs)
    fun chat(messages: List<ChatGptRequest.Message>) = BasicPrompt.Chat(messages, state.systemPromptChat)
    fun promptFromSelection(code: String) = BasicPrompt.PromptFromSelection(code, "")
}
