package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.serializers.GrantTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GrantTypeSerializer::class)
enum class GrantType{
    AUTHORIZATION_CODE,
    IMPLICIT,
    REFRESH_TOKEN,
    PASSWORD,
    CLIENT_CREDENTIALS,
}

