package dev.jamiecraane.gptmentorplugin.openapi

import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class ChatGptEventSourceListener(
    private val logger: com.intellij.openapi.diagnostic.Logger,
    private val onError: (Response?) -> Unit,
    private val onDataReceived: (String) -> Unit,
) : EventSourceListener() {
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
    }

    override fun onEvent(
        eventSource: EventSource,
        id: String?,
        type: String?,
        data: String,
    ) {
        super.onEvent(eventSource, id, type, data)
        onDataReceived(data)
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        logger.error(t)
        onError(response)
    }
}
