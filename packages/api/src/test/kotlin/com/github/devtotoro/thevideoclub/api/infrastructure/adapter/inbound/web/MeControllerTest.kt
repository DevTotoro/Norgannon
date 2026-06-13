package com.github.devtotoro.thevideoclub.api.infrastructure.adapter.inbound.web

import com.github.devtotoro.thevideoclub.api.domain.user.User
import com.github.devtotoro.thevideoclub.api.domain.user.UserProvisioningService
import com.github.devtotoro.thevideoclub.api.infrastructure.config.SecurityConfig
import com.github.devtotoro.thevideoclub.api.infrastructure.config.WebConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(MeController::class)
@Import(SecurityConfig::class, WebConfig::class)
class MeControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userProvisioningService: UserProvisioningService

    @MockitoBean
    lateinit var jwtDecoder: JwtDecoder

    @Test
    fun `returns the provisioned user for an authenticated request`() {
        val externalId = "ext-123"
        val email = "dev@thevideoclub.test"
        Mockito
            .`when`(userProvisioningService.provision(externalId, email))
            .thenReturn(User(externalId = externalId, email = email))

        mockMvc
            .get("/api/me") {
                with(jwt().jwt { it.subject(externalId).claim("email", email) })
            }.andExpect {
                status { isOk() }
                jsonPath("$.email") { value(email) }
                jsonPath("$.id") { exists() }
            }

        Mockito.verify(userProvisioningService).provision(externalId, email)
    }

    @Test
    fun `returns 401 when the token has no email claim`() {
        mockMvc
            .get("/api/me") {
                with(jwt().jwt { it.subject("ext-123") })
            }.andExpect {
                status { isUnauthorized() }
            }

        Mockito.verifyNoInteractions(userProvisioningService)
    }

    @Test
    fun `returns 401 when the request is unauthenticated`() {
        mockMvc
            .get("/api/me")
            .andExpect {
                status { isUnauthorized() }
            }

        Mockito.verifyNoInteractions(userProvisioningService)
    }
}
