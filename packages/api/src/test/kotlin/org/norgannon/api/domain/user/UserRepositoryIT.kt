package org.norgannon.api.domain.user

import org.junit.jupiter.api.Test
import org.norgannon.api.support.BaseIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Transactional
class UserRepositoryIT : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `should successfully generate a valid 64-bit TSID primary key on save`() {
        val user = User(email = "primary-key@norgannon.org", externalId = UUID.randomUUID().toString())

        val savedUser = userRepository.save(user)

        assertNotNull(savedUser.id, "The primary key should be automatically assigned by our BaseEntity")
        assertTrue(savedUser.id > 0, "TSID must be a positive, non-zero 64-bit Long identifier")
    }

    @Test
    fun `should verify public id encoding matches expected base32 format and length`() {
        val user = User(email = "public-id@norgannon.org", externalId = UUID.randomUUID().toString())

        val savedUser = userRepository.save(user)
        val publicId = savedUser.getPublicId()

        assertNotNull(publicId, "The Crockford Base32 public string id must be accessible")
        assertEquals(
            13,
            publicId.length,
            "A standard 64-bit TSID encoded to Crockford Base32 must be exactly 13 characters",
        )

        val illegalCharacters = listOf('I', 'O', 'L', 'U', 'i', 'o', 'l', 'u')
        val containsIllegalChars = publicId.any { it in illegalCharacters }
        assertTrue(!containsIllegalChars, "Public ID '$publicId' contains invalid Crockford Base32 characters")
    }

    @Test
    fun `should automatically populate auditing timestamps upon initial persistence`() {
        val user = User(email = "auditing@norgannon.org", externalId = UUID.randomUUID().toString())

        val savedUser = userRepository.save(user)

        assertNotNull(savedUser.createdAt, "createdAt timestamp must be automatically injected by JpaAuditing")
        assertNotNull(savedUser.updatedAt, "updatedAt timestamp must be automatically injected by JpaAuditing")
        assertEquals(savedUser.createdAt, savedUser.updatedAt, "During creation, both timestamps should match exactly")
    }

    @Test
    fun `should execute standard database round-trip read and write operations without data loss`() {
        val targetExternalId = UUID.randomUUID().toString()
        val user = User(email = "round-trip@norgannon.org", externalId = targetExternalId)
        val savedUser = userRepository.save(user)

        userRepository.flush()

        val retrievedUser = userRepository.findById(savedUser.id).orElseThrow()

        assertEquals(savedUser.id, retrievedUser.id)
        assertEquals("round-trip@norgannon.org", retrievedUser.email)
        assertEquals(targetExternalId, retrievedUser.externalId)
    }
}
