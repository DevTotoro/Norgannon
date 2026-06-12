package org.norgannon.api.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {
    companion object {
        @ServiceConnection
        val postgres =
            PostgreSQLContainer("postgres:18").apply {
                withDatabaseName("norgannon_test")
                withUsername("test")
                withPassword("test")
                withReuse(true)
            }

        init {
            postgres.start()
        }
    }
}
