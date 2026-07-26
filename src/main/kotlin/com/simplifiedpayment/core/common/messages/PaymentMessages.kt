package com.simplifiedpayment.core.common.messages

object PaymentMessages {
    const val MONEY_MUST_NOT_BE_NEGATIVE = "Money must not be negative"

    const val DOCUMENT_MUST_NOT_BE_BLANK = "Document must not be blank"
    const val DOCUMENT_MUST_CONTAIN_ONLY_DIGITS = "Document must contain only digits"
    const val DOCUMENT_MUST_HAVE_VALID_LENGTH = "Document must have 11 or 14 digits"

    const val EMAIL_MUST_NOT_BE_BLANK = "Email must not be blank"
    const val EMAIL_MUST_BE_VALID = "Email must be valid"

    const val PASSWORD_MUST_NOT_BE_BLANK = "Password must not be blank"
    const val PASSWORD_MUST_HAVE_MIN_LENGTH = "Password must have at least 6 characters"
}
