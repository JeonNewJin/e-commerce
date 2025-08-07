package com.loopers.application.product

import com.loopers.domain.brand.BrandService
import com.loopers.domain.like.LikeService
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.product.ProductService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
    private val likeService: LikeService,
) {

    @Transactional(readOnly = true)
    fun getProductOnSale(productId: Long): ProductOutput {
        val product = productService.getProductOnSale(productId)
        val brand = brandService.getBrand(product.brandId)
        val likeCount = likeService.getLikeCount(PRODUCT, product.id)
        return ProductOutput.of(product, brand, likeCount)
    }

    @Transactional(readOnly = true)
    fun getProductsOnSale(input: ProductInput.FindProductsOnSale): ProductsOutput {
        val products = productService.findProductsOnSale(input.toCommand())
        val brandIds = products.content.map { it.brandId }.distinct()
        val brands = brandService.getBrands(brandIds)
        val likeCounts = likeService.findLikeCounts(PRODUCT, products.content.map { it.id })
        return ProductsOutput.of(products, brands, likeCounts)
    }
}
