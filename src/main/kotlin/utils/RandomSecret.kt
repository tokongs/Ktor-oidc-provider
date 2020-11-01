package dev.kongsvik.ktor_oidc_server.utils

import java.security.SecureRandom
import java.util.*

fun generateSecret(byteSize: Int): String {
    val rand = SecureRandom();
    var bytes = ByteArray(byteSize)
    rand.nextBytes(bytes)
    val encoder = Base64.getEncoder().withoutPadding()
    return encoder.encodeToString(bytes)
}