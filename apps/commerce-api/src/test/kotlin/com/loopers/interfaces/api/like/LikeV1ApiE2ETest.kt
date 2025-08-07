package com.loopers.interfaces.api.like

import com.loopers.domain.brand.entity.Brand
import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.product.entity.Product
import com.loopers.domain.product.model.ProductStatus.SALE
import com.loopers.domain.user.entity.User
import com.loopers.domain.user.model.Gender.MALE
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.like.LikeCountJpaRepository
import com.loopers.infrastructure.like.LikeJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import java.math.BigDecimal
import java.time.LocalDate

class LikeV1ApiE2ETest : E2ETestSupport() {

    @Test
    fun `좋아요가 정상적으로 등록된다`(
        @Autowired client: TestRestTemplate,
        @Autowired userJpaRepository: UserJpaRepository,
    ) {
        // Given
        val loginId = "wjsyuwls"
        val user = User(
            loginId = loginId,
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val request = LikeV1Dto.Request.Like(targetType = PRODUCT)

        val headers = HttpHeaders().apply { set("X-USER-ID", loginId) }
        val httpEntity = HttpEntity(request, headers)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<Unit>>() {}
        val actual = client.exchange(
            "/api/v1/like/products/1",
            POST,
            httpEntity,
            responseType,
        )

        // Then
        assertAll(
            { assertThat(actual.statusCode.value()).isEqualTo(201) },
            { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
        )
    }

    @Test
    fun `좋아요가 정상적으로 취소된다`(
        @Autowired client: TestRestTemplate,
        @Autowired userJpaRepository: UserJpaRepository,
        @Autowired brandJpaRepository: BrandJpaRepository,
        @Autowired productJpaRepository: ProductJpaRepository,
        @Autowired likeJpaRepository: LikeJpaRepository,
        @Autowired likeCountJpaRepository: LikeCountJpaRepository,
    ) {
        // Given
        val loginId = "wjsyuwls"
        val user = User(
            loginId = loginId,
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val brand = Brand(
            name = "Brand",
            description = "Brand Description",
        )
        brandJpaRepository.save(brand)

        val product = Product(
            brandId = brand.id,
            name = "Test Product",
            price = BigDecimal(10_000L),
            publishedAt = "2023-01-01",
            status = SALE,
        )
        productJpaRepository.save(product)

        val like = Like(
            userId = user.id,
            targetId = product.id,
            targetType = PRODUCT,
        )
        likeJpaRepository.save(like)

        val likeCount = LikeCount(
            targetId = product.id,
            targetType = PRODUCT,
            count = 5L,
        )
        likeCountJpaRepository.save(likeCount)

        val request = LikeV1Dto.Request.Unlike(targetType = PRODUCT)

        val headers = HttpHeaders().apply { set("X-USER-ID", loginId) }
        val httpEntity = HttpEntity(request, headers)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<Unit>>() {}
        val actual = client.exchange(
            "/api/v1/like/products/${product.id}",
            DELETE,
            httpEntity,
            responseType,
        )

        // Then
        assertThat(actual.statusCode.value()).isEqualTo(204)
    }

    @Test
    fun `내가 좋아요 한 상품 목록을 조회할 수 있다`(
        @Autowired client: TestRestTemplate,
        @Autowired userJpaRepository: UserJpaRepository,
        @Autowired brandJpaRepository: BrandJpaRepository,
        @Autowired productJpaRepository: ProductJpaRepository,
        @Autowired likeJpaRepository: LikeJpaRepository,
        @Autowired likeCountJpaRepository: LikeCountJpaRepository,
    ) {
        // Given
        val loginId = "wjsyuwls"
        val user = User(
            loginId = loginId,
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val brand = Brand(
            name = "Brand",
            description = "Brand Description",
        )
        brandJpaRepository.save(brand)

        val products = (1..20).map { i ->
            Product(
                brandId = brand.id,
                name = "Test Product $i",
                price = BigDecimal(10_000L * i),
                publishedAt = LocalDate.of(2025, 8, i).toString(),
                status = SALE,
            )
        }
        productJpaRepository.saveAll(products)

        val likes = (1..20).map { i ->
            Like(
                userId = user.id,
                targetId = products[i - 1].id,
                targetType = PRODUCT,
            )
        }
        likeJpaRepository.saveAll(likes)

        val likeCounts = (1..20).map { i ->
            LikeCount(
                targetId = products[i - 1].id,
                targetType = PRODUCT,
                count = i.toLong(),
            )
        }
        likeCountJpaRepository.saveAll(likeCounts)

        val request = PRODUCT
        val headers = HttpHeaders().apply { set("X-USER-ID", loginId) }
        val httpEntity = HttpEntity(request, headers)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<LikeV1Dto.Response.LikedProductsResponse>>() {}
        val actual = client.exchange(
            "/api/v1/like/products",
            GET,
            httpEntity,
            responseType,
        )

        // Then
        assertAll(
            { assertThat(actual.statusCode.value()).isEqualTo(200) },
            { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
            { assertThat(actual.body?.data?.products).hasSize(20) },
        )
    }
}
