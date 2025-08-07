package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.entity.QLike.like
import com.loopers.domain.like.entity.QLikeCount.likeCount
import com.loopers.domain.like.model.LikeWithCount
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class LikeCustomRepository(private val query: JPAQueryFactory) {

    fun findLikes(command: LikeCommand.FindLikes): Page<LikeWithCount> {
        val likes = query
            .select(
                Projections.constructor(
                    LikeWithCount::class.java,
                    like.id,
                    like.userId,
                    like.target.type,
                    like.target.id,
                    likeCount.count,
                ),
            )
            .from(like)
            .join(likeCount)
            .on(like.target.eq(likeCount.target))
            .where(
                like.userId.eq(command.userId),
                like.target.type.eq(command.targetType),
            )
            .orderBy(like.createdAt.desc())
            .offset(command.pageable.offset)
            .limit(command.pageable.pageSize.toLong())
            .fetch()

        val total = query
            .select(like.count())
            .from(like)
            .where(
                like.userId.eq(command.userId),
                like.target.type.eq(command.targetType),
            )
            .fetchOne() ?: 0L

        return PageImpl(likes, command.pageable, total)
    }
}
