package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import io.ktor.client.*
import kotlinx.coroutines.*

class CreateUnitTestAction : AnAction() {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private
    var apiJob: Job? = null

    private val openApi = RealOpenApi(
        HttpClient(),
    )

    override fun actionPerformed(e: AnActionEvent) {
        println("PrintSelectedTextAction.actionPerformed")
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val selectedText = editor.selectionModel.selectedText
        println("Selected Text: $selectedText")

       /* apiJob?.cancel()
        apiJob = scope.launch {
            selectedText?.let {
                val explanation = openApi.getExplanation(it)
                println("Explanation: $explanation")
            }
        }*/

    }

}
