package dev.jamiecraane.gptmentorplugin.common

class UUIDIdGenerator : IdGenerator {
    override fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
