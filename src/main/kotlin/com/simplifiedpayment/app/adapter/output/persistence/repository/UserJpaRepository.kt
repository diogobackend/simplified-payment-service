package com.simplifiedpayment.app.adapter.output.persistence.repository

import com.simplifiedpayment.app.adapter.output.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun existsByDocument(document: String): Boolean

    fun existsByEmail(email: String): Boolean
}
