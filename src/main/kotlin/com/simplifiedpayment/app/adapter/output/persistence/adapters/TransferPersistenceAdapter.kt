package com.simplifiedpayment.app.adapter.output.persistence.adapters

import com.simplifiedpayment.app.adapter.output.persistence.mapper.TransferPersistenceMapper
import com.simplifiedpayment.app.adapter.output.persistence.repository.TransferJpaRepository
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.output.TransferRepositoryPort
import org.springframework.stereotype.Component

@Component
class TransferPersistenceAdapter(
    private val transferJpaRepository: TransferJpaRepository,
    private val transferPersistenceMapper: TransferPersistenceMapper,
) : TransferRepositoryPort {
    override fun save(transfer: Transfer): Transfer =
        transferJpaRepository
            .save(transferPersistenceMapper.toEntity(transfer))
            .let(transferPersistenceMapper::toDomain)
}
