package com.simplifiedpayment.app.adapter.output.persistence.entity

import com.simplifiedpayment.core.domain.model.TransferStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transfers")
class TransferEntity(
    @Id
    @Column(name = "id", nullable = false, length = 36)
    var id: String,
    @Column(name = "payer_id", nullable = false)
    var payerId: Long,
    @Column(name = "payee_id", nullable = false)
    var payeeId: Long,
    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    var value: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: TransferStatus,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
