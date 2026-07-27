package com.simplifiedpayment.app.adapter.output.persistence.mapper

import com.simplifiedpayment.app.adapter.output.persistence.entity.TransferEntity
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.domain.valueobject.Money
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TransferPersistenceMapper {
    fun toDomain(entity: TransferEntity): Transfer =
        Transfer(
            transferId = UUID.fromString(entity.id),
            payerId = entity.payerId,
            payeeId = entity.payeeId,
            value = Money(entity.value),
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: Transfer): TransferEntity =
        TransferEntity(
            id = domain.transferId.toString(),
            payerId = domain.payerId,
            payeeId = domain.payeeId,
            value = domain.value.value,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
