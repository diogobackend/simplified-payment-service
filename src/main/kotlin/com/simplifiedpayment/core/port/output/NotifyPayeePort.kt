package com.simplifiedpayment.core.port.output

import com.simplifiedpayment.core.domain.model.Transfer

interface NotifyPayeePort {
    fun notify(transfer: Transfer)
}
