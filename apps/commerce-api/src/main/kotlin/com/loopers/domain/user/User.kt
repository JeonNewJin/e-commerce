package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
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

    @Enumerated(STRING)
    var gender: Gender = gender
        private set
}
