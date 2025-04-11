package com.team8.project2.domain.curation.curation.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.team8.project2.domain.comment.entity.QComment
import com.team8.project2.domain.curation.curation.dto.CurationProjectionDto
import com.team8.project2.domain.curation.curation.dto.QCurationProjectionDto
import com.team8.project2.domain.curation.curation.entity.QCuration
import com.team8.project2.domain.curation.curation.entity.QCurationTag
import com.team8.project2.domain.curation.tag.entity.QTag
import com.team8.project2.domain.member.entity.QMember
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class CurationRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CurationRepositoryCustom {

    override fun searchByProjection(
        tags: List<String>?,
        tagsSize: Int,
        title: String?,
        content: String?,
        author: String?,
        pageable: Pageable
    ): Page<CurationProjectionDto> {
        val c = QCuration.curation
        val ct = QCurationTag.curationTag
        val t = QTag.tag
        val m = QMember.member
        val cm = QComment.comment

        val conditions = mutableListOf<com.querydsl.core.types.Predicate>()

        if (!title.isNullOrBlank()) {
            conditions.add(c.title.contains(title))
        }
        if (!content.isNullOrBlank()) {
            conditions.add(c.content.contains(content))
        }
        if (!author.isNullOrBlank()) {
            conditions.add(m.username.contains(author))
        }
        if (!tags.isNullOrEmpty()) {
            conditions.add(t.name.`in`(tags))
        }

        val query = queryFactory
            .select(
                QCurationProjectionDto(
                    c.id,
                    c.title,
                    c.content,
                    c.viewCount,
                    m.username,
                    m.profileImage,
                    c.createdAt,
                    c.modifiedAt,
                    cm.id.count().castToNum(Int::class.java)
                )
            )
            .from(c)
            .leftJoin(c.member, m)
            .leftJoin(c.tags, ct)
            .leftJoin(ct.tag, t)
            .leftJoin(cm).on(cm.curation.eq(c))
            .where(*conditions.toTypedArray())
            .groupBy(
                c.id,
                c.title,
                c.content,
                c.viewCount,
                m.username,
                m.profileImage,
                c.createdAt,
                c.modifiedAt
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val content = query.fetch()

        val countQuery = queryFactory
            .select(c.countDistinct())
            .from(c)
            .leftJoin(c.member, m)
            .leftJoin(c.tags, ct)
            .leftJoin(ct.tag, t)
            .where(*conditions.toTypedArray())

        return PageImpl(content, pageable, countQuery.fetchOne() ?: 0L)
    }
}
