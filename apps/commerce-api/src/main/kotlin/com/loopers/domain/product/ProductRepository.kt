package com.loopers.domain.product

import org.springframework.data.domain.Page

interface ProductRepository {

    fun findById(productId: Long): Product?

    fun findProducts(command: ProductCommand.GetProducts): Page<Product>

    fun findByIds(productIds: List<Long>): List<Product>
}
