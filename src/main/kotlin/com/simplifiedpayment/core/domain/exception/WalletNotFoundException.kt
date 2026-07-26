package com.simplifiedpayment.core.domain.exception

import com.simplifiedpayment.core.common.messages.PaymentMessages.WALLET_NOT_FOUND_WITH_USER_ID

class WalletNotFoundException(
    userId: Long,
) : RuntimeException("$WALLET_NOT_FOUND_WITH_USER_ID: $userId")
