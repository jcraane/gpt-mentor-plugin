package dev.jamiecraane.gptmentorplugin.ui.main

class MainPresenter(private val mainView: MainView) {
    fun selectTab(tab: Tab) {
        mainView.selectTab(tab)
    }
}
