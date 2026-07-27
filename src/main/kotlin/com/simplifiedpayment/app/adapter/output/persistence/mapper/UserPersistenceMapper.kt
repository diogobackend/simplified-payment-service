package com.simplifiedpayment.app.adapter.output.persistence.mapper

import com.simplifiedpayment.app.adapter.output.persistence.entity.UserEntity
import com.simplifiedpayment.core.domain.model.User
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email
import com.simplifiedpayment.core.domain.valueobject.Password
import org.springframework.stereotype.Component

@Component
class UserPersistenceMapper {
    fun toDomain(entity: UserEntity): User =
        User(
            userId = entity.id,
            fullName = entity.fullName,
            document = Document(entity.document),
            email = Email(entity.email),
            password = Password(entity.password),
            type = entity.type,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: User): UserEntity =
        UserEntity(
            id = domain.userId,
            fullName = domain.fullName,
            document = domain.document.value,
            email = domain.email.value,
            password = domain.password.value,
            type = domain.type,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
