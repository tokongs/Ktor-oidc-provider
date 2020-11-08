package dev.kongsvik.ktor_oidc_server.routes

import arrow.fx.typeclasses.Async
import dev.kongsvik.ktor_oidc_server.entities.*
import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.SecureRandom
import java.util.*

fun Routing.register() {

    route("/register") {
        post {
            val test = Either
            val body = call.receive<ClientRegistrationPostBody>()
            val client = createClientFromPostBody(body);
            val response = clientToResponseBody(client)
            call.respond(response)
        }

        get("/{clientId}") {
            val clientId = call.parameters["clientId"]

            if(clientId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val client = transaction { ClientEntity.findById(clientId.toLong())?.toClient() }
            if(client == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val response = clientToResponseBody(client)
            call.respond(response)
        }

        patch("/{clientId}") {
            val clientId = call.parameters["clientId"]

        }
    }
}
