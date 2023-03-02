package com.github.jcraane.gptmentorplugin.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
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
    val maxTokens: Int = 1024,
) {
    @Serializable
    data class Message(
        val role: Role,
        val content: String,
    ) {
        @Serializable(with = Role.RoleSerializer::class)
        enum class Role(val code: String) {
            USER("user"),
            SYSTEM("system");

            @OptIn(ExperimentalSerializationApi::class)
            @Serializer(forClass = Role::class)
            object RoleSerializer : KSerializer<Role> {

                override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Role", PrimitiveKind.STRING)

                override fun serialize(encoder: Encoder, value: Role) {
                    encoder.encodeString(value.code)
                }

                override fun deserialize(decoder: Decoder): Role {
                    return when (val code = decoder.decodeString()) {
                        "user" -> Role.USER
                        "system" -> Role.SYSTEM
                        else -> throw IllegalArgumentException("Unknown Role code: $code")
                    }
                }
            }

        }
    }
}

