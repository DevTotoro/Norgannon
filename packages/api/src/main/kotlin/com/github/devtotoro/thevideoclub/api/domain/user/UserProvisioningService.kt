package com.github.devtotoro.thevideoclub.api.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProvisioningService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun provision(
        externalId: String,
        email: String,
    ): User {
        val existing = userRepository.findByExternalId(externalId)

        if (existing != null) {
            if (existing.email != email) {
                existing.email = email
            }
            return existing
        }

        return userRepository.save(User(externalId = externalId, email = email))
    }
}
