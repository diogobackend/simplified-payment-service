package com.simplifiedpayment.core.port.output

import com.simplifiedpayment.core.domain.model.Transfer

interface TransferRepositoryPort {
    fun save(transfer: Transfer): Transfer
}
