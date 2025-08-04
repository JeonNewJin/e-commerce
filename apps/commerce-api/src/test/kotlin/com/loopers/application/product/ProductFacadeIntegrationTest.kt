package com.loopers.application.product

import com.loopers.domain.brand.Brand
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeableType.PRODUCT
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductSortType.LIKES_DESC
import com.loopers.domain.product.ProductStatus.SALE
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.like.LikeCountJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class ProductFacadeIntegrationTest(
    private val productFacade: ProductFacade,
    private val brandJpaRepository: BrandJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val likeCountJpaRepository: LikeCountJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `상품 정보를 조회할 때, ` {

        @Test
        fun `존재하지 않는 상품 ID로 조회하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val nonExistentProductId = 999L

            // When
            val actual = assertThrows<CoreException> {
                productFacade.getProduct(nonExistentProductId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 상품을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 상품 ID로 조회하면, 상품과 브랜드 정보 및 좋아요 개수를 포함한 상품 정보를 반환한다`() {
            // Given
            val brand = Brand(
                name = "무신사",
                description = "브랜드 설명입니다.",
            )
            brandJpaRepository.save(brand)

            val product = Product(
                name = "상품",
                price = BigDecimal(10_000L),
                publishedAt = "2025-07-31",
                status = SALE,
                brandId = 1L,
            )
            productJpaRepository.save(product)

            val likeCount = LikeCount(
                targetId = product.id,
                targetType = PRODUCT,
                count = 100L,
            )
            likeCountJpaRepository.save(likeCount)

            // When
            val actual = productFacade.getProduct(product.id)

            // Then
            assertAll(
                { assertThat(actual.id).isNotNull() },
                { assertThat(actual.name).isEqualTo("상품") },
                { assertThat(actual.price).isEqualTo(BigDecimal("10000.00")) },
                { assertThat(actual.publishedAt).isEqualTo("2025-07-31") },
                { assertThat(actual.status).isEqualTo(SALE) },
                { assertThat(actual.likeCount).isEqualTo(100L) },
                { assertThat(actual.brandId).isNotNull() },
                { assertThat(actual.brandName).isEqualTo("무신사") },
                { assertThat(actual.brandDescription).isEqualTo("브랜드 설명입니다.") },
            )
        }
    }

    @Test
    fun `조건과 페이징 정보로 상품 목록을 조회하면, 상품과 브랜드 정보 및 좋아요 개수를 포함한 상품 목록을 반환한다`() {
        // Given
        val brand = Brand(
            name = "무신사",
            description = "브랜드 설명입니다.",
        )
        brandJpaRepository.save(brand)

        val products = (1..20).map { index ->
            Product(
                name = "상품$index",
                price = BigDecimal(10_000L * index),
                publishedAt = LocalDate.of(2025, 7, index).toString(),
                status = SALE,
                brandId = brand.id,
            )
        }
        productJpaRepository.saveAll(products)

        val likeCounts = products.map { product ->
            LikeCount(
                targetId = product.id,
                targetType = PRODUCT,
                count = product.id * 10L,
            )
        }
        likeCountJpaRepository.saveAll(likeCounts)

        val input = ProductInput.GetProducts(
            sortType = LIKES_DESC,
            pageable = Pageable.ofSize(10),
        )

        // When
        val actual = productFacade.getProducts(input)

        // Then
        assertAll(
            {
                assertThat(actual.products).hasSize(10)
                    .extracting("name", "price", "publishedAt", "status", "likeCount", "brandId", "brandName", "brandDescription")
                    .containsExactlyInAnyOrder(
                        tuple("상품20", BigDecimal("200000.00"), "2025-07-20", SALE, 200L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품19", BigDecimal("190000.00"), "2025-07-19", SALE, 190L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품18", BigDecimal("180000.00"), "2025-07-18", SALE, 180L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품17", BigDecimal("170000.00"), "2025-07-17", SALE, 170L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품16", BigDecimal("160000.00"), "2025-07-16", SALE, 160L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품15", BigDecimal("150000.00"), "2025-07-15", SALE, 150L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품14", BigDecimal("140000.00"), "2025-07-14", SALE, 140L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품13", BigDecimal("130000.00"), "2025-07-13", SALE, 130L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품12", BigDecimal("120000.00"), "2025-07-12", SALE, 120L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품11", BigDecimal("110000.00"), "2025-07-11", SALE, 110L, brand.id, "무신사", "브랜드 설명입니다."),
                    )
            },
            { assertThat(actual.totalElements).isEqualTo(20L) },
            { assertThat(actual.totalPages).isEqualTo(2) },
            { assertThat(actual.currentPage).isEqualTo(0) },
            { assertThat(actual.pageSize).isEqualTo(10) },
            { assertThat(actual.hasNext).isTrue() },
            { assertThat(actual.hasPrevious).isFalse() },
        )
    }
}
