package dev.jamiecraane.gptmentorplugin.openapi.request

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ChatGptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Float = 0.8f,
    @SerialName("max_tokens")
    val maxTokens: Int = 1024,
    val stream: Boolean = false,
) {
    @Serializable
    data class Message(
        val role: Role,
        val content: String,
    ) {
        companion object {
            fun newUserMessage(content: String): Message {
                return Message(Role.USER, content)
            }

            fun newSystemMessage(content: String): Message {
                return Message(Role.SYSTEM, content)
            }

            fun newMessage(content: String, role: Role): Message {
                return Message(role, content)
            }
        }
        @Serializable(with = Role.RoleSerializer::class)
        enum class Role(val code: String) {
            USER("user"),
            SYSTEM("system");

            companion object {
                fun fromCode(code: String): Role = values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USER
            }

            @OptIn(ExperimentalSerializationApi::class)
            @Serializer(forClass = Role::class)
            object RoleSerializer : KSerializer<Role> {

                override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Role", PrimitiveKind.STRING)

                override fun serialize(encoder: Encoder, value: Role) {
                    encoder.encodeString(value.code)
                }

                override fun deserialize(decoder: Decoder): Role {
                    return when (val code = decoder.decodeString()) {
                        "user" -> USER
                        "system" -> SYSTEM
                        else -> throw IllegalArgumentException("Unknown Role code: $code")
                    }
                }
            }

        }
    }
}

