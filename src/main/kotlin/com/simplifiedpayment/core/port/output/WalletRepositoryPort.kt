package com.simplifiedpayment.core.port.output

import com.simplifiedpayment.core.domain.model.Wallet

interface WalletRepositoryPort {
    fun save(wallet: Wallet): Wallet

    fun findByUserId(userId: Long): Wallet?
}
