package com.loopers.infrastructure.product

import com.loopers.domain.product.entity.Product
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ProductCoreRepository(
    private val productJpaRepository: ProductJpaRepository,
    private val customRepository: ProductCustomRepository,
) : ProductRepository {

    override fun findById(productId: Long): Product? = productJpaRepository.findById(productId).orElse(null)

    override fun findProductsOnSale(command: ProductCommand.FindProductsOnSale): Page<Product> =
        customRepository.findProductsOnSale(command)

    override fun findAllByIds(productIds: List<Long>): List<Product> {
        if (productIds.isEmpty()) {
            return emptyList()
        }

        return productJpaRepository.findByIdIn(productIds)
    }
}
