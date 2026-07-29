package com.simplifiedpayment.integration

import com.simplifiedpayment.IntegrationTest
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.isEqualTo
import java.math.BigDecimal

class TransferIntegrationTest : IntegrationTest() {
    @BeforeEach
    fun setup() {
        database.deleteTransfers()
        database.updateWalletBalance(PAYER_ID, PAYER_INITIAL_BALANCE)
        database.updateWalletBalance(PAYEE_ID, PAYEE_INITIAL_BALANCE)
    }

    @Test
    fun `should create transfer with success`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYER_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.payer").value(PAYER_ID))
            .andExpect(jsonPath("$.payee").value(PAYEE_ID))
            .andExpect(jsonPath("$.value").value(TRANSFER_VALUE.toDouble()))
            .andExpect(jsonPath("$.status").value(COMPLETED_STATUS))

        database.assertWalletBalance(PAYER_ID, PAYER_EXPECTED_BALANCE)
        database.assertWalletBalance(PAYEE_ID, PAYEE_EXPECTED_BALANCE)
        database.assertCompletedTransferExists(PAYER_ID, PAYEE_ID)
        notifyPayeePort.assertNotificationWasSent()
    }

    @Test
    fun `should return forbidden when transfer is not authorized`() {
        authorizeTransferPort.denyTransfers()

        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYER_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message").value(TRANSFER_NOT_AUTHORIZED_MESSAGE))

        database.assertWalletBalance(PAYER_ID, PAYER_INITIAL_BALANCE)
        database.assertWalletBalance(PAYEE_ID, PAYEE_INITIAL_BALANCE)
        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return unprocessable content when payer is merchant`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYEE_ID,
            payee = PAYER_ID,
        ).andExpect(status().isEqualTo(422))
            .andExpect(jsonPath("$.message").value(MERCHANT_CANNOT_SEND_MONEY_MESSAGE))

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return bad request when payer has insufficient balance`() {
        performTransfer(
            value = BigDecimal("999999.00"),
            payer = PAYER_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(WALLET_BALANCE_INSUFFICIENT_MESSAGE))

        database.assertWalletBalance(PAYER_ID, PAYER_INITIAL_BALANCE)
        database.assertWalletBalance(PAYEE_ID, PAYEE_INITIAL_BALANCE)
        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return not found when payer does not exist`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = UNKNOWN_USER_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value(containsString("User not found with id")))

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return not found when payee does not exist`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYER_ID,
            payee = UNKNOWN_USER_ID,
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value(containsString("User not found with id")))

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return not found when payer wallet does not exist`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = COMMON_USER_WITHOUT_WALLET_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value(containsString("Wallet not found with userId")))

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return not found when payee wallet does not exist`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYER_ID,
            payee = MERCHANT_WITHOUT_WALLET_ID,
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value(containsString("Wallet not found with userId")))

        database.assertWalletBalance(PAYER_ID, PAYER_INITIAL_BALANCE)
        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return bad request when payer and payee are the same user`() {
        performTransfer(
            value = TRANSFER_VALUE,
            payer = PAYER_ID,
            payee = PAYER_ID,
        ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(PAYER_AND_PAYEE_MUST_BE_DIFFERENT_MESSAGE))

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    @Test
    fun `should return bad request when value is zero`() {
        performTransfer(
            value = BigDecimal.ZERO,
            payer = PAYER_ID,
            payee = PAYEE_ID,
        ).andExpect(status().isBadRequest)

        database.assertTransferCount(0)
        notifyPayeePort.assertNotificationWasNotSent()
    }

    private fun performTransfer(
        value: BigDecimal,
        payer: Long,
        payee: Long,
    ): ResultActions =
        mockMvc.perform(
            post("/transfer")
                .contentType(APPLICATION_JSON)
                .content(buildTransferRequestJson(value, payer, payee)),
        )

    private fun buildTransferRequestJson(
        value: BigDecimal,
        payer: Long,
        payee: Long,
    ): String =
        """
        {
          "value": $value,
          "payer": $payer,
          "payee": $payee
        }
        """.trimIndent()

    companion object {
        private const val PAYER_ID = 1L
        private const val PAYEE_ID = 101L
        private const val UNKNOWN_USER_ID = 999L
        private const val COMMON_USER_WITHOUT_WALLET_ID = 201L
        private const val MERCHANT_WITHOUT_WALLET_ID = 301L

        private const val COMPLETED_STATUS = "COMPLETED"

        private const val TRANSFER_NOT_AUTHORIZED_MESSAGE = "Transfer was not authorized"
        private const val MERCHANT_CANNOT_SEND_MONEY_MESSAGE = "Merchant cannot send money"
        private const val WALLET_BALANCE_INSUFFICIENT_MESSAGE = "Wallet balance is insufficient"
        private const val PAYER_AND_PAYEE_MUST_BE_DIFFERENT_MESSAGE = "Transfer payer and payee must be different"

        private val TRANSFER_VALUE = BigDecimal("100.00")
        private val PAYER_INITIAL_BALANCE = BigDecimal("1000.00")
        private val PAYEE_INITIAL_BALANCE = BigDecimal("0.00")
        private val PAYER_EXPECTED_BALANCE = BigDecimal("900.00")
        private val PAYEE_EXPECTED_BALANCE = BigDecimal("100.00")
    }
}
