# gpt-mentor-plugin

![Build](https://github.com/jcraane/gpt-mentor-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Verify the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the Plugin ID in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
GPT-Mentor which is powered by Open AI is a plugin that helps you to improve your code. It can explain your code, improve your code, review your code, create unit tests and add comments to your code.

It also enables you to create custom chats. 

The default shortcuts for the standard actions are:
- Explain Code: Ctrl + Alt + Shift + E
- Improve Code: Ctrl + Alt + Shift + I
- Review Code: Ctrl + Alt + Shift + R
- Create Unit Test: Ctrl + Alt + Shift + T
- Add Comments: Ctrl + Alt + Shift + C

Each action uses a custom system prompt to instruct ChatGPT how to behave. Those prompts can be adjusted in the settings of the plugin if required.

The history view displays the chat history. You can also remove messages from the history. 

- Double-click on a chat in the history view to open the chat in the editor. 
- Select one or multiple elements and use the backspace or context menu to delete items from the history
- Rename history items with the context menu or Shift+F6

To start using it create an account and API key at: https://platform.openai.com/account/api-keys  

After you have created an API key you can add it in the settings of the plugin.  

To see the uptime of the OpenAI API visit: https://status.openai.com/uptime
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "gpt-mentor-plugin"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/jcraane/gpt-mentor-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
