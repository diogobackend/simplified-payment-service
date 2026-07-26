package com.simplifiedpayment.core.domain.exception

import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_NOT_AUTHORIZED

class TransferNotAuthorizedException : RuntimeException(TRANSFER_NOT_AUTHORIZED)
