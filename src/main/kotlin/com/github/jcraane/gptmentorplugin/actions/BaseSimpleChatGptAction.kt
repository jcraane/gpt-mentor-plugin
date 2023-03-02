package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
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
    )

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val selectedText = editor.selectionModel.selectedText
        println("Selected Text: $selectedText")

        apiJob?.cancel()
        apiJob = scope.launch {
            selectedText?.let { code ->
                doAction(project, code)
            }
        }
    }

    abstract suspend fun doAction(project: Project, code: String)
}
