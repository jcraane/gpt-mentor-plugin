package dev.jamiecraane.gptmentorplugin.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ModelSerializer::class)
enum class Model(val code: String) {
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    GPT_4("gpt-4"),
    GPT_4_32K("gpt-4-32k");

    companion object {
        fun fromCode(code: String): Model =
            values().firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown Model code: $code")
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Model::class)
object ModelSerializer {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Model", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): Model {
        val code = decoder.decodeString()
        return Model.fromCode(code)
    }
}
