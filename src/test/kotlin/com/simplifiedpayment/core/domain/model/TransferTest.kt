package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.builder.buildTransferDomain
import com.simplifiedpayment.core.domain.model.TransferStatus.AUTHORIZED
import com.simplifiedpayment.core.domain.model.TransferStatus.COMPLETED
import com.simplifiedpayment.core.domain.model.TransferStatus.CREATED
import com.simplifiedpayment.core.domain.model.TransferStatus.FAILED
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransferTest : UnitTest() {
    @Test
    fun `should create transfer successfully`() {
        val transfer =
            buildTransferDomain(
                payerId = PAYER_ID,
                payeeId = PAYEE_ID,
                value = TRANSFER_VALUE,
            )

        assertThat(transfer.payerId).isEqualTo(PAYER_ID)
        assertThat(transfer.payeeId).isEqualTo(PAYEE_ID)
        assertThat(transfer.value.value).isEqualByComparingTo(TRANSFER_VALUE)
        assertThat(transfer.status).isEqualTo(CREATED)
    }

    @Test
    fun `should authorize transfer`() {
        val transfer = buildTransferDomain()

        val result = transfer.authorize()

        assertThat(result.status).isEqualTo(AUTHORIZED)
    }

    @Test
    fun `should complete transfer`() {
        val transfer = buildTransferDomain(status = AUTHORIZED)

        val result = transfer.complete()

        assertThat(result.status).isEqualTo(COMPLETED)
    }

    @Test
    fun `should fail transfer`() {
        val transfer = buildTransferDomain()

        val result = transfer.fail()

        assertThat(result.status).isEqualTo(FAILED)
    }

    @Test
    fun `should throw exception when payer id is invalid`() {
        assertThatThrownBy {
            buildTransferDomain(payerId = 0L)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when payee id is invalid`() {
        assertThatThrownBy {
            buildTransferDomain(payeeId = 0L)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when payer and payee are the same`() {
        assertThatThrownBy {
            buildTransferDomain(
                payerId = PAYER_ID,
                payeeId = PAYER_ID,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when transfer value is zero`() {
        assertThatThrownBy {
            buildTransferDomain(value = BigDecimal.ZERO)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should expose transfer generated properties`() {
        val transfer = buildTransferDomain()

        assertThat(transfer.transferId).isNotNull()
        assertThat(transfer.createdAt).isNotNull()
        assertThat(transfer.updatedAt).isNull()
    }

    companion object {
        private const val PAYER_ID = 1L
        private const val PAYEE_ID = 2L

        private val TRANSFER_VALUE = BigDecimal("100.00")
    }
}
