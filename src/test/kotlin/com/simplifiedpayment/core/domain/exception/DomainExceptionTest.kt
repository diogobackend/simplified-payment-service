package com.simplifiedpayment.core.domain.exception

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.common.messages.PaymentMessages.MERCHANT_CANNOT_SEND_MONEY
import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_NOT_AUTHORIZED
import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_ALREADY_EXISTS_WITH_DOCUMENT
import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_ALREADY_EXISTS_WITH_EMAIL
import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_NOT_FOUND_WITH_ID
import com.simplifiedpayment.core.common.messages.PaymentMessages.WALLET_NOT_FOUND_WITH_USER_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DomainExceptionTest : UnitTest() {
    @Test
    fun `should create user not found exception`() {
        val exception = UserNotFoundException(USER_ID)

        assertThat(exception.message)
            .isEqualTo("$USER_NOT_FOUND_WITH_ID: $USER_ID")
    }

    @Test
    fun `should create wallet not found exception`() {
        val exception = WalletNotFoundException(USER_ID)

        assertThat(exception.message)
            .isEqualTo("$WALLET_NOT_FOUND_WITH_USER_ID: $USER_ID")
    }

    @Test
    fun `should create user already exists exception with document`() {
        val exception = UserAlreadyExistsException.withDocument(DOCUMENT)

        assertThat(exception.message)
            .isEqualTo("$USER_ALREADY_EXISTS_WITH_DOCUMENT: $DOCUMENT")
    }

    @Test
    fun `should create user already exists exception with email`() {
        val exception = UserAlreadyExistsException.withEmail(EMAIL)

        assertThat(exception.message)
            .isEqualTo("$USER_ALREADY_EXISTS_WITH_EMAIL: $EMAIL")
    }

    @Test
    fun `should create merchant cannot send money exception`() {
        val exception = MerchantCannotSendMoneyException()

        assertThat(exception.message)
            .isEqualTo(MERCHANT_CANNOT_SEND_MONEY)
    }

    @Test
    fun `should create transfer not authorized exception`() {
        val exception = TransferNotAuthorizedException()

        assertThat(exception.message)
            .isEqualTo(TRANSFER_NOT_AUTHORIZED)
    }

    companion object {
        private const val USER_ID = 1L
        private const val DOCUMENT = "12345678901"
        private const val EMAIL = "diogobackend@test.com"
    }
}
