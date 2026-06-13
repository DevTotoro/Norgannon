package com.github.devtotoro.thevideoclub.api.infrastructure.adapter.inbound.web

import com.github.devtotoro.thevideoclub.api.domain.user.UserRepository
import com.github.devtotoro.thevideoclub.api.support.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@AutoConfigureMockMvc
class MeControllerIT : BaseIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `GET me provisions the caller and is idempotent across repeated calls`() {
        val externalId = UUID.randomUUID().toString()
        val email = "$externalId@thevideoclub.test"

        val body =
            mockMvc
                .get("/api/me") {
                    with(jwt().jwt { it.subject(externalId).claim("email", email) })
                }.andExpect {
                    status { isOk() }
                }.andReturn()
                .response.contentAsString

        val persisted = userRepository.findByExternalId(externalId)
        assertNotNull(persisted, "The first call must provision and persist the caller")
        assertTrue(body.contains(email), "Response must echo the caller's email")
        assertTrue(body.contains(persisted.getPublicId()), "Response id must be the user's public id")

        val countAfterFirstCall = userRepository.count()

        mockMvc
            .get("/api/me") {
                with(jwt().jwt { it.subject(externalId).claim("email", email) })
            }.andExpect {
                status { isOk() }
            }

        assertEquals(
            countAfterFirstCall,
            userRepository.count(),
            "A second call for the same subject must not create another user",
        )
    }

    @Test
    fun `GET me rejects unauthenticated requests`() {
        mockMvc
            .get("/api/me")
            .andExpect {
                status { isUnauthorized() }
            }
    }
}
