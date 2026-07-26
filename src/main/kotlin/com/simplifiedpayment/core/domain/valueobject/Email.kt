package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.core.common.messages.PaymentMessages.EMAIL_MUST_BE_VALID
import com.simplifiedpayment.core.common.messages.PaymentMessages.EMAIL_MUST_NOT_BE_BLANK

data class Email(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            EMAIL_MUST_NOT_BE_BLANK
        }

        require(EMAIL_REGEX.matches(value)) {
            EMAIL_MUST_BE_VALID
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
