package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import io.ktor.client.*
import kotlinx.coroutines.*

abstract class BaseSimpleChatGptAction : AnAction() {
    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    protected var apiJob: Job? = null

    protected val openApi = RealOpenApi(
        HttpClient(),
        GptMentorCredentialsManager,
    )

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val selectedText = editor.selectionModel.selectedText

        apiJob?.cancel()
        apiJob = scope.launch {
            selectedText?.let { code ->
                val prompt = createPrompt(code)
                publishLoading(project)
                publishPrompt(project, prompt.action)
                try {
                    doAction(project, code, prompt)
                } catch (e: Exception) {
                    publishError(project, e.message ?: "An unknown error occurred")
                }
            }
        }
    }

    abstract protected fun createPrompt(code: String): BasicPrompt

    protected fun publishLoading(project: Project) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onLoading()
    }

    protected fun publishPrompt(project: Project, message: String) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onPromptReady(message)
    }

    protected fun publishExplanation(project: Project, message: String) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onExplanationReady(message)
    }

    protected fun publishError(project: Project, message: String) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onError(message)
    }


    abstract suspend fun doAction(project: Project, code: String, prompt: BasicPrompt)
}
