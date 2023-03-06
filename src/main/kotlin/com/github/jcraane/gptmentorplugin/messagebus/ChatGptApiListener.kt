package com.github.jcraane.gptmentorplugin.messagebus

import com.intellij.util.messages.Topic

val CHAT_GPT_ACTION_TOPIC: Topic<ChatGptApiListener> = Topic.create("GptMentorChatGptTopic", ChatGptApiListener::class.java)

interface ChatGptApiListener {
    fun onPromptReady(message: String)
    fun onExplanationReady(explanation: String)
    fun onError(message: String)
    /**
     * Append explanation to the current explanation. Can be used for streaming backend responses.
     */
    fun onAppendExplanation(explanation: String)
    fun onLoading()
}
