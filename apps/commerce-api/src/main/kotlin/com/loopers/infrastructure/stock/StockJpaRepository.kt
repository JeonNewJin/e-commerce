package com.loopers.infrastructure.stock

import com.loopers.domain.stock.entity.Stock
import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface StockJpaRepository : JpaRepository<Stock, Long> {

    fun findByProductId(productId: Long): Stock?

    @Lock(PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.productId = :productId")
    fun findByProductIdWithLock(productId: Long): Stock?
}
