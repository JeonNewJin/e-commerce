package com.loopers.domain.product

import com.loopers.domain.product.ProductSortType.PRICE_ASC
import com.loopers.domain.product.ProductStatus.SALE
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

class ProductServiceTest(
    private val productService: ProductService,
    private val productJpaRepository: ProductJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `상품 정보를 조회할 때, ` {

        @Test
        fun `존재하지 않는 상품 ID로 조회하면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val nonExistentProductId = 999L

            // When
            val actual = assertThrows<CoreException> {
                productService.getProduct(nonExistentProductId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 상품을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 상품 ID로 조회하면, 해당 상품 정보를 반환한다`() {
            // Given
            val product = Product("테스트 상품", 10000.toBigDecimal(), 1L, "2025-07-30", SALE)
            productJpaRepository.save(product)

            // When
            val actual = productService.getProduct(product.id)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.id).isNotNull() },
                { assertThat(actual.name).isEqualTo("테스트 상품") },
                { assertThat(actual.price).isEqualTo(BigDecimal("10000.00")) },
                { assertThat(actual.brandId).isEqualTo(1L) },
                { assertThat(actual.publishedAt).isEqualTo("2025-07-30") },
                { assertThat(actual.status).isEqualTo(SALE) },
            )
        }
    }

    @Nested
    inner class `상품 목록을 조회할 때, ` {

        @Test
        fun `정렬 조건 미선택 시 상품게시일 내림차순으로, 페이지네이션을 통해 상품 목록을 분할하여 조회할 수 있다`() {
            // Given
            val products = (1..20).map { i ->
                Product(
                    "상품$i",
                    (10000 * i).toBigDecimal(),
                    1L,
                    LocalDate.of(2025, 7, i).toString(),
                    SALE,
                )
            }
            productJpaRepository.saveAll(products)

            val command = ProductCommand.GetProducts(pageable = Pageable.ofSize(10))

            // When
            val actual = productService.findProducts(command)

            // Then
            assertAll(
                {
                    assertThat(actual.content).hasSize(10)
                        .extracting("brandId", "name", "price", "publishedAt", "status")
                        .containsExactlyInAnyOrder(
                            tuple(1L, "상품20", BigDecimal("200000.00"), "2025-07-20", SALE),
                            tuple(1L, "상품19", BigDecimal("190000.00"), "2025-07-19", SALE),
                            tuple(1L, "상품18", BigDecimal("180000.00"), "2025-07-18", SALE),
                            tuple(1L, "상품17", BigDecimal("170000.00"), "2025-07-17", SALE),
                            tuple(1L, "상품16", BigDecimal("160000.00"), "2025-07-16", SALE),
                            tuple(1L, "상품15", BigDecimal("150000.00"), "2025-07-15", SALE),
                            tuple(1L, "상품14", BigDecimal("140000.00"), "2025-07-14", SALE),
                            tuple(1L, "상품13", BigDecimal("130000.00"), "2025-07-13", SALE),
                            tuple(1L, "상품12", BigDecimal("120000.00"), "2025-07-12", SALE),
                            tuple(1L, "상품11", BigDecimal("110000.00"), "2025-07-11", SALE),
                        )
                },
            )
        }

        @Test
        fun `상품 가격 오름차순 정렬 조건으로 상품 목록을 조회하면, 가격이 낮은 순서대로 반환된다`() {
            // Given
            val products = (1..20).map { i ->
                Product(
                    "상품$i",
                    (10000 * i).toBigDecimal(),
                    1L,
                    LocalDate.of(2025, 7, i).toString(),
                    SALE,
                )
            }
            productJpaRepository.saveAll(products)

            val command = ProductCommand.GetProducts(
                sortType = PRICE_ASC,
                pageable = Pageable.ofSize(10),
            )

            // When
            val actual = productService.findProducts(command)

            // Then
            assertAll(
                {
                    assertThat(actual.content).hasSize(10)
                        .extracting("brandId", "name", "price", "publishedAt", "status")
                        .containsExactlyInAnyOrder(
                            tuple(1L, "상품1", BigDecimal("10000.00"), "2025-07-01", SALE),
                            tuple(1L, "상품2", BigDecimal("20000.00"), "2025-07-02", SALE),
                            tuple(1L, "상품3", BigDecimal("30000.00"), "2025-07-03", SALE),
                            tuple(1L, "상품4", BigDecimal("40000.00"), "2025-07-04", SALE),
                            tuple(1L, "상품5", BigDecimal("50000.00"), "2025-07-05", SALE),
                            tuple(1L, "상품6", BigDecimal("60000.00"), "2025-07-06", SALE),
                            tuple(1L, "상품7", BigDecimal("70000.00"), "2025-07-07", SALE),
                            tuple(1L, "상품8", BigDecimal("80000.00"), "2025-07-08", SALE),
                            tuple(1L, "상품9", BigDecimal("90000.00"), "2025-07-09", SALE),
                            tuple(1L, "상품10", BigDecimal("100000.00"), "2025-07-10", SALE),
                        )
                },
            )
        }
    }
}
