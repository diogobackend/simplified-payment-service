package com.simplifiedpayment.app.adapter.input.web.requests

import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class TransferRequest(
    @field:Positive
    val value: BigDecimal,
    @field:Positive
    val payer: Long,
    @field:Positive
    val payee: Long,
)
