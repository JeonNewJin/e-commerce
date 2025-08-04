package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class User private constructor(loginId: LoginId, email: Email, birthdate: Birthdate, gender: Gender) : BaseEntity() {

    @Embedded
    var loginId: LoginId = loginId
        private set

    @Embedded
    var email: Email = email
        private set

    @Embedded
    var birthdate: Birthdate = birthdate
        private set

    @Enumerated(STRING)
    var gender: Gender = gender
        private set

    companion object {
        operator fun invoke(
            loginId: String,
            email: String,
            birthdate: String,
            gender: Gender,
        ): User = User(
            loginId = LoginId(loginId),
            email = Email(email),
            birthdate = Birthdate(birthdate),
            gender = gender,
        )
    }
}
