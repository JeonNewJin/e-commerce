package com.loopers.domain.user

import com.loopers.domain.user.model.Gender.MALE
import com.loopers.domain.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `사용자가 정상 생성된다`() {
        // Given
        val loginId = "wjsyuwls"
        val email = "wjsyuwls@google.com"
        val birthdate = "2000-01-01"
        val gender = MALE

        // When
        val actual = User(
            loginId = loginId,
            email = email,
            birthdate = birthdate,
            gender = gender,
        )

        // Then
        assertThat(actual.loginId.value).isEqualTo(loginId)
        assertThat(actual.email.value).isEqualTo(email)
        assertThat(actual.birthdate.value).isEqualTo(birthdate)
        assertThat(actual.gender).isEqualTo(gender)
    }
}
