package dev.kongsvik.ktor_oidc_server.entities

import java.net.URL

data class IdToken(
    val iss: URL,
    val sub: String,
    val aud: List<String>,
    val exp: Long,
    val iat: Long,
    val auth_time: Long?,
    val nonce: String,
    val acr: String,
    val amr: List<String>,
    val azp: String
);