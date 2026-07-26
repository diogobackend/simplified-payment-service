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

    const val USER_FULL_NAME_MUST_NOT_BE_BLANK = "User full name must not be blank"

    const val WALLET_USER_ID_MUST_BE_POSITIVE = "Wallet userId must be positive"
    const val WALLET_BALANCE_INSUFFICIENT = "Wallet balance is insufficient"

    const val TRANSFER_PAYER_ID_MUST_BE_POSITIVE = "Transfer payerId must be positive"
    const val TRANSFER_PAYEE_ID_MUST_BE_POSITIVE = "Transfer payeeId must be positive"
    const val TRANSFER_PAYER_AND_PAYEE_MUST_BE_DIFFERENT = "Transfer payer and payee must be different"
    const val TRANSFER_VALUE_MUST_BE_POSITIVE = "Transfer value must be positive"

    const val USER_NOT_FOUND_WITH_ID = "User not found with id"
    const val WALLET_NOT_FOUND_WITH_USER_ID = "Wallet not found with userId"
    const val USER_ALREADY_EXISTS_WITH_DOCUMENT = "User already exists with document"
    const val USER_ALREADY_EXISTS_WITH_EMAIL = "User already exists with email"
    const val MERCHANT_CANNOT_SEND_MONEY = "Merchant cannot send money"
    const val TRANSFER_NOT_AUTHORIZED = "Transfer was not authorized"
}
