package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.entity.Brand
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET

class BrandV1ApiE2ETest : E2ETestSupport() {

    @Test
    fun `브랜드 정보를 정상적으로 조회한다`(
        @Autowired client: TestRestTemplate,
        @Autowired brandJpaRepository: BrandJpaRepository,
    ) {
        // Given
        val brand = Brand(
            name = "Brand",
            description = "Brand Description",
        )
        brandJpaRepository.save(brand)

        // When
        val responseType = object : ParameterizedTypeReference<ApiResponse<BrandV1Dto.Response.BrandResponse>>() {}
        val actual = client.exchange("/api/v1/brands/${brand.id}", GET, null, responseType)

        // Then
        assertAll(
            { assertThat(actual.statusCode.value()).isEqualTo(200) },
            { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
            { assertThat(actual.body?.data).isNotNull() },
            { assertThat(actual.body?.data?.id).isEqualTo(brand.id) },
            { assertThat(actual.body?.data?.name).isEqualTo("Brand") },
            { assertThat(actual.body?.data?.description).isEqualTo("Brand Description") },
        )
    }
}
