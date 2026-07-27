package com.simplifiedpayment.app.adapter.output.persistence.adapters

import com.simplifiedpayment.app.adapter.output.persistence.mapper.UserPersistenceMapper
import com.simplifiedpayment.app.adapter.output.persistence.repository.UserJpaRepository
import com.simplifiedpayment.core.domain.model.User
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email
import com.simplifiedpayment.core.port.output.UserRepositoryPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val userPersistenceMapper: UserPersistenceMapper,
) : UserRepositoryPort {
    override fun save(user: User): User =
        userJpaRepository
            .save(userPersistenceMapper.toEntity(user))
            .let(userPersistenceMapper::toDomain)

    override fun findById(userId: Long): User? =
        userJpaRepository
            .findByIdOrNull(userId)
            ?.let(userPersistenceMapper::toDomain)

    override fun existsByDocument(document: Document): Boolean = userJpaRepository.existsByDocument(document.value)

    override fun existsByEmail(email: Email): Boolean = userJpaRepository.existsByEmail(email.value)
}
