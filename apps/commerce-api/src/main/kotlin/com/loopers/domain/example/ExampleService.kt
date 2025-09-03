package com.loopers.domain.example

import jakarta.persistence.EntityManager
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExampleService(
    private val entityManager: EntityManager,
    private val publisher: ApplicationEventPublisher,
) {

    @Transactional
    fun foo() {
        println("hello")
        entityManager.persist(ExampleEntity("save foo"))
        publisher.publishEvent(ExampleEvent("world"))
        throw RuntimeException("Foo Exception")
    }
}
