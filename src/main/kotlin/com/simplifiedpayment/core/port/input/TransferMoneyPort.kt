package com.simplifiedpayment.core.port.input

import com.simplifiedpayment.core.domain.model.Transfer

interface TransferMoneyPort {
    fun transfer(input: TransferMoneyInput): Transfer
}
