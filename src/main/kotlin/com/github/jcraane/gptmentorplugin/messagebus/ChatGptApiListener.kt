package com.github.jcraane.gptmentorplugin.messagebus

import com.intellij.util.messages.Topic

val CHAT_GPT_ACTION_TOPIC: Topic<ChatGptApiListener> = Topic.create("GptMentorChatGptTopic", ChatGptApiListener::class.java)

interface ChatGptApiListener {
    fun onSuccess(message: String)
    fun onError(message: String)
    fun onLoading()
}
