package com.github.devtotoro.thevideoclub.api.config

import com.github.devtotoro.thevideoclub.api.infrastructure.web.TSIDFormatterFactory
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addFormatterForFieldAnnotation(TSIDFormatterFactory())
    }
}
