package dev.kongsvik.ktor_oidc_server.services

import arrow.fx.IO
import dev.kongsvik.ktor_oidc_server.entities.Client
import dev.kongsvik.ktor_oidc_server.entities.GrantType
import dev.kongsvik.ktor_oidc_server.entities.ResponseType
import dev.kongsvik.ktor_oidc_server.entities.TokenEndpointAuthMethod
import java.util.*


interface IClientService {
    fun getClientById(clientId: String): IO<Client>
    fun createClient(
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
    ): IO<Client>
}

sealed class ClientServiceException {
    object ClientDoesNotExist : ClientServiceException()
    object FailedTOCreate : ClientServiceException()
}