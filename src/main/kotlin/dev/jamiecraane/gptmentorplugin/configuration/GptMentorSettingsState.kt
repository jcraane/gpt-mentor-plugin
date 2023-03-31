package dev.jamiecraane.gptmentorplugin.configuration

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState",
    storages = [Storage("GptMentorConfig.xml")]
)
class GptMentorSettingsState private constructor(): PersistentStateComponent<GptMentorSettingsState> {
    var openAiApiKey: String = "SECURED"
    var systemPromptExplainCode: String = DEFAULT_PROMPT_EXPLAIN
    var systemPromptCreateUnitTest: String = DEFAULT_PROMPT_CREATE_UNIT_TEST
    var systemPromptImproveCode: String = DEFAULT_PROMPT_IMPROVE_CODE
    var systemPromptReviewCode: String = DEFAULT_PROMPT_REVIEW
    var systemPromptAddDocs: String = DEFAULT_PROMPT_ADD_COMMENTS
    var systemPromptChat: String = DEFAULT_PROMPT_CHAT

    override fun getState(): GptMentorSettingsState {
        return this
    }

    override fun loadState(state: GptMentorSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance() = ApplicationManager.getApplication().getService(GptMentorSettingsState::class.java)

        const val DEFAULT_PROMPT = "You are an expert AI programmer."

        val DEFAULT_PROMPT_EXPLAIN = """
        You are an expert AI programmer and an expert in explaining code to medior and junior programmers.

        - Explain the code in concise sentences
        - Provide examples when possible to explain what the code does
    """.trimIndent()

        val DEFAULT_PROMPT_REVIEW = """
        You are an expert AI programmer reviewing code written by others. During the review:

        - Summarize what is good about the code
        - Mention where the code can be improved and why if applicable
        - Check if there are potential security issues
        - Check if there are potential performance issues
        - Check if deprecated code is used and suggest alternatives
        - DO NOT EXPLAIN THE CODE
    """.trimIndent()

        val DEFAULT_PROMPT_CREATE_UNIT_TEST = """
        You are an expert AI programmer which uses unit tests to verify behavior.

        - Write unit tests for the obvious cases
        - Write unit tests for the edge cases
        - DO NOT EXPLAIN THE TEST
    """.trimIndent()

        const val DEFAULT_PROMPT_IMPROVE_CODE = DEFAULT_PROMPT

        val DEFAULT_PROMPT_ADD_COMMENTS = """
        You are an expert AI programmer which writes code documentation to explain code to other developers.

        - Add documentation for explaining non-obvious things
        - Do not write comments which are obvious in the code
        - ONLY WRITE DOCS
        - DO NOT EXPLAIN THE CODE
    """.trimIndent()

        val DEFAULT_PROMPT_CHAT = """
            You are an expert AI programmer.
                - Follow the user's requirements carefully & to the letter. 
                - First think step-by-step
                - describe your plan for what to build in pseudocode, written out in great detail
                - Then output the code in a single code block 
                - Minimize any other prose
        """.trimIndent()
    }
}
