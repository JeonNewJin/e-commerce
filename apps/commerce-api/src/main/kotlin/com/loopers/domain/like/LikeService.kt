package com.loopers.domain.like

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class LikeService(private val likeRepository: LikeRepository) {

    fun getLikeCount(
        targetType: LikeTargetType,
        targetId: Long,
    ): LikeCountInfo =
        likeRepository.findLikeCountByTarget(LikeTarget(targetId, targetType))
            ?.let { LikeCountInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "좋아요 카운트를 찾을 수 없습니다.")

    fun findLikeCounts(
        targetType: LikeTargetType,
        targetIds: List<Long>,
    ): List<LikeCountInfo> =
        likeRepository.findLikeCounts(targetType, targetIds)
            .map { LikeCountInfo.from(it) }
}
