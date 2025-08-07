package com.loopers.domain.brand

import com.loopers.domain.brand.entity.Brand
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class BrandServiceTest(
    private val brandService: BrandService,
    private val brandJpaRepository: BrandJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `브랜드 정보를 조회할 때, ` {

        @Test
        fun `존재하지 않는 브랜드 ID로 조회하면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val nonExistentBrandId = 999L

            // When
            val actual = assertThrows<CoreException> {
                brandService.getBrand(nonExistentBrandId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 브랜드를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 브랜드 ID로 조회하면, 해당 브랜드 정보를 반환한다`() {
            // Given
            val brand = Brand(
                name = "무신사",
                description = "브랜드 설명입니다.",
            )
            brandJpaRepository.save(brand)

            // When
            val actual = brandService.getBrand(brand.id)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.id).isNotNull() },
                { assertThat(actual.name).isEqualTo("무신사") },
                { assertThat(actual.description).isEqualTo("브랜드 설명입니다.") },
            )
        }
    }

    @Test
    fun `브랜드 ID 목록으로 브랜드들을 조회할 수 있다`() {
        // Given
        val brands = (1..5).map { i ->
            Brand(
                name = "브랜드 $i",
                description = "브랜드 설명 $i",
            )
        }
        brandJpaRepository.saveAll(brands)

        // When
        val actual = brandService.getBrands(brands.map { it.id })

        // Then
        assertThat(actual).hasSize(5)
            .extracting("name", "description")
            .containsExactlyInAnyOrder(
                tuple("브랜드 1", "브랜드 설명 1"),
                tuple("브랜드 2", "브랜드 설명 2"),
                tuple("브랜드 3", "브랜드 설명 3"),
                tuple("브랜드 4", "브랜드 설명 4"),
                tuple("브랜드 5", "브랜드 설명 5"),
            )
    }
}
