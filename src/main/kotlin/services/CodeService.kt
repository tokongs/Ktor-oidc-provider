package dev.kongsvik.ktor_oidc_server.services

import arrow.Kind
import arrow.core.Either
import arrow.fx.typeclasses.Async
import dev.kongsvik.ktor_oidc_server.entities.Code
import dev.kongsvik.ktor_oidc_server.entities.CodeEntity
import dev.kongsvik.ktor_oidc_server.entities.Codes
import org.jetbrains.exposed.sql.transactions.transaction

class CodeService<F>(private val A: Async<F>) : ICodeService<F>, Async<F> by A {
    override fun getCodeByCode(code: String): Kind<F, Either<CodeServiceException, Code>> = async {
        transaction {
            CodeEntity.find { Codes.code eq code }.first().toCode()
        }
    }
}