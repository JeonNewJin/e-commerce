package com.loopers.domain.product

import com.loopers.domain.product.model.ProductInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getProductOnSale(productId: Long): ProductInfo {
        val product = productRepository.findById(productId)
            ?: throw CoreException(NOT_FOUND, "해당 상품을 찾을 수 없습니다.")

        product.checkIsOnSale()

        return ProductInfo.from(product)
    }

    fun findProductsOnSale(command: ProductCommand.FindProductsOnSale): Page<ProductInfo> =
        productRepository.findProductsOnSale(command)
            .map { ProductInfo.from(it) }

    fun findProductsByIds(productIds: List<Long>): List<ProductInfo> =
        productRepository.findAllByIds(productIds)
            .map { ProductInfo.from(it) }
}
