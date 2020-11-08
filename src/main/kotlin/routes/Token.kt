package dev.kongsvik.ktor_oidc_server.routes

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.extensions.fx
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.IO.Companion.effect
import arrow.fx.extensions.fx
import arrow.fx.extensions.toIO
import dev.kongsvik.ktor_oidc_server.services.Grant
import dev.kongsvik.ktor_oidc_server.services.TokenService
import dev.kongsvik.ktor_oidc_server.services.TokenServiceError
import dev.kongsvik.ktor_oidc_server.services.Tokens
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


fun Routing.token() {
    route("token") {
        post {
            IO.fx {
                val response = !effect { call.receiveParameters() }
                    .flatMap { getTokenParams(it) }
                    .flatMap { grantFromRequest(it).toIO() }
                    .flatMap { getToken(it) }

                when (response) {
                    is Either.Left -> effect { call.respond(HttpStatusCode.BadRequest, response.a) }
                    is Either.Right -> effect { call.respond(HttpStatusCode.OK, response.b) }
                }

            }.unsafeRunAsync {
                if (it is Either.Left) effect {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        TokenEndpointError.InvalidRequest("something went front")
                    )
                }
            }
        }
    }
}

private fun getToken(grant: Grant): IO<Either<TokenEndpointError, Tokens>> =IO.fx {
    when (grant) {
         is Grant.AuthorizationCodeGrant -> !TokenService.getTokensByAuthorizationCodeGrant(grant)
             .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }
    /*     is Grant.ClientCredentialsGrant -> TokenService.getTokensByClientCredentialsGrant(grant)
             .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }
         is Grant.DeviceCodeGrant -> TokenService.getTokensByDeviceCodeGrant(grant)
             .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }
         is Grant.RefreshTokenGrant -> TokenService.getTokensByRefreshTokenGrant(grant)
             .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }*/
     }
}

private fun grantFromRequest(request: TokenRequest): Either<TokenEndpointError, Grant> = when (request.grantType) {
    "authorization_code" -> Grant.AuthorizationCodeGrant(
        request.code,
        request.redirectUri,
        request.clientId,
        request.clientSecret
    ).right()

    "client_credentials" -> Grant.ClientCredentialsGrant(
        emptyList(),
        request.clientId,
        request.clientSecret
    ).right()

    "device_code" -> Grant.DeviceCodeGrant(
        "code",
        request.clientId
    ).right()

    "refresh_token" ->
        Grant.RefreshTokenGrant(
            "token",
            emptyList(),
            request.clientId,
            request.clientSecret,
        ).right()

    else -> TokenEndpointError.InvalidGrant().left()
}

private fun tokenEndpointErrorFromTokenServiceError(error: TokenServiceError) = when (error) {
    is TokenServiceError.InvalidGrant -> TokenEndpointError.InvalidGrant()
    is TokenServiceError.InvalidClient -> TokenEndpointError.InvalidClient()
    is TokenServiceError.UnauthorizedClient -> TokenEndpointError.UnauthorizedClient()
    is TokenServiceError.InvalidScope -> TokenEndpointError.InvalidScope()
}

private fun getTokenParams(params: Parameters): IO<TokenRequest> = IO.fx {
    TokenRequest(
        params["client_id"]!!,
        params["grant_type"]!!,
        params["code"]!!,
        params["redirect_uri"]!!,
        params["client_secret"]!!
    )
}


@Serializable
private sealed class TokenEndpointError(val error: String) : Error() {
    @Serializable
    class InvalidRequest(val error_description: String) : TokenEndpointError("invalid_request")

    @Serializable
    class InvalidClient : TokenEndpointError("invalid_client")

    @Serializable
    class InvalidGrant : TokenEndpointError("invalid_grant")

    @Serializable
    class UnauthorizedClient : TokenEndpointError("unauthorized_client")

    @Serializable
    class InvalidScope : TokenEndpointError("invalid_scope")
}

@Serializable
private data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("id_token")
    val idToken: String,
)

private data class TokenRequest(
    val clientId: String,
    val grantType: String,
    val code: String,
    val redirectUri: String,
    val clientSecret: String,
)
