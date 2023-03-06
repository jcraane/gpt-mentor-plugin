package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.openapi.StreamingResponse
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.github.jcraane.gptmentorplugin.ui.GptMentorToolWindowFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

//todo we need to add append support for steaming data.
abstract class BaseSimpleChatGptAction : AnAction() {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    protected val openApi = RealOpenApi(
        client = HttpClient(),
        okHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build(),
        credentialsManager = GptMentorCredentialsManager,
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

                ToolWindowManager.getInstance(project).getToolWindow(GptMentorToolWindowFactory.ID)?.also { toolWindow ->
                    ApplicationManager.getApplication().invokeLater {
                        toolWindow.show()
                    }
                }

                publishLoading(project)
                publishPrompt(project, prompt.action)
                try {
                    executeStreaming(project, prompt)
                } catch (e: Exception) {
                    publishError(project, e.message ?: "An unknown error occurred")
                }
            }
        }
    }

    private suspend fun executeStreaming(project: Project, prompt: BasicPrompt) {
        publishPrompt(project, prompt.action)
        openApi.executeBasicActionStreaming(prompt)
            .onStart {
                publishExplanation(project, "")
            }
            .onEach {
                println("PLUGIN::: Received: $it")
            }
            .collect {
                when (it) {
                    is StreamingResponse.Data -> appendExplanation(project, it.data)
                    is StreamingResponse.Error -> publishError(project, it.error)
                    StreamingResponse.Done -> {
                        // Do nothing
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

    protected fun appendExplanation(project: Project, message: String) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onAppendExplanation(message)
    }

    protected fun publishError(project: Project, message: String) {
        project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onError(message)
    }


//    abstract suspend fun doAction(project: Project, code: String, prompt: BasicPrompt)
}
