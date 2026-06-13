package com.github.devtotoro.thevideoclub.api.domain.common

import io.hypersistence.tsid.TSID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TSIDCodecTest {
    @Test
    fun `encode then decode round-trips back to the original long`() {
        val original = TSID.fast().toLong()

        val encoded = TSIDCodec.encode(original)
        val decoded = TSIDCodec.decode(encoded)

        assertEquals(original, decoded, "Decoding an encoded id must yield the original value")
    }

    @Test
    fun `encode produces a 13-character Crockford Base32 string`() {
        val encoded = TSIDCodec.encode(TSID.fast().toLong())

        assertEquals(13, encoded.length, "A 64-bit TSID in Crockford Base32 is always 13 characters")

        val illegalCharacters = setOf('I', 'O', 'L', 'U', 'i', 'o', 'l', 'u')
        assertTrue(
            encoded.none { it in illegalCharacters },
            "Encoded id '$encoded' must not contain ambiguous Crockford characters",
        )
    }

    @Test
    fun `decode rejects a blank value with a descriptive error`() {
        val error = assertThrows<IllegalArgumentException> { TSIDCodec.decode("   ") }

        assertEquals("TSID value cannot be blank", error.message)
    }

    @Test
    fun `decode rejects an empty value`() {
        assertThrows<IllegalArgumentException> { TSIDCodec.decode("") }
    }
}
