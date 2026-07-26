package com.simplifiedpayment.core.usecase.builder

import com.simplifiedpayment.core.port.input.TransferMoneyInput
import java.math.BigDecimal

fun buildTransferMoneyInput(
    value: BigDecimal = BigDecimal("100.00"),
    payer: Long = 1L,
    payee: Long = 2L,
): TransferMoneyInput =
    TransferMoneyInput(
        value = value,
        payer = payer,
        payee = payee,
    )
