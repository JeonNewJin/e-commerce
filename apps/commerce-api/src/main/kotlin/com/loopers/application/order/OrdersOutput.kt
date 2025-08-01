package com.loopers.application.order

import com.loopers.domain.order.OrderInfo
import org.springframework.data.domain.Page

data class OrdersOutput(
    val orders: List<OrderOutput>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun from(orders: Page<OrderInfo>): OrdersOutput =
            OrdersOutput(
                orders = orders.content.map { OrderOutput.from(it) },
                totalElements = orders.totalElements,
                totalPages = orders.totalPages,
                currentPage = orders.number,
                pageSize = orders.size,
                hasNext = orders.hasNext(),
                hasPrevious = orders.hasPrevious(),
            )
    }
}
