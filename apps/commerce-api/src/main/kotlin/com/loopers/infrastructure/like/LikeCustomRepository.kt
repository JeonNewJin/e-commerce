package com.loopers.infrastructure.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.QLike.like
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class LikeCustomRepository(private val query: JPAQueryFactory) {

    fun findLikes(command: LikeCommand.GetLikes): Page<Like> {
        val likes = query
            .selectFrom(like)
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
