package com.loopers.infrastructure.payment.feign

import com.loopers.infrastructure.payment.PgDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "pgClient",
    url = "http://localhost:8082/api/v1/payments",
)
interface PgClient {

    @PostMapping
    fun requestPayment(
        @RequestHeader("X-USER-ID") userId: String,
        request: PgDto.PaymentRequest,
    ): PgDto.PaymentResponse

    @GetMapping
    fun getPayments(
        @RequestHeader("X-USER-ID") userId: String,
        orderId: String,
    ): PgDto.OrderResponse
}
