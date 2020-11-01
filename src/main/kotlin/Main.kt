package dev.kongsvik.ktor_oidc_server

import dev.kongsvik.ktor_oidc_server.entities.*
import dev.kongsvik.ktor_oidc_server.routes.authorize
import dev.kongsvik.ktor_oidc_server.routes.register
import dev.kongsvik.ktor_oidc_server.routes.token
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.oidcServer() {
    install(ContentNegotiation) {
        install(ContentNegotiation) {
            json(json = Json {
                encodeDefaults = false
            })
        }
    }

    routing {
        authorize()
        register()
        token()
    }
}

fun main(){
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
        SchemaUtils.create (Clients)
        SchemaUtils.create (Contacts)
        SchemaUtils.create (RequestUris)
        SchemaUtils.create (RedirectUris)
        SchemaUtils.create (Codes)
    }

    embeddedServer(
        Netty,
        watchPaths = listOf("ktor-oidc-server"),
        port = 8080,
        module = Application::oidcServer
    ).apply {start(wait = true) }
}

