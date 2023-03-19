package dev.jamiecraane.gptmentorplugin.openapi

import kotlinx.serialization.json.Json

val JSON = Json {
    encodeDefaults = true
    coerceInputValues = true
    ignoreUnknownKeys = true
}
