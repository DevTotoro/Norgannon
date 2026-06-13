package com.github.devtotoro.thevideoclub.api.domain.user

import com.github.devtotoro.thevideoclub.api.support.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Transactional
class UserProvisioningServiceIT : BaseIntegrationTest() {
    @Autowired
    lateinit var userProvisioningService: UserProvisioningService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `provision creates and persists a new user when the external id is unknown`() {
        val externalId = UUID.randomUUID().toString()
        val email = "$externalId@thevideoclub.test"
        val countBefore = userRepository.count()

        val user = userProvisioningService.provision(externalId, email)

        assertEquals(countBefore + 1, userRepository.count(), "A previously unknown user must be inserted")
        assertEquals(externalId, user.externalId)
        assertEquals(email, user.email)
        assertNotNull(userRepository.findByExternalId(externalId), "The new user must be retrievable by external id")
    }

    @Test
    fun `provision returns the existing user without inserting a duplicate`() {
        val externalId = UUID.randomUUID().toString()
        val email = "$externalId@thevideoclub.test"

        val first = userProvisioningService.provision(externalId, email)
        val countAfterFirst = userRepository.count()

        val second = userProvisioningService.provision(externalId, email)

        assertEquals(first.id, second.id, "Re-provisioning the same external id must return the same user")
        assertEquals(countAfterFirst, userRepository.count(), "Re-provisioning must not insert a second row")
    }

    @Test
    fun `provision updates the email in place when it has changed upstream`() {
        val externalId = UUID.randomUUID().toString()
        val originalEmail = "$externalId@thevideoclub.test"
        val updatedEmail = "$externalId-updated@thevideoclub.test"

        val first = userProvisioningService.provision(externalId, originalEmail)
        userRepository.flush()
        val countAfterFirst = userRepository.count()

        val updated = userProvisioningService.provision(externalId, updatedEmail)

        assertEquals(first.id, updated.id, "Email changes must mutate the existing user, not create a new one")
        assertEquals(updatedEmail, updated.email)
        assertEquals(countAfterFirst, userRepository.count(), "Updating email must not insert a new row")
        assertEquals(
            updatedEmail,
            userRepository.findByExternalId(externalId)?.email,
            "The updated email must be persisted",
        )
    }
}
