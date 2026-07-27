package com.simplifiedpayment.core.usecase

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.builder.buildTransferMoneyInput
import com.simplifiedpayment.core.builder.buildUserDomain
import com.simplifiedpayment.core.builder.buildWalletDomain
import com.simplifiedpayment.core.domain.exception.MerchantCannotSendMoneyException
import com.simplifiedpayment.core.domain.exception.TransferNotAuthorizedException
import com.simplifiedpayment.core.domain.exception.UserNotFoundException
import com.simplifiedpayment.core.domain.exception.WalletNotFoundException
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.domain.model.TransferStatus
import com.simplifiedpayment.core.domain.model.TransferStatus.COMPLETED
import com.simplifiedpayment.core.domain.model.TransferStatus.CREATED
import com.simplifiedpayment.core.domain.model.UserType
import com.simplifiedpayment.core.domain.model.UserType.COMMON
import com.simplifiedpayment.core.domain.model.UserType.MERCHANT
import com.simplifiedpayment.core.domain.model.Wallet
import com.simplifiedpayment.core.port.input.TransferMoneyInput
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import com.simplifiedpayment.core.port.output.TransferRepositoryPort
import com.simplifiedpayment.core.port.output.UserRepositoryPort
import com.simplifiedpayment.core.port.output.WalletRepositoryPort
import io.mockk.MockKMatcherScope
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

        mockUsersAndWallets(input)

        mockAuthorizedTransferPersistence()
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
                match { matchesTransfer(it, input, CREATED) },
            )

            transferRepositoryPort.save(
                match { matchesTransfer(it, input, COMPLETED) },
            )

            notifyPayeePort.notify(
                match { matchesTransfer(it, input, COMPLETED) },
            )
        }

        verify(exactly = 1) {
            walletRepositoryPort.save(matchWallet(input.payer, PAYER_FINAL_BALANCE))
            walletRepositoryPort.save(matchWallet(input.payee, PAYEE_FINAL_BALANCE))
        }
    }

    @Test
    fun `should throw exception when payer is merchant`() {
        val input = buildTransferMoneyInput()
        mockUsers(
            input = input,
            payerType = MERCHANT,
            payeeType = COMMON,
        )

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(MerchantCannotSendMoneyException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
        }

        verify(exactly = 0) {
            walletRepositoryPort.findByUserId(any())
        }
        verifyNoAuthorizationPersistenceOrNotification()
    }

    @Test
    fun `should throw exception when transfer is not authorized`() {
        val input = buildTransferMoneyInput()

        mockUsersAndWallets(input)

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
                match { matchesTransfer(it, input, CREATED) },
            )
        }

        verifyNoTransferPersistenceOrNotification()
    }

    @Test
    fun `should complete transfer when notification fails`() {
        val input = buildTransferMoneyInput()

        mockUsersAndWallets(input)

        mockAuthorizedTransferPersistence()
        every { notifyPayeePort.notify(any()) } throws RuntimeException(NOTIFICATION_FAILED)

        val result = transferMoneyUseCase.transfer(input)

        assertThat(result.status).isEqualTo(COMPLETED)

        verify(exactly = 1) {
            transferRepositoryPort.save(match { matchesTransfer(it, input, COMPLETED) })
            notifyPayeePort.notify(match { matchesTransfer(it, input, COMPLETED) })
        }
    }

    @Test
    fun `should throw exception when payer has insufficient balance`() {
        val input = buildTransferMoneyInput()

        mockUsersAndWallets(
            input = input,
            payerBalance = INSUFFICIENT_PAYER_BALANCE,
        )

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(IllegalArgumentException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(input.payer)
            walletRepositoryPort.findByUserId(input.payee)
        }

        verifyNoAuthorizationPersistenceOrNotification()
    }

    @Test
    fun `should throw exception when payer is not found`() {
        val input = buildTransferMoneyInput()

        every { userRepositoryPort.findById(input.payer) } returns null

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(UserNotFoundException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
        }

        verify(exactly = 0) {
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(any())
        }

        verifyNoAuthorizationPersistenceOrNotification()
    }

    @Test
    fun `should throw exception when payee is not found`() {
        val input = buildTransferMoneyInput()
        val payer = buildUserDomain(userId = input.payer, type = COMMON)

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns null

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(UserNotFoundException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
        }

        verify(exactly = 0) {
            walletRepositoryPort.findByUserId(any())
        }

        verifyNoAuthorizationPersistenceOrNotification()
    }

    @Test
    fun `should throw exception when payer wallet is not found`() {
        val input = buildTransferMoneyInput()

        mockUsers(input)

        every { walletRepositoryPort.findByUserId(input.payer) } returns null

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(WalletNotFoundException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(input.payer)
        }

        verify(exactly = 0) {
            walletRepositoryPort.findByUserId(input.payee)
        }

        verifyNoAuthorizationPersistenceOrNotification()
    }

    @Test
    fun `should throw exception when payee wallet is not found`() {
        val input = buildTransferMoneyInput()
        val payerWallet = buildWalletDomain(userId = input.payer, balance = PAYER_INITIAL_BALANCE)

        mockUsers(input)

        every { walletRepositoryPort.findByUserId(input.payer) } returns payerWallet
        every { walletRepositoryPort.findByUserId(input.payee) } returns null

        assertThatThrownBy {
            transferMoneyUseCase.transfer(input)
        }.isInstanceOf(WalletNotFoundException::class.java)

        verify(exactly = 1) {
            userRepositoryPort.findById(input.payer)
            userRepositoryPort.findById(input.payee)
            walletRepositoryPort.findByUserId(input.payer)
            walletRepositoryPort.findByUserId(input.payee)
        }

        verifyNoAuthorizationPersistenceOrNotification()
    }

    private fun mockUsersAndWallets(
        input: TransferMoneyInput,
        payerType: UserType = COMMON,
        payeeType: UserType = MERCHANT,
        payerBalance: BigDecimal = PAYER_INITIAL_BALANCE,
        payeeBalance: BigDecimal = PAYEE_INITIAL_BALANCE,
    ) {
        mockUsers(
            input = input,
            payerType = payerType,
            payeeType = payeeType,
        )

        val payerWallet = buildWalletDomain(userId = input.payer, balance = payerBalance)
        val payeeWallet = buildWalletDomain(userId = input.payee, balance = payeeBalance)

        every { walletRepositoryPort.findByUserId(input.payer) } returns payerWallet
        every { walletRepositoryPort.findByUserId(input.payee) } returns payeeWallet
    }

    private fun matchesTransfer(
        transfer: Transfer,
        input: TransferMoneyInput,
        status: TransferStatus,
    ): Boolean =
        transfer.payerId == input.payer &&
            transfer.payeeId == input.payee &&
            transfer.value.value.compareTo(input.value) == 0 &&
            transfer.status == status

    private fun MockKMatcherScope.matchWallet(
        userId: Long,
        balance: BigDecimal,
    ): Wallet =
        match {
            it.userId == userId &&
                it.balance.value.compareTo(balance) == 0
        }

    private fun mockUsers(
        input: TransferMoneyInput,
        payerType: UserType = COMMON,
        payeeType: UserType = MERCHANT,
    ) {
        val payer = buildUserDomain(userId = input.payer, type = payerType)
        val payee = buildUserDomain(userId = input.payee, type = payeeType)

        every { userRepositoryPort.findById(input.payer) } returns payer
        every { userRepositoryPort.findById(input.payee) } returns payee
    }

    private fun mockAuthorizedTransferPersistence() {
        every { authorizeTransferPort.authorize(any()) } returns true
        every { walletRepositoryPort.save(any()) } answers { firstArg() }
        every { transferRepositoryPort.save(any()) } answers { firstArg<Transfer>() }
    }

    private fun verifyNoTransferPersistenceOrNotification() {
        verify(exactly = 0) {
            walletRepositoryPort.save(any())
            transferRepositoryPort.save(any())
            notifyPayeePort.notify(any())
        }
    }

    private fun verifyNoAuthorizationPersistenceOrNotification() {
        verify(exactly = 0) {
            authorizeTransferPort.authorize(any())
            walletRepositoryPort.save(any())
            transferRepositoryPort.save(any())
            notifyPayeePort.notify(any())
        }
    }

    companion object {
        private const val NOTIFICATION_FAILED = "Notification failed"

        private val PAYER_INITIAL_BALANCE = BigDecimal("200.00")
        private val PAYEE_INITIAL_BALANCE = BigDecimal("50.00")
        private val INSUFFICIENT_PAYER_BALANCE = BigDecimal("50.00")
        private val PAYER_FINAL_BALANCE = BigDecimal("100.00")
        private val PAYEE_FINAL_BALANCE = BigDecimal("150.00")
    }
}
