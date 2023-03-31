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
    GPT_4("gpt-4");
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Model::class)
object ModelSerializer {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Model", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): Model {
        return when (val code = decoder.decodeString()) {
            "gpt-3.5-turbo" -> Model.GPT_3_5_TURBO
            "gpt-4" -> Model.GPT_4
            else -> throw IllegalArgumentException("Unknown Model code: $code")
        }
    }
}
