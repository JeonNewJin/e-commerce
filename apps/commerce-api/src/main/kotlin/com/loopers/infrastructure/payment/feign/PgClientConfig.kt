package com.loopers.infrastructure.payment.feign

import feign.Logger
import feign.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class PgClientConfig {

    @Bean
    fun feignOptions(): Request.Options? = Request.Options(
        1,
        TimeUnit.SECONDS,
        3,
        TimeUnit.SECONDS,
        true,
        )

    // Recommended
    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.BASIC
}
