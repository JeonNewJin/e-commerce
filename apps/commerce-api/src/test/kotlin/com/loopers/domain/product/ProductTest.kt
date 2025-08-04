package com.loopers.domain.product

import com.loopers.domain.product.ProductStatus.SALE
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class ProductTest {

    @Nested
    inner class `상품 정보를 생성할 때, ` {

        @Test
        fun `상품 이름이 공백이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val invalidName = " "
            val price = BigDecimal(1_000L)
            val brandId = 1L
            val publishedAt = "2023-10-01"
            val status = SALE

            // When
            val actual = assertThrows<CoreException> {
                Product(
                    name = invalidName,
                    price = price,
                    brandId = brandId,
                    publishedAt = publishedAt,
                    status = status,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("상품 이름은 필수입니다.") },
            )
        }

        @Test
        fun `상품 가격이 음수이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val name = "상품"
            val negativePrice = BigDecimal(-1L)
            val brandId = 1L
            val publishedAt = "2023-10-01"
            val status = SALE

            // When
            val actual = assertThrows<CoreException> {
                Product(
                    name = name,
                    price = negativePrice,
                    brandId = brandId,
                    publishedAt = publishedAt,
                    status = status,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("상품 가격은 0 이상이어야 합니다.") },
            )
        }

        @Test
        fun `상품 출간일이 공백이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val name = "상품"
            val price = BigDecimal(1_000L)
            val brandId = 1L
            val invalidPublishedAt = " "
            val status = SALE

            // When
            val actual = assertThrows<CoreException> {
                Product(
                    name = name,
                    price = price,
                    brandId = brandId,
                    publishedAt = invalidPublishedAt,
                    status = status,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("상품 출간일시는 필수입니다.") },
            )
        }

        @Test
        fun `브랜드 ID가 유효하지 않으면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val name = "상품"
            val price = BigDecimal(1_000L)
            val invalidBrandId = 0L
            val publishedAt = "2023-10-01"
            val status = SALE

            // When
            val actual = assertThrows<CoreException> {
                Product(
                    name = name,
                    price = price,
                    brandId = invalidBrandId,
                    publishedAt = publishedAt,
                    status = status,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("유효하지 않은 브랜드 ID 입니다.") },
            )
        }

        @Test
        fun `유효한 정보로 정상 생성된다`() {
            // Given
            val name = "상품"
            val price = BigDecimal(1_000L)
            val brandId = 1L
            val publishedAt = "2023-10-01"
            val status = SALE

            // When
            val actual = Product(
                name = name,
                price = price,
                brandId = brandId,
                publishedAt = publishedAt,
                status = status,
            )

            // Then
            assertAll(
                { assertThat(actual.name).isEqualTo("상품") },
                { assertThat(actual.price).isEqualTo(BigDecimal(1_000L)) },
                { assertThat(actual.brandId).isEqualTo(1L) },
                { assertThat(actual.publishedAt).isEqualTo("2023-10-01") },
                { assertThat(actual.status).isEqualTo(SALE) },
            )
        }
    }
}
