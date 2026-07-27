package com.simplifiedpayment.app.adapter.input.web.responses

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransferResponse(
    val transferId: String,
    val payer: Long,
    val payee: Long,
    val value: BigDecimal,
    val status: String,
    val createdAt: LocalDateTime,
)
