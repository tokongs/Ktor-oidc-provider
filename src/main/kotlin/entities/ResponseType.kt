package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.serializers.ApplicationTypeSerializer
import dev.kongsvik.ktor_oidc_server.entities.serializers.ResponseTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ResponseTypeSerializer::class)
enum class ResponseType{
    CODE,
    TOKEN
}