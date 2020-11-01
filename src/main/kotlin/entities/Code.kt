package dev.kongsvik.ktor_oidc_server.entities

import dev.kongsvik.ktor_oidc_server.entities.Codes.code
import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

data class Code(
    val code: String,
    val expireAt: LocalDateTime,
    val issuedAt: LocalDateTime,
    val client: Client,
    val redirectUri: String?,
    )

object Codes : IntIdTable("Codes") {
    val code = varchar("code", 512).uniqueIndex();
    val expireAt = datetime("expireAt");
    val issuedAt = datetime("issuedAt");
    val client = reference("client", Clients);
    val redirectUri = varchar("redirectUri", 512).nullable();

}

class CodeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CodeEntity>(Codes) {
        fun create(client: Client, expiryTime: TemporalAmount, redirectUri: String?): CodeEntity {
            val code = Code(
                generateSecret(32),
                LocalDateTime.now(),
                LocalDateTime.now().plus(expiryTime),
                client,
                redirectUri
            )
            return save(code)
        }

        fun save(code: Code) = new {
            this.code = code.code
            expireAt = code.expireAt
            issuedAt = code.issuedAt
            this.client = client
        }

        fun getByCode(value: String) = find { code eq value }.firstOrNull()
    }

    var client by ClientEntity referencedOn Codes.client
    var code by Codes.code
    var expireAt by Codes.expireAt
    var issuedAt by Codes.issuedAt
    var redirectUri by Codes.redirectUri

    fun toCode() = Code(code, expireAt, issuedAt, client.toClient(), redirectUri)
}

