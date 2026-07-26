package com.simplifiedpayment.core.usecase

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.domain.exception.MerchantCannotSendMoneyException
import com.simplifiedpayment.core.domain.exception.TransferNotAuthorizedException
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.domain.model.TransferStatus.COMPLETED
import com.simplifiedpayment.core.domain.model.TransferStatus.CREATED
import com.simplifiedpayment.core.domain.model.UserType.COMMON
import com.simplifiedpayment.core.domain.model.UserType.MERCHANT
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import com.simplifiedpayment.core.port.output.TransferRepositoryPort
import com.simplifiedpayment.core.port.output.UserRepositoryPort
import com.simplifiedpayment.core.port.output.WalletRepositoryPort
import com.simplifiedpayment.core.usecase.builder.buildTransferMoneyInput
import com.simplifiedpayment.core.usecase.builder.buildUserDomain
import com.simplifiedpayment.core.usecase.builder.buildWalletDomain
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransferMoneyUseCaseTest(
    @param:MockK private val userRepositoryPort: UserRepositoryPort,
    @param:MockK private val walletRepositoryPort: WalletRepositoryPort,
    @param:MockK private val transferRepositoryPort: TransferRepositoryPort,
    @param:MockK private val authorizeTransferPort: AuthorizeTransferPort,
    @param:MockK private val notifyPayeePort: NotifyPayeePort,
    @param:InjectMockKs private val transferMoneyUseCase: TransferMoneyUseCase,
) : UnitTest() {
    @Test
    fun `should transfer money successfully`() {
        val input = buildTransferMoneyInput()
        val payer = buildUserDomain(userId = input.payer, type = COMMON)
        val payee = buildUserDomain(userId = input.payee, type = MERCHANT)
        val payerWallet = buildWalletDomain(userId = input.payer, balance = BigDecimal("200.00"))
        val payeeWallet = buildWalletDomain(userId = input.payee, balance = BigDecimal("50.00"))

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns payee
        every { walletRepositoryPort.findByUserId(input.payer) } returns payerWallet
        every { walletRepositoryPort.findByUserId(input.payee) } returns payeeWallet
        every { authorizeTransferPort.authorize(any()) } returns true
        every { walletRepositoryPort.save(any()) } answers { firstArg() }
        every { transferRepositoryPort.save(any()) } answers { firstArg<Transfer>() }
        justRun { notifyPayeePort.notify(any()) }

        val result = transferMoneyUseCase.transfer(input)

        assertThat(result.payerId).isEqualTo(input.payer)
        assertThat(result.payeeId).isEqualTo(input.payee)
        assertThat(result.value.value).isEqualByComparingTo(input.value)
        assertThat(result.status).isEqualTo(COMPLETED)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(input.payer)
            walletRepositoryPort.findByUserId(input.payee)

            authorizeTransferPort.authorize(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == CREATED
                },
            )

            transferRepositoryPort.save(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == COMPLETED
                },
            )

            notifyPayeePort.notify(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == COMPLETED
                },
            )
        }

        verify(exactly = 1) {
            walletRepositoryPort.save(
                match {
                    it.userId == input.payer &&
                        it.balance.value.compareTo(BigDecimal("100.00")) == 0
                },
            )
            walletRepositoryPort.save(
                match {
                    it.userId == input.payee &&
                        it.balance.value.compareTo(BigDecimal("150.00")) == 0
                },
            )
        }
    }

    @Test
    fun `should throw exception when payer is merchant`() {
        val input = buildTransferMoneyInput()
        val payer = buildUserDomain(userId = input.payer, type = MERCHANT)
        val payee = buildUserDomain(userId = input.payee, type = COMMON)

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns payee

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(MerchantCannotSendMoneyException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
        }

        verify(exactly = 0) {
            walletRepositoryPort.findByUserId(any())
            authorizeTransferPort.authorize(any())
            walletRepositoryPort.save(any())
            transferRepositoryPort.save(any())
            notifyPayeePort.notify(any())
        }
    }

    @Test
    fun `should throw exception when transfer is not authorized`() {
        val input = buildTransferMoneyInput()
        val payer = buildUserDomain(userId = input.payer, type = COMMON)
        val payee = buildUserDomain(userId = input.payee, type = MERCHANT)
        val payerWallet = buildWalletDomain(userId = input.payer, balance = BigDecimal("200.00"))
        val payeeWallet = buildWalletDomain(userId = input.payee, balance = BigDecimal("50.00"))

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns payee
        every { walletRepositoryPort.findByUserId(input.payer) } returns payerWallet
        every { walletRepositoryPort.findByUserId(input.payee) } returns payeeWallet
        every { authorizeTransferPort.authorize(any()) } returns false

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(TransferNotAuthorizedException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(input.payer)
            walletRepositoryPort.findByUserId(input.payee)
            authorizeTransferPort.authorize(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == CREATED
                },
            )
        }

        verify(exactly = 0) {
            walletRepositoryPort.save(any())
            transferRepositoryPort.save(any())
            notifyPayeePort.notify(any())
        }
    }

    @Test
    fun `should complete transfer when notification fails`() {
        val input = buildTransferMoneyInput()
        val payer = buildUserDomain(userId = input.payer, type = COMMON)
        val payee = buildUserDomain(userId = input.payee, type = MERCHANT)
        val payerWallet = buildWalletDomain(userId = input.payer, balance = BigDecimal("200.00"))
        val payeeWallet = buildWalletDomain(userId = input.payee, balance = BigDecimal("50.00"))

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns payee
        every { walletRepositoryPort.findByUserId(input.payer) } returns payerWallet
        every { walletRepositoryPort.findByUserId(input.payee) } returns payeeWallet
        every { authorizeTransferPort.authorize(any()) } returns true
        every { walletRepositoryPort.save(any()) } answers { firstArg() }
        every { transferRepositoryPort.save(any()) } answers { firstArg<Transfer>() }
        every { notifyPayeePort.notify(any()) } throws RuntimeException(NOTIFICATION_FAILED)

        val result = transferMoneyUseCase.transfer(input)

        assertThat(result.status).isEqualTo(COMPLETED)

        verify(exactly = 1) {
            transferRepositoryPort.save(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == COMPLETED
                },
            )

            notifyPayeePort.notify(
                match {
                    it.payerId == input.payer &&
                        it.payeeId == input.payee &&
                        it.value.value.compareTo(input.value) == 0 &&
                        it.status == COMPLETED
                },
            )
        }
    }

    companion object {
        private const val NOTIFICATION_FAILED = "Notification failed"
    }
}
