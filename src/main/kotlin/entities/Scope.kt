package dev.kongsvik.ktor_oidc_server.entities
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Scope(
    val id: Int,
    val clientId: Long,
    val scope: String,
)

object Scopes : IntIdTable("Scopes") {
    val client = reference("client", Clients);
    val scope = varchar("scope", 512);
}

class ScopeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ScopeEntity>(Scopes)

    var client by ClientEntity referencedOn Scopes.client
    var scope by Scopes.scope

    fun toScope() = Scope(id.value, client.id.value, scope)
}

