package com.simplifiedpayment.app.adapter.output.persistence.adapters

import com.simplifiedpayment.app.adapter.output.persistence.mapper.WalletPersistenceMapper
import com.simplifiedpayment.app.adapter.output.persistence.repository.WalletJpaRepository
import com.simplifiedpayment.core.domain.model.Wallet
import com.simplifiedpayment.core.port.output.WalletRepositoryPort
import org.springframework.stereotype.Component

@Component
class WalletPersistenceAdapter(
    private val walletJpaRepository: WalletJpaRepository,
    private val walletPersistenceMapper: WalletPersistenceMapper,
) : WalletRepositoryPort {
    override fun save(wallet: Wallet): Wallet =
        walletJpaRepository
            .save(walletPersistenceMapper.toEntity(wallet))
            .let(walletPersistenceMapper::toDomain)

    override fun findByUserId(userId: Long): Wallet? =
        walletJpaRepository
            .findByUserId(userId)
            ?.let(walletPersistenceMapper::toDomain)
}
