package com.loopers.domain.stock

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(private val stockRepository: StockRepository) {

    @Transactional
    fun deduct(command: StockCommand.Deduct) {
        val stock = stockRepository.findByProductId(command.productId)
            ?: throw CoreException(NOT_FOUND, "해당 상품의 재고를 찾을 수 없습니다.")

        stock.deduct(command.quantity)
        stockRepository.save(stock)
    }
}
