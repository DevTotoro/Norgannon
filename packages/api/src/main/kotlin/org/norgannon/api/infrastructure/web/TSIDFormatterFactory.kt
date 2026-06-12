package com.github.devtotoro.thevideoclub.api.infrastructure.web

import io.hypersistence.tsid.TSID
import org.springframework.format.AnnotationFormatterFactory
import org.springframework.format.Parser
import org.springframework.format.Printer

class TSIDFormatterFactory : AnnotationFormatterFactory<TSIDParam> {
    override fun getFieldTypes(): Set<Class<*>> = setOf(Long::class.java)

    override fun getPrinter(
        annotation: TSIDParam,
        fieldType: Class<*>,
    ): Printer<Long> = Printer { objectValue, _ -> TSID.from(objectValue).toString() }

    override fun getParser(
        annotation: TSIDParam,
        fieldType: Class<*>,
    ): Parser<Long> =
        Parser { text, _ ->
            if (text.isBlank()) throw IllegalArgumentException("TSID parameter cannot be blank")
            TSID.from(text).toLong()
        }
}
