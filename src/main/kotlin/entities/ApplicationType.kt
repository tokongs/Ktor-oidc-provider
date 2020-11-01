package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.serializers.ApplicationTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ApplicationTypeSerializer::class)
enum class ApplicationType{
    WEB,
    NATIVE
}