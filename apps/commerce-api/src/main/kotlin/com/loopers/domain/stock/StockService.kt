package com.loopers.domain.stock

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class StockService(private val stockRepository: StockRepository) {

    @Transactional
    fun deduct(command: StockCommand.Deduct) {
        val stock = getStock(command.productId)
        stock.deduct(command.quantity)
        stockRepository.save(stock)
    }

    fun checkAvailability(id: Long, quantity: Int) {
        val stock = getStock(id)
        if (!stock.canDeduct(quantity)) {
            throw CoreException(CONFLICT, "재고가 부족합니다. 현재 재고: ${stock.quantity}, 요청 수량: $quantity")
        }
    }

    private fun getStock(productId: Long): Stock =
        stockRepository.findByProductId(productId)
            ?: throw CoreException(NOT_FOUND, "해당 상품의 재고를 찾을 수 없습니다.")
}
