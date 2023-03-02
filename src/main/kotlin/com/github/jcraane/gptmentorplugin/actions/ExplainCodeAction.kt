package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.ui.ShowSuggestionDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import io.ktor.client.*
import kotlinx.coroutines.*
import javax.swing.SwingUtilities

class ExplainCodeAction : AnAction() {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private
    var apiJob: Job? = null

    private val openApi = RealOpenApi(
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
                try {
                    val chatGptResponse = openApi.explainCode(code)
                    chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
                        SwingUtilities.invokeLater {
                            ShowSuggestionDialog(project, content).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
