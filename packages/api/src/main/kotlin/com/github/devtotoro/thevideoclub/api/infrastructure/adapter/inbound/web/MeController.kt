package com.github.devtotoro.thevideoclub.api.infrastructure.adapter.inbound.web

import com.github.devtotoro.thevideoclub.api.domain.user.UserProvisioningService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class MeController(
    private val userProvisioningService: UserProvisioningService,
) {
    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal jwt: Jwt,
    ): MeResponse {
        val externalId =
            jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is missing or invalid")
        val email =
            jwt.getClaimAsString("email")
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email is missing or invalid")

        val user = userProvisioningService.provision(externalId, email)

        return MeResponse(id = user.getPublicId(), email = user.email)
    }
}

data class MeResponse(
    val id: String,
    val email: String,
)
