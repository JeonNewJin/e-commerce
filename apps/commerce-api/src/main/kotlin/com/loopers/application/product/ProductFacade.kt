package com.loopers.application.product

import com.fasterxml.jackson.core.type.TypeReference
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductService
import com.loopers.domain.support.cache.CacheKeyGenerator
import com.loopers.domain.support.cache.CacheKeys
import com.loopers.domain.support.cache.CacheTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import kotlin.random.Random

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
    private val cacheTemplate: CacheTemplate,
) {

    @Transactional(readOnly = true)
    fun getProductOnSale(productId: Long): ProductOutput {
        val cacheKey = CacheKeyGenerator.generate(
            namespace = CacheKeys.PRODUCT_DETAIL,
            params = mapOf("productId" to productId),
        )

        val typeRef = object : TypeReference<ProductOutput>() {}
        val expiryGap = 10L

        cacheTemplate.get(cacheKey, typeRef)?.let { cached ->
            val ttl = cacheTemplate.getTimeToLive(cacheKey)
            val randomGap = (Random.nextDouble() * expiryGap).toLong()
            ttl?.minus(randomGap)?.let {
                if (it > 0) {
                    return cached
                }
            }
        }

        val product = productService.getProductOnSale(productId)
        val brand = brandService.getBrand(product.brandId)
        val result = ProductOutput.of(product, brand)
        cacheTemplate.set(cacheKey, result, Duration.ofMinutes(3))
        return result
    }

    @Transactional(readOnly = true)
    fun getProductsOnSale(input: ProductInput.FindProductsOnSale): ProductsOutput {
        val cacheKey = CacheKeyGenerator.forProductsOnSale(
            sortType = input.sortType?.name,
            page = input.pageable.pageNumber,
        )

        val typeRef = object : TypeReference<ProductsOutput>() {}
        val expiryGap = 10L

        cacheTemplate.get(cacheKey, typeRef)?.let { cached ->
            val ttl = cacheTemplate.getTimeToLive(cacheKey)
            val randomGap = (Random.nextDouble() * expiryGap).toLong()
            ttl?.minus(randomGap)?.let {
                if (it > 0) {
                    return cached
                }
            }
        }

        val products = productService.findProductsOnSale(input.toCommand())
        val brandIds = products.content.map { it.brandId }.distinct()
        val brands = brandService.getBrands(brandIds)
        val result = ProductsOutput.of(products, brands)
        cacheTemplate.set(cacheKey, result, Duration.ofMinutes(3))
        return result
    }
}
