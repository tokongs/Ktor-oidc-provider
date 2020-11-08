package dev.kongsvik.ktor_oidc_server.services

import arrow.fx.IO
import arrow.fx.extensions.fx
import dev.kongsvik.ktor_oidc_server.entities.*
import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object ClientService : IClientService {
    override fun getClientById(clientId: String): IO<Client> = IO.fx {
        transaction {
            ClientEntity.findById(clientId.toLong())!!.toClient()
        }
    }

    override fun createClient(
        redirectUris: List<String>,
        tokenEndpointAuthMethod: TokenEndpointAuthMethod,
        grantTypes: List<GrantType>,
        responseTypes: List<ResponseType>,
        clientUri: String?,
        logoUri: String?,
        scope: String?,
        contacts: List<String>,
        tosUri: String?,
        policyUrl: String?,
        jwksUri: String?,
        softwareId: UUID?,
        softwareVersion: UUID?
    ): IO<Client> = IO.fx {
        val clientSecret = !generateSecret(32)
        val now = LocalDateTime.now()

        transaction {
            val clientEntity = ClientEntity.new {
                this.tokenEndpointAuthMethod = tokenEndpointAuthMethod.toString()
                this.grantTypes = grantTypes.joinToString(",")
                this.responseTypes = responseTypes.joinToString(",")
                this.clientUri = clientUri
                this.logoUri = logoUri
                this.tosUri = tosUri
                this.policyUri = policyUri
                this.jwksUri = jwksUri
                this.softwareId = softwareId
                this.softwareVersion = softwareVersion
                this.clientSecret = clientSecret
                this.clientIdIssuedAt = now
                this.clientSecretExpiresAt = null
            }

            scope?.split(" ")?.forEach {
                ScopeEntity.new {
                    client = clientEntity
                    this.scope = it
                }
            }

            redirectUris.forEach {
                RedirectUriEntity.new {
                    client = clientEntity
                    this.uri = it
                }
            }

            clientEntity.toClient()
        }
    }

}