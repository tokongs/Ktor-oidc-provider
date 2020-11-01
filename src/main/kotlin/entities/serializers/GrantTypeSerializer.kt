package dev.kongsvik.ktor_oidc_server.entities.serializers

import dev.kongsvik.ktor_oidc_server.entities.GrantType
import dev.kongsvik.ktor_oidc_server.entities.ResponseType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object GrantTypeSerializer : KSerializer<GrantType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: GrantType) = encoder.encodeString(value.toString().toLowerCase())
    override fun deserialize(decoder: Decoder): GrantType = GrantType.valueOf(decoder.decodeString().toUpperCase())
}