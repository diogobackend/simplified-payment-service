package com.simplifiedpayment.app.adapter.output.client

import com.simplifiedpayment.app.configuration.logs.LogInfo
import com.simplifiedpayment.app.configuration.logs.LogParameter
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.Builder

@Component
class NotificationClientAdapter(
    private val restClientBuilder: Builder,
    @Value("\${external.notification.url}")
    private val notificationUrl: String,
) : NotifyPayeePort {
    @LogInfo(logParameters = true)
    override fun notify(
        @LogParameter(name = "transfer")
        transfer: Transfer,
    ) {
        val requestBody =
            """
            {
              "transferId": "${transfer.transferId}",
              "payerId": ${transfer.payerId},
              "payeeId": ${transfer.payeeId},
              "value": ${transfer.value.value}
            }
            """.trimIndent()

        restClientBuilder
            .build()
            .post()
            .uri(notificationUrl)
            .contentType(APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .toBodilessEntity()
    }
}
