package com.simplifiedpayment.core.domain.exception

import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_ALREADY_EXISTS_WITH_DOCUMENT
import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_ALREADY_EXISTS_WITH_EMAIL

class UserAlreadyExistsException private constructor(
    message: String,
) : RuntimeException(message) {
    companion object {
        fun withDocument(document: String): UserAlreadyExistsException =
            UserAlreadyExistsException("$USER_ALREADY_EXISTS_WITH_DOCUMENT: $document")

        fun withEmail(email: String): UserAlreadyExistsException = UserAlreadyExistsException("$USER_ALREADY_EXISTS_WITH_EMAIL: $email")
    }
}
