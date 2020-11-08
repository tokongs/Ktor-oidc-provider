package dev.kongsvik.ktor_oidc_server.services

import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.mtl.EitherT

sealed class TokenServiceError(){
    object InvalidClient : TokenServiceError()
    object InvalidGrant : TokenServiceError()
    object UnauthorizedClient : TokenServiceError()
    object InvalidScope : TokenServiceError()
}

data class Tokens(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val refreshToken: String?,
    val scope: List<String>,
    val IdToken: String
)

sealed class Grant {
    data class AuthorizationCodeGrant(val code: String, val redirectUri: String, val clientId: String, val clientSecret: String): Grant()
    data class ClientCredentialsGrant(val scope: List<String>, val clientId: String, val clientSecret: String): Grant()
    data class DeviceCodeGrant(val deviceCode: String, val clientId: String): Grant()
    data class RefreshTokenGrant(val refreshToken: String, val scope: List<String>,val clientId: String, val clientSecret: String): Grant()
}

interface ITokenService {

    fun getTokensByAuthorizationCodeGrant(grant: Grant.AuthorizationCodeGrant): IO<Either<TokenServiceError, Tokens>>
    fun getTokensByDeviceCodeGrant(grant: Grant.DeviceCodeGrant): IO<Either<TokenServiceError, Tokens>>
    fun getTokensByClientCredentialsGrant(grant: Grant.ClientCredentialsGrant): IO<Either<TokenServiceError, Tokens>>
    fun getTokensByRefreshTokenGrant(grant: Grant.RefreshTokenGrant): IO<Either<TokenServiceError, Tokens>>
}

