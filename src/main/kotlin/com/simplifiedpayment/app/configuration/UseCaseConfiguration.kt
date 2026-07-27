package com.simplifiedpayment.app.configuration

import com.simplifiedpayment.core.port.input.TransferMoneyPort
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import com.simplifiedpayment.core.port.output.TransferRepositoryPort
import com.simplifiedpayment.core.port.output.UserRepositoryPort
import com.simplifiedpayment.core.port.output.WalletRepositoryPort
import com.simplifiedpayment.core.usecase.TransferMoneyUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfiguration {
    @Bean
    fun transferMoneyPort(
        userRepositoryPort: UserRepositoryPort,
        walletRepositoryPort: WalletRepositoryPort,
        transferRepositoryPort: TransferRepositoryPort,
        authorizeTransferPort: AuthorizeTransferPort,
        notifyPayeePort: NotifyPayeePort,
    ): TransferMoneyPort =
        TransferMoneyUseCase(
            userRepositoryPort = userRepositoryPort,
            walletRepositoryPort = walletRepositoryPort,
            transferRepositoryPort = transferRepositoryPort,
            authorizeTransferPort = authorizeTransferPort,
            notifyPayeePort = notifyPayeePort,
        )
}
