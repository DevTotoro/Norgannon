package com.github.devtotoro.thevideoclub.api.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByExternalId(externalId: String): User?
}
