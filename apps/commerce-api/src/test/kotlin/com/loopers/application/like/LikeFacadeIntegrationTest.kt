package com.loopers.application.like

import com.loopers.domain.brand.Brand
import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeTarget
import com.loopers.domain.like.LikeTargetType.PRODUCT
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductStatus.SALE
import com.loopers.domain.user.Gender.MALE
import com.loopers.domain.user.User
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.like.LikeCountJpaRepository
import com.loopers.infrastructure.like.LikeJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
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

class LikeFacadeIntegrationTest(
    private val likeFacade: LikeFacade,
    private val likeJpaRepository: LikeJpaRepository,
    private val likeCountJpaRepository: LikeCountJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val brandJpaRepository: BrandJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `좋아요를 할 때, ` {

        @Test
        fun `존재하지 않는 사용자 ID로 좋아요하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val input = LikeInput.Like(
                loginId = "notfound",
                targetId = 1L,
                targetType = PRODUCT,
            )

            // When
            val actual = assertThrows<CoreException> {
                likeFacade.like(input)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }
    }

    @Test
    fun `좋아요를 취소하면, 좋아요 카운트가 감소한다`() {
        // Given
        val user = User(
            loginId = "wjsyuwls",
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val like = Like(
            userId = 1L,
            targetId = 1L,
            targetType = PRODUCT,
        )
        likeJpaRepository.save(like)

        val likeCount = LikeCount(
            targetId = 1L,
            targetType = PRODUCT,
            count = 1,
        )
        likeCountJpaRepository.save(likeCount)

        val input = LikeInput.Unlike(
            loginId = "wjsyuwls",
            targetId = 1L,
            targetType = PRODUCT,
        )

        // When
        likeFacade.unlike(input)

        // Then
        val actual = likeCountJpaRepository.findByTarget(LikeTarget(id = 1L, type = PRODUCT))

        assertAll(
            { assertThat(actual).isNotNull() },
            { assertThat(actual?.count).isEqualTo(0) },
        )
    }

    @Test
    fun `좋아요한 상품 목록을 조회할 때, 브랜드 정보 및 좋아요 개수를 포함한 상품 정보를 반환한다`() {
        // Given
        val user = User(
            loginId = "wjsyuwls",
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val brand = Brand(
            name = "무신사",
            description = "브랜드 설명입니다.",
        )
        brandJpaRepository.save(brand)

        val products = (1..20).map { i ->
            Product(
                name = "상품$i",
                price = BigDecimal(10000L * i),
                brandId = brand.id,
                publishedAt = "2025-07-31",
                status = SALE,
            )
        }
        productJpaRepository.saveAll(products)

        val likes = (1..20).map { i ->
            Like(
                userId = user.id,
                targetId = i.toLong(),
                targetType = PRODUCT,
            )
        }
        likeJpaRepository.saveAll(likes)

        val likeCounts = (1..20).map { i ->
            LikeCount(
                targetId = i.toLong(),
                targetType = PRODUCT,
                count = i.toLong(),
            )
        }
        likeCountJpaRepository.saveAll(likeCounts)

        val input = LikeInput.GetLikes(
            loginId = "wjsyuwls",
            targetType = PRODUCT,
            pageable = Pageable.ofSize(10),
        )

        // When
        val actual = likeFacade.getLikedProducts(input)

        // Then
        assertAll(
            {
                assertThat(actual.products).hasSize(10)
                    .extracting("name", "price", "publishedAt", "status", "likeCount", "brandId", "brandName", "brandDescription")
                    .containsExactlyInAnyOrder(
                        tuple("상품20", BigDecimal("200000.00"), "2025-07-31", SALE, 20L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품19", BigDecimal("190000.00"), "2025-07-31", SALE, 19L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품18", BigDecimal("180000.00"), "2025-07-31", SALE, 18L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품17", BigDecimal("170000.00"), "2025-07-31", SALE, 17L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품16", BigDecimal("160000.00"), "2025-07-31", SALE, 16L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품15", BigDecimal("150000.00"), "2025-07-31", SALE, 15L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품14", BigDecimal("140000.00"), "2025-07-31", SALE, 14L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품13", BigDecimal("130000.00"), "2025-07-31", SALE, 13L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품12", BigDecimal("120000.00"), "2025-07-31", SALE, 12L, brand.id, "무신사", "브랜드 설명입니다."),
                        tuple("상품11", BigDecimal("110000.00"), "2025-07-31", SALE, 11L, brand.id, "무신사", "브랜드 설명입니다."),
                    )
            },
            { assertThat(actual.totalElements).isEqualTo(20L) },
            { assertThat(actual.pageSize).isEqualTo(10) },
            { assertThat(actual.currentPage).isEqualTo(0) },
            { assertThat(actual.totalPages).isEqualTo(2) },
            { assertThat(actual.hasNext).isTrue() },
            { assertThat(actual.hasPrevious).isFalse() },
        )
    }
}
