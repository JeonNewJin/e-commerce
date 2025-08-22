package com.loopers.support

import com.loopers.CommerceApiApplication
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest(
    classes = [CommerceApiApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class E2ETestSupport {

    @Autowired
    protected lateinit var databaseCleanUp: DatabaseCleanUp

    @Autowired
    protected lateinit var redisCleanUp: RedisCleanUp

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
    }
}
