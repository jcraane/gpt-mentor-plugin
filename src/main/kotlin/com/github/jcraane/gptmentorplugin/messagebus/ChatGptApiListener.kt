package com.github.jcraane.gptmentorplugin.messagebus

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.intellij.util.messages.Topic

val CHAT_GPT_ACTION_TOPIC: Topic<ChatGptApiListener> = Topic.create("GptMentorChatGptTopic", ChatGptApiListener::class.java)

interface ChatGptApiListener {
    /**
     * Append explanation to the current explanation. Can be used for streaming backend responses.
     */
    fun onNewPrompt(prompt: BasicPrompt)

    /**
     * An item loaded from history will result in a chat which the user can continue.
     */
    fun onHistoryItemLoaded(historyItem: HistoryItem)
}
