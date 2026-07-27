package com.simplifiedpayment.app.adapter.output.persistence.repository

import com.simplifiedpayment.app.adapter.output.persistence.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WalletJpaRepository : JpaRepository<WalletEntity, Long> {
    fun findByUserId(userId: Long): WalletEntity?
}
