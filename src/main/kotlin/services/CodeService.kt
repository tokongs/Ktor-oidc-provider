package dev.kongsvik.ktor_oidc_server.services

import arrow.fx.IO
import arrow.fx.extensions.fx
import dev.kongsvik.ktor_oidc_server.entities.Code
import dev.kongsvik.ktor_oidc_server.entities.CodeEntity
import dev.kongsvik.ktor_oidc_server.entities.Codes
import dev.kongsvik.ktor_oidc_server.entities.Codes.code
import org.jetbrains.exposed.sql.transactions.transaction

object CodeService : ICodeService {
    override fun getCodeByCode(code: String): IO<Code> = IO.fx {
        transaction {
            CodeEntity.find { Codes.code eq code }.first().toCode()
        }
    }

}