package com.simplifiedpayment.app.adapter.output.persistence.mapper

import com.simplifiedpayment.app.adapter.output.persistence.entity.WalletEntity
import com.simplifiedpayment.core.domain.model.Wallet
import com.simplifiedpayment.core.domain.valueobject.Money
import org.springframework.stereotype.Component

@Component
class WalletPersistenceMapper {
    fun toDomain(entity: WalletEntity): Wallet =
        Wallet(
            walletId = entity.id,
            userId = entity.userId,
            balance = Money(entity.balance),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: Wallet): WalletEntity =
        WalletEntity(
            id = domain.walletId,
            userId = domain.userId,
            balance = domain.balance.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
