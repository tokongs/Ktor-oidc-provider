package dev.kongsvik.ktor_oidc_server.services

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import java.time.LocalDateTime

object TokenService : ITokenService {

    override fun getTokensByAuthorizationCodeGrant(grant: Grant.AuthorizationCodeGrant): IO<Either<TokenServiceError, Tokens>> =
        IO.fx {
            val server = CodeService(IO.async())
            val client = !ClientService.getClientById(grant.clientId)
            val code = server.getCodeByCode(grant.code).map
            if (client.clientId != grant.clientId || client.clientSecret != grant.clientSecret)  return@fx TokenServiceError.InvalidClient.left()
            if(code.redirectUri != grant.redirectUri || code.expireAt.isAfter(LocalDateTime.now())) return@fx TokenServiceError.InvalidGrant.left()

            generateTokens().bind().right()
        }

    override fun getTokensByDeviceCodeGrant(grant: Grant.DeviceCodeGrant): IO<Either<TokenServiceError, Tokens>> {
        TODO("Not yet implemented")
    }

    override fun getTokensByClientCredentialsGrant(grant: Grant.ClientCredentialsGrant): IO<Either<TokenServiceError, Tokens>> {
        TODO("Not yet implemented")
    }

    override fun getTokensByRefreshTokenGrant(grant: Grant.RefreshTokenGrant): IO<Either<TokenServiceError, Tokens>> {
        TODO("Not yet implemented")
    }

    fun generateTokens(): IO<Tokens> = IO.fx {
        Tokens(
            !generateSecret(16),
            !generateSecret(16),
            3600,
            !generateSecret(16),
            emptyList(),
            !generateSecret(16))
    }
}