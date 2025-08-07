package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.entity.QOrder.order
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class OrderCustomRepository(private val query: JPAQueryFactory) {

    fun findOrders(command: OrderCommand.GetOrders): Page<Order> {
        val orders = query
            .selectFrom(order)
            .where(order.userId.eq(command.userId))
            .orderBy(order.createdAt.desc())
            .offset(command.pageable.offset)
            .limit(command.pageable.pageSize.toLong())
            .fetch()

        val total = query
            .select(order.count())
            .from(order)
            .where(order.userId.eq(command.userId))
            .fetchOne() ?: 0L

        return PageImpl(orders, command.pageable, total)
    }
}
