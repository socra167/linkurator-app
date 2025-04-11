package com.team8.project2.domain.curation.curation.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.team8.project2.domain.comment.entity.QComment
import com.team8.project2.domain.curation.curation.dto.CurationProjectionDto
import com.team8.project2.domain.curation.curation.dto.CurationResDto
import com.team8.project2.domain.curation.curation.entity.QCuration
import com.team8.project2.domain.curation.curation.entity.QCurationLink
import com.team8.project2.domain.curation.curation.entity.QCurationTag
import com.team8.project2.domain.curation.tag.entity.QTag
import com.team8.project2.domain.link.entity.QLink
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
        val cl = QCurationLink.curationLink
        val l = QLink.link

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

        val curations = queryFactory
            .select(c)
            .from(c)
            .leftJoin(c.member, m).fetchJoin()
            .leftJoin(c.tags, ct).leftJoin(ct.tag, t)
            .leftJoin(c.curationLinks, cl).leftJoin(cl.link, l)
            .leftJoin(cm).on(cm.curation.eq(c))
            .where(*conditions.toTypedArray())
            .groupBy(c.id)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val result = curations.map { curation ->
            val tagNames = curation.tags.mapNotNull { it.tag.name }
            val linkDtos = curation.curationLinks.map { CurationResDto.LinkResDto.from(it.link) }
            val commentCount = curation.comments.size

            CurationProjectionDto(
                id = curation.id!!,
                title = curation.title,
                content = curation.content,
                viewCount = curation.viewCount,
                authorName = curation.member.getUsername(),
                memberImgUrl = curation.member.profileImage,
                createdAt = curation.createdAt,
                modifiedAt = curation.modifiedAt,
                commentCount = commentCount,
                tags = tagNames,
                urls = linkDtos
            )
        }

        val total = queryFactory
            .select(c.countDistinct())
            .from(c)
            .leftJoin(c.tags, ct).leftJoin(ct.tag, t)
            .leftJoin(c.member, m)
            .where(*conditions.toTypedArray())
            .fetchOne() ?: 0L

        return PageImpl(result, pageable, total)
    }
}
