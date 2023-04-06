package dev.jamiecraane.gptmentorplugin.messagebus

import com.intellij.util.messages.Topic
import dev.jamiecraane.gptmentorplugin.ui.main.Tab

val COMMON_ACTIONS_TOPIC: Topic<CommonActions> = Topic.create("GptMentorCommonActionsTopic", CommonActions::class.java)

interface CommonActions {
    fun selectTab(tab: Tab)
}
