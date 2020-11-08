package dev.kongsvik.ktor_oidc_server.services

import arrow.Kind
import arrow.core.Either
import arrow.fx.IO
import dev.kongsvik.ktor_oidc_server.entities.Code

interface  ICodeService<F> {
    fun getCodeByCode(code: String): Kind<F, Either<CodeServiceException, Code>>
}

sealed class CodeServiceException {
    object CodeDoesNotExist: CodeServiceException()
}

