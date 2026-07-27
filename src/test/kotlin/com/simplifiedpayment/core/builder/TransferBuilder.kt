package com.simplifiedpayment.core.builder

import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.domain.model.TransferStatus
import com.simplifiedpayment.core.domain.model.TransferStatus.CREATED
import com.simplifiedpayment.core.domain.valueobject.Money
import java.math.BigDecimal

fun buildTransferDomain(
    payerId: Long = 1L,
    payeeId: Long = 2L,
    value: BigDecimal = BigDecimal("100.00"),
    status: TransferStatus = CREATED,
): Transfer =
    Transfer(
        payerId = payerId,
        payeeId = payeeId,
        value = Money(value),
        status = status,
    )
