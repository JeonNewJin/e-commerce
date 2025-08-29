package com.loopers.infrastructure.product

import com.loopers.domain.product.entity.Product
import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ProductJpaRepository : JpaRepository<Product, Long> {

    fun findByIdIn(productIds: List<Long>): List<Product>

    @Lock(PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :productId")
    fun findByIdWithLock(productId: Long): Product?
}
