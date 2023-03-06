package com.github.jcraane.gptmentorplugin.openapi

import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class ChatGptEventSourceListener(private val onDataReceived: (String) -> Unit,) : EventSourceListener() {
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
    }

    override fun onEvent(
        eventSource: EventSource,
        id: String?,
        type: String?,
        data: String
    ) {
        super.onEvent(eventSource, id, type, data)
        onDataReceived(data)
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        super.onFailure(eventSource, t, response)
    }
}
