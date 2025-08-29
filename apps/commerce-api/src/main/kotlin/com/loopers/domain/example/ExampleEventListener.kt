package com.loopers.domain.example

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ExampleEventListener {

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
fun beforeCommit(event: ExampleEvent) {
        println("Before Commit Listener: ${event.message}")
//        throw RuntimeException("Before Commit Listener Exception")
    }

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
fun afterCommit(event: ExampleEvent) {
        println("After Commit Listener: ${event.message}")
//        throw RuntimeException("강제 예외")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun afterRollback(event: ExampleEvent) {
        println("After Rollback Listener: ${event.message}")
//        throw RuntimeException("강제 예외")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    fun afterCompletion(event: ExampleEvent) {
        println("After Completion Listener: ${event.message}")
//        throw RuntimeException("강제 예외")
    }
}
