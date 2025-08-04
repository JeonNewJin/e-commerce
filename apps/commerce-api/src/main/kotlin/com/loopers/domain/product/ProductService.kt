package com.loopers.domain.product

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getProduct(productId: Long): ProductInfo =
        productRepository.findById(productId)
            ?.let { ProductInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "해당 상품을 찾을 수 없습니다.")

    fun findProducts(command: ProductCommand.GetProducts): Page<ProductInfo> =
        productRepository.findProducts(command)
            .map { ProductInfo.from(it) }

    fun getProducts(productIds: List<Long>): List<ProductInfo> =
        productRepository.findByIds(productIds)
            .map { ProductInfo.from(it) }
}
