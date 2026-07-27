package com.simplifiedpayment.core.usecase

import com.simplifiedpayment.app.configuration.logs.LogInfo
import com.simplifiedpayment.core.domain.exception.MerchantCannotSendMoneyException
import com.simplifiedpayment.core.domain.exception.TransferNotAuthorizedException
import com.simplifiedpayment.core.domain.exception.UserNotFoundException
import com.simplifiedpayment.core.domain.exception.WalletNotFoundException
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.domain.valueobject.Money
import com.simplifiedpayment.core.port.input.TransferMoneyInput
import com.simplifiedpayment.core.port.input.TransferMoneyPort
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import com.simplifiedpayment.core.port.output.TransferRepositoryPort
import com.simplifiedpayment.core.port.output.UserRepositoryPort
import com.simplifiedpayment.core.port.output.WalletRepositoryPort
import org.springframework.transaction.annotation.Transactional

open class TransferMoneyUseCase(
    private val userRepositoryPort: UserRepositoryPort,
    private val walletRepositoryPort: WalletRepositoryPort,
    private val transferRepositoryPort: TransferRepositoryPort,
    private val authorizeTransferPort: AuthorizeTransferPort,
    private val notifyPayeePort: NotifyPayeePort,
) : TransferMoneyPort {
    @Transactional
    @LogInfo(logParameters = true, logReturn = true)
    override fun transfer(input: TransferMoneyInput): Transfer {
        val payer =
            userRepositoryPort.findById(input.payer)
                ?: throw UserNotFoundException(input.payer)

        userRepositoryPort.findById(input.payee)
            ?: throw UserNotFoundException(input.payee)

        if (!payer.canSendMoney()) {
            throw MerchantCannotSendMoneyException()
        }

        val payerWallet =
            walletRepositoryPort.findByUserId(input.payer)
                ?: throw WalletNotFoundException(input.payer)

        val payeeWallet =
            walletRepositoryPort.findByUserId(input.payee)
                ?: throw WalletNotFoundException(input.payee)

        val transfer =
            Transfer(
                payerId = input.payer,
                payeeId = input.payee,
                value = Money(input.value),
            )

        val debitedPayerWallet = payerWallet.debit(transfer.value)
        val creditedPayeeWallet = payeeWallet.credit(transfer.value)

        if (!authorizeTransferPort.authorize(transfer)) {
            throw TransferNotAuthorizedException()
        }

        walletRepositoryPort.save(debitedPayerWallet)
        walletRepositoryPort.save(creditedPayeeWallet)

        val completedTransfer =
            transferRepositoryPort.save(
                transfer
                    .authorize()
                    .complete(),
            )

        runCatching {
            notifyPayeePort.notify(completedTransfer)
        }

        return completedTransfer
    }
}
