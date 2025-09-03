package com.loopers.domain.product

import com.loopers.domain.product.entity.Product
import org.springframework.data.domain.Page

interface ProductRepository {

    fun save(product: Product)

    fun findById(productId: Long): Product?

    fun findProductsOnSale(command: ProductCommand.FindProductsOnSale): Page<Product>

    fun findAllByIds(productIds: List<Long>): List<Product>

    fun findByIdWithLock(productId: Long): Product?
}
