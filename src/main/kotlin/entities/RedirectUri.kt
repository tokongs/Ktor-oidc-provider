package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.serializers.URISerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.net.URI


object RedirectUris : IntIdTable("RedirectUris") {
    val client = reference("client", Clients);
    val uri: Column<String> = varchar("uri", 512);
}

class RedirectUriEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RedirectUriEntity>(RedirectUris)

    var client by ClientEntity referencedOn RedirectUris.client
    var uri by RedirectUris.uri

    fun toRedirectUri() = RedirectUri(id.value, client.id.value, URI(uri))
}

@Serializable
data class RedirectUri(
    val id: Int,
    val clientId: Long,
    @Serializable(with = URISerializer::class) val uri: URI,
)

