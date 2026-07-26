package com.simplifiedpayment.core.port.input

import java.math.BigDecimal

data class TransferMoneyInput(
    val value: BigDecimal,
    val payer: Long,
    val payee: Long,
)
