package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "brand")
class Brand(name: String, description: String) : BaseEntity() {

    val name: String = name

    var description: String = description
        private set

    init {
        require(name.isNotBlank()) {
            throw IllegalArgumentException("브랜드 이름은 필수입니다.")
        }
        require(description.isNotBlank()) {
            throw IllegalArgumentException("브랜드 설명은 필수입니다.")
        }
    }
}
