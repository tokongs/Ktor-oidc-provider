package dev.kongsvik.ktor_oidc_server.services

import arrow.fx.IO
import dev.kongsvik.ktor_oidc_server.entities.Code

interface ICodeService {
    fun getCodeByCode(code: String): IO<Code>
}

sealed class CodeServiceException {
    object CodeDoesNotExist: CodeServiceException()
}