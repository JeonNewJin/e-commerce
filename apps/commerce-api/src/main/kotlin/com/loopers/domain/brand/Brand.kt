package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "brand")
class Brand private constructor(name: String, description: String) : BaseEntity() {

    val name: String = name

    val description: String = description

    companion object {
        operator fun invoke(name: String, description: String): Brand {
            require(name.isNotBlank()) {
                throw IllegalArgumentException("브랜드 이름은 필수입니다.")
            }
            require(description.isNotBlank()) {
                throw IllegalArgumentException("브랜드 설명은 필수입니다.")
            }

            return Brand(name = name, description = description)
        }
    }
}
