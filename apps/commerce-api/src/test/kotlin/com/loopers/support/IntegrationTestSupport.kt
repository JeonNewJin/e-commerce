package com.loopers.support

import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.event.RecordApplicationEvents

@RecordApplicationEvents
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class IntegrationTestSupport {

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
