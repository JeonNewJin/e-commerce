package com.loopers.domain.user.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.user.vo.Birthdate
import com.loopers.domain.user.vo.Email
import com.loopers.domain.user.model.Gender
import com.loopers.domain.user.vo.LoginId
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class User(loginId: String, email: String, birthdate: String, gender: Gender) : BaseEntity() {

    @Embedded
    val loginId: LoginId = LoginId(loginId)

    @Embedded
    var email: Email = Email(email)
        private set

    @Embedded
    var birthdate: Birthdate = Birthdate(birthdate)
        private set

    @Enumerated(EnumType.STRING)
    var gender: Gender = gender
        private set
}
