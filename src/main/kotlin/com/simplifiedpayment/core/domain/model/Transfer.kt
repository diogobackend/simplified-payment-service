package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_PAYEE_ID_MUST_BE_POSITIVE
import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_PAYER_AND_PAYEE_MUST_BE_DIFFERENT
import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_PAYER_ID_MUST_BE_POSITIVE
import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_VALUE_MUST_BE_POSITIVE
import com.simplifiedpayment.core.domain.valueobject.Money
import java.time.LocalDateTime
import java.util.UUID

data class Transfer(
    val transferId: UUID = UUID.randomUUID(),
    val payerId: Long,
    val payeeId: Long,
    val value: Money,
    val status: TransferStatus = TransferStatus.CREATED,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
) {
    init {
        require(payerId > 0) {
            TRANSFER_PAYER_ID_MUST_BE_POSITIVE
        }

        require(payeeId > 0) {
            TRANSFER_PAYEE_ID_MUST_BE_POSITIVE
        }

        require(payerId != payeeId) {
            TRANSFER_PAYER_AND_PAYEE_MUST_BE_DIFFERENT
        }

        require(value.isGreaterThan(Money.ZERO)) {
            TRANSFER_VALUE_MUST_BE_POSITIVE
        }
    }

    fun authorize(): Transfer =
        copy(
            status = TransferStatus.AUTHORIZED,
            updatedAt = LocalDateTime.now(),
        )

    fun complete(): Transfer =
        copy(
            status = TransferStatus.COMPLETED,
            updatedAt = LocalDateTime.now(),
        )

    fun fail(): Transfer =
        copy(
            status = TransferStatus.FAILED,
            updatedAt = LocalDateTime.now(),
        )
}
