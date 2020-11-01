package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.serializers.URISerializer
import dev.kongsvik.ktor_oidc_server.entities.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.net.URI
import java.time.LocalDateTime
import java.util.*

object Clients : LongIdTable("Clients", "clientId") {
    val tokenEndpointAuthMethod = varchar("tokenEndpointAuthMethod", 256)
    val grantTypes = varchar("grantTypes", 256).default("authorization_code")
    val responseTypes = varchar("responseTypes", 256).default("code")
    val clientName = varchar("clientName", 512).default("").nullable()
    val clientUri = varchar("clientUri", 512).nullable()
    val logoUri = varchar("logoUri", 512).nullable()
    val policyUri = varchar("policyUri", 512).nullable()
    val tosUri = varchar("tosUri", 512).nullable()
    val jwksUri = varchar("jwksUri", 512).nullable()
    val jwks = varchar("jwks", 1024).nullable()
    val softwareId = uuid("softwareId").nullable()
    val softwareVersion = uuid("softwareVersion").nullable()
    val clientSecret = varchar("clientSecret", 512).nullable()
    val clientIdIssuedAt = datetime("clientIdIssuedAt")
    val clientSecretExpiresAt = datetime("clientSecretExpiresAt").nullable()
}

class ClientEntity(client_id: EntityID<Long>) : LongEntity(client_id) {
    companion object : LongEntityClass<ClientEntity>(Clients)

    val redirectUris by RedirectUriEntity referrersOn RedirectUris.client
    var tokenEndpointAuthMethod by Clients.tokenEndpointAuthMethod
    var grantTypes by Clients.grantTypes
    var responseTypes by Clients.responseTypes
    var clientName by Clients.clientName
    var clientUri by Clients.clientUri
    var logoUri by Clients.logoUri
    val scope by ScopeEntity referrersOn Scopes.client
    val contacts by ContactEntity referrersOn Contacts.client
    var clientSecret by Clients.clientSecret
    var policyUri by Clients.policyUri
    var tosUri by Clients.tosUri
    var jwksUri by Clients.jwksUri
    var jwks by Clients.jwks
    var softwareId by Clients.softwareId
    var softwareVersion by Clients.softwareVersion
    var clientIdIssuedAt by Clients.clientIdIssuedAt
    var clientSecretExpiresAt by Clients.clientSecretExpiresAt

    fun toClient() = Client(
        redirectUris.map { it.uri },
        TokenEndpointAuthMethod.valueOf(tokenEndpointAuthMethod),
        grantTypes.split(",").map { GrantType.valueOf(it) },
        responseTypes.split(",").map { ResponseType.valueOf(it) },
        clientName,
        URIorNull(clientUri),
        URIorNull(logoUri),
        scope.map { it.scope }.reduce { acc, s -> "$acc, $s" },
        contacts.map { it.contact },
        URIorNull(tosUri),
        URIorNull(policyUri),
        URIorNull(jwksUri),
        jwks,
        softwareId,
        softwareVersion,
        id.value.toString(),
        clientSecret,
        clientIdIssuedAt,
        clientSecretExpiresAt
    )
}

fun URIorNull(value: String?) = runCatching { URI(value) }.fold({ it }, { null });

data class Client(
    val redirectUris: List<String>,
    val tokenEndpointAuthMethod: TokenEndpointAuthMethod,
    val grantTypes: List<GrantType>,
    val responseTypes: List<ResponseType>,
    val clientName: String?,
    val clientUri: URI?,
    val logoUri: URI?,
    val scope: String?,
    val contacts: List<String>,
    val tosUri: URI?,
    val policyUri: URI?,
    val jwksUri: URI?,
    val jwks: String?,
    val softwareId: UUID?,
    val softwareVersion: UUID?,
    val clientId: String,
    val clientSecret: String?,
    val clientIdIssuedAt: LocalDateTime,
    val clientSecretExpiresAt: LocalDateTime?
)
