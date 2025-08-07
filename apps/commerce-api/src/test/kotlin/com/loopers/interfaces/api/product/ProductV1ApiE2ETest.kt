package com.loopers.interfaces.api.product

import com.loopers.domain.brand.entity.Brand
import com.loopers.domain.product.entity.Product
import com.loopers.domain.product.model.ProductStatus.SALE
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.GET
import java.math.BigDecimal
import java.time.LocalDate

class ProductV1ApiE2ETest : E2ETestSupport() {

    @Test
    fun `상품 정보를 정상적으로 조회한다`(
        @Autowired client: TestRestTemplate,
        @Autowired brandJpaRepository: BrandJpaRepository,
        @Autowired productJpaRepository: ProductJpaRepository,
    ) {
        // Given
        val brand = Brand(
            name = "Brand",
            description = "Brand Description",
        )
        brandJpaRepository.save(brand)

        val product = Product(
            brandId = brand.id,
            name = "Test Product",
            price = BigDecimal(1_000L),
            publishedAt = "2025-08-01",
            status = SALE,
        )
        productJpaRepository.save(product)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.Response.ProductResponse>>() {}
        val actual = client.exchange("/api/v1/products/${product.id}", GET, HttpEntity(Unit), responseType)

        // Then
        assertAll(
            { assertThat(actual.statusCode.value()).isEqualTo(200) },
            { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
            { assertThat(actual.body?.data).isNotNull() },
            { assertThat(actual.body?.data?.id).isEqualTo(1L) },
            { assertThat(actual.body?.data?.name).isEqualTo("Test Product") },
            { assertThat(actual.body?.data?.price).isEqualTo(BigDecimal("1000.00")) },
            { assertThat(actual.body?.data?.publishedAt).isEqualTo("2025-08-01") },
            { assertThat(actual.body?.data?.status).isEqualTo(SALE) },
            { assertThat(actual.body?.data?.likeCount).isEqualTo(0) },
            { assertThat(actual.body?.data?.brandId).isNotNull() },
            { assertThat(actual.body?.data?.brandName).isEqualTo("Brand") },
            { assertThat(actual.body?.data?.brandDescription).isEqualTo("Brand Description") },
        )
    }

    @Test
    fun `상품 목록을 정장적으로 조회한다`(
        @Autowired client: TestRestTemplate,
        @Autowired brandJpaRepository: BrandJpaRepository,
        @Autowired productJpaRepository: ProductJpaRepository,
    ) {
        // Given
        val brand = Brand(
            name = "Brand",
            description = "Brand Description",
        )
        brandJpaRepository.save(brand)

        val products = (1..20).map { i ->
            Product(
                brandId = brand.id,
                name = "Test Product $i",
                price = BigDecimal(1_000L * i),
                publishedAt = LocalDate.of(2025, 8, i).toString(),
                status = SALE,
            )
        }
        productJpaRepository.saveAll(products)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.Response.ProductsResponse>>() {}
        val actual = client.exchange("/api/v1/products", GET, HttpEntity(Unit), responseType)

        // Then
        assertAll(
            { assertThat(actual.statusCode.value()).isEqualTo(200) },
            { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
            { assertThat(actual.body?.data).isNotNull() },
            {
                assertThat(actual.body?.data?.products).hasSize(20)
                    .extracting("name", "price", "publishedAt", "status", "likeCount", "brandId", "brandName", "brandDescription")
                    .containsExactlyInAnyOrder(
                        tuple("Test Product 20", BigDecimal("20000.00"), "2025-08-20", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 19", BigDecimal("19000.00"), "2025-08-19", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 18", BigDecimal("18000.00"), "2025-08-18", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 17", BigDecimal("17000.00"), "2025-08-17", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 16", BigDecimal("16000.00"), "2025-08-16", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 15", BigDecimal("15000.00"), "2025-08-15", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 14", BigDecimal("14000.00"), "2025-08-14", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 13", BigDecimal("13000.00"), "2025-08-13", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 12", BigDecimal("12000.00"), "2025-08-12", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 11", BigDecimal("11000.00"), "2025-08-11", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 10", BigDecimal("10000.00"), "2025-08-10", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 9", BigDecimal("9000.00"), "2025-08-09", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 8", BigDecimal("8000.00"), "2025-08-08", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 7", BigDecimal("7000.00"), "2025-08-07", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 6", BigDecimal("6000.00"), "2025-08-06", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 5", BigDecimal("5000.00"), "2025-08-05", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 4", BigDecimal("4000.00"), "2025-08-04", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 3", BigDecimal("3000.00"), "2025-08-03", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 2", BigDecimal("2000.00"), "2025-08-02", SALE, 0L, brand.id, "Brand", "Brand Description"),
                        tuple("Test Product 1", BigDecimal("1000.00"), "2025-08-01", SALE, 0L, brand.id, "Brand", "Brand Description"),
                    )
            },
        )
    }
}
