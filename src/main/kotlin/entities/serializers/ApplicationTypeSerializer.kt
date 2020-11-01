package dev.kongsvik.ktor_oidc_server.entities.serializers

import dev.kongsvik.ktor_oidc_server.entities.ApplicationType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ApplicationTypeSerializer : KSerializer<ApplicationType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ApplicationType) = encoder.encodeString(value.toString().toLowerCase())
    override fun deserialize(decoder: Decoder): ApplicationType = ApplicationType.valueOf(decoder.decodeString().toUpperCase())
}