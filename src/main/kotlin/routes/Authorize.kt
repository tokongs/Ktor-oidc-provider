package dev.kongsvik.ktor_oidc_server.routes

import dev.kongsvik.ktor_oidc_server.entities.ClientEntity
import dev.kongsvik.ktor_oidc_server.entities.CodeEntity
import dev.kongsvik.ktor_oidc_server.entities.GrantType
import dev.kongsvik.ktor_oidc_server.entities.ResponseType
import dev.kongsvik.ktor_oidc_server.utils.generateSecret
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

fun Routing.authorize() {
    route("/authorize"){
        post {
            val body = call.receive<AuthorizeRequestBody>();
            val state = if(body.state != null) "&state=${body.state}" else ""

            val clientEntity = transaction { ClientEntity.findById(body.clientId.toLong()) }
            if(clientEntity == null) {
                call.respondRedirect("${body.redirectUri}?error=invalid_request&error_description=client_id%20does%20not%20exists${state}")
                return@post
            }

            val client = transaction { clientEntity.toClient() }

            if(!client.grantTypes.contains(GrantType.AUTHORIZATION_CODE)){
                call.respondRedirect("${body.redirectUri}?error=unauthorized_client&error_description=client%20is%20not%20authorized%20to%20request%20code${state}")
                return@post
            }

            if(!client.responseTypes.contains(ResponseType.CODE)){
                call.respondRedirect("${body.redirectUri}?error=unsupported_response_type&error_description=client%20does%20not%20support%20code%20response%20type${state}")
                return@post
            }

            if(!client.redirectUris.any{
                    println(it.uri)
                    it.uri.toString() == body.redirectUri
                }){
                call.respondRedirect("${body.redirectUri}?error=access_denied&error_description=invalid%20redirect_uri${state}")
                return@post
            }

            if(!body.scope.contains("openid")){
                call.respondRedirect("${body.redirectUri}?error=invalid_scope&error_description=request%20openid%20scope${state}")
                return@post
            }

            val code =  transaction {
                CodeEntity.new {
                    this.client = clientEntity
                    issuedAt = LocalDateTime.now()
                    expireAt = LocalDateTime.now().plusMinutes(10)
                    this.code = generateSecret(32)
                    redirectUri = body.redirectUri
                }.toCode()
            }

            call.respondRedirect("${body.redirectUri}?code=${code.code}${state}")

        }

        get {

        }
    }
}

data class AuthorizeResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long?,
    val scope: List<String>?,
    val state: String?
)

@Serializable
data class AuthorizeRequestBody(
    val scope: List<String>,
    @SerialName("response_type")
    val responseType: ResponseType,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("redirect_uri")
    val redirectUri: String,
    @SerialName("state")
    val state: String? = null,
    @SerialName("response_mode")
    val responseMode: String? = null,
    @SerialName("nonce")
    val nonce: String? = null,
    @SerialName("display")
    val display: String? = null,
    @SerialName("prompt")
    val prompt: String? = null,
    @SerialName("max_age")
    val maxAge: String? = null,
    @SerialName("ui_locales")
    val uiLocales: String? = null,
    @SerialName("id_token_hint")
    val idTokenHint: String? = null,
    @SerialName("login_hint")
    val loginHint: String? = null,
    @SerialName("acr_values")
    val acrValues: List<String>? = null,
)
