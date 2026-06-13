package com.github.devtotoro.thevideoclub.api.infrastructure.adapter.inbound.web.support

import com.github.devtotoro.thevideoclub.api.domain.common.TSIDCodec
import io.hypersistence.tsid.TSID
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TSIDFormatterFactoryTest {
    private val factory = TSIDFormatterFactory()
    private val annotation: TSIDParam = Mockito.mock(TSIDParam::class.java)

    @Test
    fun `supports the Long field type`() {
        assertTrue(factory.fieldTypes.contains(Long::class.java))
    }

    @Test
    fun `printer renders a long as its Crockford Base32 public id`() {
        val id = TSID.fast().toLong()

        val printed = factory.getPrinter(annotation, Long::class.java).print(id, Locale.ROOT)

        assertEquals(TSIDCodec.encode(id), printed)
    }

    @Test
    fun `parser converts a public id back into the underlying long`() {
        val id = TSID.fast().toLong()
        val encoded = TSIDCodec.encode(id)

        val parsed = factory.getParser(annotation, Long::class.java).parse(encoded, Locale.ROOT)

        assertEquals(id, parsed)
    }

    @Test
    fun `printer and parser are symmetric`() {
        val id = TSID.fast().toLong()

        val printed = factory.getPrinter(annotation, Long::class.java).print(id, Locale.ROOT)
        val parsed = factory.getParser(annotation, Long::class.java).parse(printed, Locale.ROOT)

        assertEquals(id, parsed)
    }
}
