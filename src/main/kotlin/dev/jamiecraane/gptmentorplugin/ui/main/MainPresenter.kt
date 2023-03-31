package dev.jamiecraane.gptmentorplugin.ui.main

import dev.jamiecraane.gptmentorplugin.openapi.RealOpenApi
import java.awt.Desktop
import javax.swing.event.HyperlinkEvent

class MainPresenter(private val mainView: MainView) {
    fun selectTab(tab: Tab) {
        mainView.selectTab(tab)
    }

    fun openUrl(event: HyperlinkEvent) {
        if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(event.url.toURI())
            } catch (ex: Exception) {
                logger.error(ex)
            }
        }
    }

    companion object {
        private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(RealOpenApi::class.java)
    }
}
