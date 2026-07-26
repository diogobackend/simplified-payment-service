package com.simplifiedpayment.core.port.output

import com.simplifiedpayment.core.domain.model.Transfer

interface AuthorizeTransferPort {
    fun authorize(transfer: Transfer): Boolean
}
