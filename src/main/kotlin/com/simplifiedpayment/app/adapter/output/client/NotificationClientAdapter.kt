package com.simplifiedpayment.app.adapter.output.client

import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.Builder
import java.math.BigDecimal

@Component
class NotificationClientAdapter(
    private val restClientBuilder: Builder,
    @Value("\${external.notification.url}")
    private val notificationUrl: String,
) : NotifyPayeePort {
    override fun notify(transfer: Transfer) {
        restClientBuilder
            .build()
            .post()
            .uri(notificationUrl)
            .body(NotificationRequest.from(transfer))
            .retrieve()
            .toBodilessEntity()
    }

    private data class NotificationRequest(
        val transferId: String,
        val payerId: Long,
        val payeeId: Long,
        val value: BigDecimal,
    ) {
        companion object {
            fun from(transfer: Transfer): NotificationRequest =
                NotificationRequest(
                    transferId = transfer.transferId.toString(),
                    payerId = transfer.payerId,
                    payeeId = transfer.payeeId,
                    value = transfer.value.value,
                )
        }
    }
}
