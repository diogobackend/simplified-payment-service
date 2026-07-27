package com.simplifiedpayment.app.adapter.output.persistence.entity

import com.simplifiedpayment.core.domain.model.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String,
    @Column(name = "document", nullable = false, unique = true, length = 14)
    var document: String,
    @Column(name = "email", nullable = false, unique = true, length = 150)
    var email: String,
    @Column(name = "password", nullable = false)
    var password: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var type: UserType,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
