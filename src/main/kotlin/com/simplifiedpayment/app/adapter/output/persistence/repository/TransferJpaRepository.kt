package com.simplifiedpayment.app.adapter.output.persistence.repository

import com.simplifiedpayment.app.adapter.output.persistence.entity.TransferEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransferJpaRepository : JpaRepository<TransferEntity, String>
