package dev.jamiecraane.gptmentorplugin.ui.history

data class ChatMessagesViewModel(val messages: List<ChatMessageViewModel>)

/**
 * Represents the view model of a chat message.
 *
 * @property title the title of the chat message.
 * @property id the unique identifier of the chat message.
 */
data class ChatMessageViewModel(
    val id: String,
    val title: String,
) {
}
