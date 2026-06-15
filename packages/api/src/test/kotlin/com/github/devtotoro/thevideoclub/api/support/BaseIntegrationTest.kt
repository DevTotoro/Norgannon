package com.github.devtotoro.thevideoclub.api.support

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun resetDatabase() {
        val tables =
            jdbcTemplate.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename <> 'flyway_schema_history'",
                String::class.java,
            )

        if (tables.isNotEmpty()) {
            val targets = tables.joinToString(", ") { "\"$it\"" }
            jdbcTemplate.execute("TRUNCATE TABLE $targets RESTART IDENTITY CASCADE")
        }
    }

    companion object {
        @ServiceConnection
        val postgres =
            PostgreSQLContainer("postgres:18-alpine").apply {
                withDatabaseName("thevideoclub_test")
                withUsername("test")
                withPassword("test")
                withReuse(true)
            }

        init {
            postgres.start()
        }
    }
}
