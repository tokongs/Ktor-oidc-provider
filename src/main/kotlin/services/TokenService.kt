package dev.kongsvik.ktor_oidc_server.services

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.extensions.fx

import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import java.time.LocalDateTime

object TokenService : ITokenService {
    override fun getTokensByAuthorizationCodeGrant(grant: Grant.AuthorizationCodeGrant): IO<Either<TokenServiceError, Tokens>> =
        IO.fx {
            val client = ClientService.getClientById(grant.clientId)

            val isClientValid = client.map {
                it.clientId.toString() == grant.clientId && it.clientSecret == grant.clientSecret
            }.bind()

            if (!isClientValid)
                return@fx TokenServiceError.InvalidClient.left()

            val code = CodeService.getCodeByCode(grant.code)

            val isGrantValid = code.map {
                it.redirectUri == grant.redirectUri && it.expireAt.isAfter(LocalDateTime.now())
            }.bind()

            if (!isGrantValid)
                return@fx TokenServiceError.InvalidGrant.left()

            Tokens(
                generateSecret(16),
                generateSecret(16),
                3600,
                generateSecret(16),
                emptyList(),
                generateSecret(16)
            ).right()
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

}