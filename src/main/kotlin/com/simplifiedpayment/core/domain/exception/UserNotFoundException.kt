package com.simplifiedpayment.core.domain.exception

import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_NOT_FOUND_WITH_ID

class UserNotFoundException(
    userId: Long,
) : RuntimeException("$USER_NOT_FOUND_WITH_ID: $userId")
