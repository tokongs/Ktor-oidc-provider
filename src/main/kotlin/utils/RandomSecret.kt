package dev.kongsvik.ktor_oidc_server.utils

import arrow.fx.IO
import arrow.fx.extensions.fx
import java.security.SecureRandom
import java.util.*

fun generateSecret(byteSize: Int): IO<String> = IO.fx {
    val rand = SecureRandom()
    val bytes = ByteArray(byteSize)
    rand.nextBytes(bytes)
    Base64.getEncoder().withoutPadding().encodeToString(bytes)
}