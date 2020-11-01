package dev.kongsvik.ktor_oidc_server.routes

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.extensions.either.applicativeError.raiseError
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.validated.applicativeError.raiseError
import arrow.core.fix
import arrow.core.left
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.handleError
import dev.kongsvik.ktor_oidc_server.services.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.h2.engine.Right



fun Routing.token() {
    route("token") {
        post {
            getTokenParams(call.receiveParameters())
                .flatMap { grantFromRequest(it) }
                .flatMap { getToken(it) }
                .attempt().unsafeRunSync().apply {
                    when (this) {
                        is Either.Left -> call.respond(HttpStatusCode.BadRequest, TokenEndpointError.InvalidRequest("Something went wrong"))
                        is Either.Right -> when(this.b){
                            is Either.Left ->  call.respond(HttpStatusCode.BadRequest, (this.b as Either.Left<TokenEndpointError>).a)
                            is Either.Right ->  call.respond(HttpStatusCode.OK, tokenResponseFromTokens((this.b as Either.Right<Tokens>).b))
                        }
                    }
                }
        }
    }
}

private fun getToken(grant: Grant): IO<Either<TokenEndpointError, Tokens>> = when (grant) {
    is Grant.AuthorizationCodeGrant -> TokenService.getTokensByAuthorizationCodeGrant(grant)
        .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }

    is Grant.ClientCredentialsGrant -> TokenService.getTokensByClientCredentialsGrant(grant)
        .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }

    is Grant.DeviceCodeGrant -> TokenService.getTokensByDeviceCodeGrant(grant)
        .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }

    is Grant.RefreshTokenGrant -> TokenService.getTokensByRefreshTokenGrant(grant)
        .map { it.mapLeft { error -> tokenEndpointErrorFromTokenServiceError(error) } }
}

private fun grantFromRequest(request: TokenRequest): IO<Grant> = when (request.grantType) {
    "authorization_code" -> IO.just(
        Grant.AuthorizationCodeGrant(
            request.code,
            request.redirectUri,
            request.clientId,
            request.clientSecret
        )
    )
    "client_credentials" -> IO.just(
        Grant.ClientCredentialsGrant(
            emptyList(),
            request.clientId,
            request.clientSecret
        )
    )
    "device_code" -> IO.just(
        Grant.DeviceCodeGrant(
            "code",
            request.clientId
        )
    )
    "refresh_token" -> IO.just(
        Grant.RefreshTokenGrant(
            "token",
            emptyList(),
            request.clientId,
            request.clientSecret,
        )
    )
    else -> IO.raiseError(TokenEndpointError.InvalidGrant())
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

private fun tokenResponseFromTokens(tokens: Tokens) = TokenResponse(
    tokens.accessToken, tokens.tokenType, tokens.refreshToken, tokens.expiresIn, tokens.IdToken
)

@Serializable
private sealed class TokenEndpointError(val error: String) : Error() {
    @Serializable
    class InvalidRequest(val error_description: String) : TokenEndpointError("invalid_request")
    @Serializable
    class InvalidClient() : TokenEndpointError("invalid_client")
    @Serializable
    class InvalidGrant() : TokenEndpointError("invalid_grant")
    @Serializable
    class UnauthorizedClient() : TokenEndpointError("unauthorized_client")
    @Serializable
    class InvalidScope() : TokenEndpointError("invalid_scope")
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
