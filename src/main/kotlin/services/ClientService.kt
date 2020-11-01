package dev.kongsvik.ktor_oidc_server.services

import arrow.Kind
import arrow.fx.IO
import arrow.fx.extensions.fx
import dev.kongsvik.ktor_oidc_server.entities.*
import org.jetbrains.exposed.sql.transactions.transaction
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
        transaction {
            ClientEntity.new {
                re
            }.toClient()
        }
    }

}