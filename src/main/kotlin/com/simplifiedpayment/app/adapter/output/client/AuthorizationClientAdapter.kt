package com.simplifiedpayment.app.adapter.output.client

import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AuthorizationClientAdapter(
    private val restClientBuilder: RestClient.Builder,
    @Value("\${external.authorization.url}")
    private val authorizationUrl: String,
) : AuthorizeTransferPort {
    override fun authorize(transfer: Transfer): Boolean =
        runCatching {
            restClientBuilder
                .build()
                .get()
                .uri(authorizationUrl)
                .retrieve()
                .body(AuthorizationResponse::class.java)
                ?.data
                ?.authorization
                ?: false
        }.getOrDefault(false)

    private data class AuthorizationResponse(
        val data: AuthorizationData? = null,
    )

    private data class AuthorizationData(
        val authorization: Boolean = false,
    )
}
