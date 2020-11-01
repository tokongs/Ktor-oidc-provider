package dev.kongsvik.ktor_oidc_server.entities

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Contact(
    val id: Int,
    val clientId: Long,
    val contact: String,
)

object Contacts : IntIdTable("Contacts") {
    val client = reference("client", Clients);
    val contact = varchar("contact", 512);
}

class ContactEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ContactEntity>(Contacts)

    var client by ClientEntity referencedOn Contacts.client
    var contact by Contacts.contact

    fun toContact() = Contact(id.value, client.id.value, contact)
}

