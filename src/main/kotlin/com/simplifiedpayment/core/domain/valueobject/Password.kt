package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.core.common.messages.PaymentMessages.PASSWORD_MUST_HAVE_MIN_LENGTH
import com.simplifiedpayment.core.common.messages.PaymentMessages.PASSWORD_MUST_NOT_BE_BLANK

data class Password(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            PASSWORD_MUST_NOT_BE_BLANK
        }

        require(value.length >= MIN_LENGTH) {
            PASSWORD_MUST_HAVE_MIN_LENGTH
        }
    }

    companion object {
        private const val MIN_LENGTH = 6
    }
}
