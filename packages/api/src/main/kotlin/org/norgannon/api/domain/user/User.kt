package com.github.devtotoro.thevideoclub.api.domain.user

import jakarta.persistence.*
import com.github.devtotoro.thevideoclub.api.domain.common.BaseEntity

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_users_external_id", columnNames = ["external_id"]),
        UniqueConstraint(name = "uq_users_email", columnNames = ["email"]),
    ],
    indexes = [
        Index(name = "idx_users_external_id", columnList = "external_id"),
    ],
)
class User(
    @Column(name = "external_id", nullable = false, length = 128)
    val externalId: String,
    @Column(name = "email", nullable = false, length = 255)
    var email: String,
) : BaseEntity()
