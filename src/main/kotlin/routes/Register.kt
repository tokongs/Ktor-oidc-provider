package dev.kongsvik.ktor_oidc_server.routes

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

@Serializable
data class ClientRegistrationPostBody(
    @SerialName("redirect_uris")
    val redirectUris: List<String> = emptyList(),
    @SerialName("response_types")
    val responseTypes: List<ResponseType> = listOf(ResponseType.CODE),
    @SerialName("grant_types")
    val grantTypes: List<GrantType> = listOf(GrantType.AUTHORIZATION_CODE),
    @SerialName("application_type")
    val applicationType: ApplicationType = ApplicationType.WEB,
    @SerialName("contacts")
    val contacts: List<String> = emptyList(),
    @SerialName("client_name")
    val clientName: String? = null,
    @SerialName("logo_uri")
    val logoUri: String? = null,
    @SerialName("client_uri")
    val clientUri: String? = null,
    @SerialName("policy_uri")
    val policyUri: String? = null,
    @SerialName("tos_uri")
    val tosUri: String? = null,
    @SerialName("jwks_uri")
    val jwksUri: String? = null,
    @SerialName("jwks")
    val jwks: String? = null,
    @SerialName("sector_identifier_uri")
    val sectorIdentifierUri: String? = null,
    @SerialName("subject_type")
    val subjectType: String? = null,
    @SerialName("id_token_signed_response_alg")
    val idTokenSignedResponseAlg: String? = null,
    @SerialName("id_token_encrypted_response_alg")
    val idTokenEncryptedResponseAlg: String? = null,
    @SerialName("id_token_encrypted_response_enc")
    val idTokenEncryptedResponseEnc: String? = null,
    @SerialName("userinfo_signed_response_alg")
    val userinfoSignedResponseAlg: String? = null,
    @SerialName("userinfo_encrypted_response_alg")
    val userinfoEncryptedResponseAlg: String? = null,
    @SerialName("userinfo_encrypted_response_enc")
    val userinfoEncryptedResponseEnc: String? = null,
    @SerialName("request_object_signing_alg")
    val requestObjectSigningAlg: String? = null,
    @SerialName("request_object_encryption_alg")
    val requestObjectEncryptionAlg: String? = null,
    @SerialName("request_object_encryption_enc")
    val requestObjectEncryptionEnc: String? = null,
    @SerialName("token_endpoint_auth_method")
    val tokenEndpointAuthMethod: String? = null,
    @SerialName("token_endpoint_auth_signing_alg")
    val tokenEndpointAuthSigningAlg: String? = null,
    @SerialName("default_max_age")
    val defaultMaxAge: Long? = null,
    @SerialName("require_auth_time")
    val requireAuthTime: Boolean = false,
    @SerialName("default_acr_values")
    val defaultAcrValues: List<String>? = emptyList(),
    @SerialName("initiate_login_uri")
    val initiateLoginUri: String? = null,
    @SerialName("request_uris")
    val requestUris: List<String> = emptyList(),
)

@Serializable
data class ClientRegistrationSuccessResponse(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String?,
    @SerialName("registration_access_token")
    val registrationAccessToken: String?,
    @SerialName("registration_client_uri")
    val registrationClientUri: String?,
    @SerialName("client_id_issued_at")
    val clientIdIssuedAt: String?,
    @SerialName("client_secret_expires_at")
    val clientSecretExpiresAt: Long,
    @SerialName("redirect_uris")
    val redirectUris: List<String>,
    @SerialName("response_types")
    val responseTypes: List<ResponseType>,
    @SerialName("grant_types")
    val grantTypes: List<GrantType>,
    @SerialName("application_type")
    val applicationType: ApplicationType,
    @SerialName("contacts")
    val contacts: List<String>,
    @SerialName("client_name")
    val clientName: String?,
    @SerialName("logo_uri")
    val logoUri: String?,
    @SerialName("client_uri")
    val clientUri: String?,
    @SerialName("policy_uri")
    val policyUri: String?,
    @SerialName("tos_uri")
    val tosUri: String?,
    @SerialName("jwks_uri")
    val jwksUri: String?,
    @SerialName("jwks")
    val jwks: String?,
    @SerialName("sector_identifier_uri")
    val sectorIdentifierUri: String?,
    @SerialName("subject_type")
    val subjectType: String?,
    @SerialName("id_token_signed_response_alg")
    val idTokenSignedResponseAlg: String?,
    @SerialName("id_token_encrypted_response_alg")
    val idTokenEncryptedResponseAlg: String?,
    @SerialName("id_token_encrypted_response_enc")
    val idTokenEncryptedResponseEnc: String?,
    @SerialName("userinfo_signed_response_alg")
    val userinfoSignedResponseAlg: String?,
    @SerialName("userinfo_encrypted_response_alg")
    val userinfoEncryptedResponseAlg: String?,
    @SerialName("userinfo_encrypted_response_enc")
    val userinfoEncryptedResponseEnc: String?,
    @SerialName("request_object_signing_alg")
    val requestObjectSigningAlg: String?,
    @SerialName("request_object_encryption_alg")
    val requestObjectEncryptionAlg: String?,
    @SerialName("request_object_encryption_enc")
    val requestObjectEncryptionEnc: String?,
    @SerialName("token_endpoint_auth_method")
    val tokenEndpointAuthMethod: String?,
    @SerialName("token_endpoint_auth_signing_alg")
    val tokenEndpointAuthSigningAlg: String?,
    @SerialName("default_max_age")
    val defaultMaxAge: Long?,
    @SerialName("require_auth_time")
    val requireAuthTime: Boolean,
    @SerialName("default_acr_values")
    val defaultAcrValues: List<String>?,
    @SerialName("initiate_login_uri")
    val initiateLoginUri: String? ,
    @SerialName("request_uris")
    val requestUris: List<String>,
)

@Serializable
data class ClientRegistrationErrorResponse(
    val error: String,
    @SerialName("error_description")
    val errorDescription: String,

    )

fun clientToResponseBody(client: Client): ClientRegistrationSuccessResponse = ClientRegistrationSuccessResponse(
    client.clientId.toString(),
    client.clientSecret,
    "reg_access_token",
    "reg_client_uri",
    "client_id_issued_at",
    0,
    client.redirectUris.map {it.uri.toString()},
    client.responseTypes,
    client.grantTypes,
    client.applicationType,
    client.contacts.map { it.contact },
    client.clientName,
    client.logoUri.toString(),
    client.clientUri.toString(),
    client.policyUri.toString(),
    client.tosUri.toString(),
    client.jwksUri.toString(),
    client.jwks,
    client.sectorIdentifierUri.toString(),
    client.subjectType,
    client.idTokenSignedResponseAlg,
    client.idTokenEncryptedResponseAlg,
    client.idTokenEncryptedResponseEnc,
    client.userinfoSignedResponseAlg,
    client.userinfoEncryptedResponseAlg,
    client.userinfoEncryptedResponseEnc,
    client.requestObjectSigningAlg,
    client.requestObjectEncryptionAlg,
    client.requestObjectEncryptionEnc,
    client.tokenEndpointAuthMethod,
    client.tokenEndpointAuthSigningAlg,
    client.defaultMaxAge,
    client.requireAuthTime,
    client.defaultAcrValues,
    client.inititateLoginUri.toString(),
    client.redirectUris.map{it.uri.toString()}
)

fun createClientFromPostBody(body: ClientRegistrationPostBody): Client = transaction {
    ClientEntity.new {
        clientName = body.clientName
        clientSecret = generateSecret(32)
        applicationType = body.applicationType.toString()
        clientUri = body.clientUri
        defaultAcrValues = body.defaultAcrValues?.joinToString(",")
        defaultMaxAge = body.defaultMaxAge
        grantTypes = body.grantTypes?.joinToString(",")
        idTokenEncryptedResponseAlg = body.idTokenEncryptedResponseAlg
        idTokenSignedResponseAlg = body.idTokenSignedResponseAlg
        idTokenEncryptedResponseEnc = body.idTokenEncryptedResponseEnc
        initiateLoginUri = body.initiateLoginUri
        jwks = body.jwks
        jwksUri = body.jwksUri
        logoUri = body.logoUri
        policyUri = body.policyUri
        requireAuthTime = body.requireAuthTime
        requestObjectEncryptionAlg = body.requestObjectEncryptionAlg
        requestObjectEncryptionEnc = body.requestObjectEncryptionEnc
        requestObjectSigningAlg = body.requestObjectSigningAlg
        sectoryIdentifierUri = body.sectorIdentifierUri
        subjectType = body.subjectType
        tokenEndpointAuthMethod = body.tokenEndpointAuthMethod
        tokenEndpointAuthSigningAlg = body.tokenEndpointAuthSigningAlg
        userinfoEncryptedResponseAlg = body.userinfoEncryptedResponseAlg
        userinfoEncryptedResponseEnc = body.userinfoEncryptedResponseEnc
        userinfoSignedResponseAlg = body.userinfoSignedResponseAlg

    }.apply {
        body.redirectUris.forEach {
            RedirectUriEntity.new {
                client = this@apply
                uri = it
            }
        }
    }.apply {
        body.requestUris.forEach {
            RequestUriEntity.new {
                client = this@apply
                uri = it
            }
        }
    }.apply {
        body.contacts.forEach {
            ContactEntity.new {
                client = this@apply
                contact = it
            }
        }
    }.toClient()

}

