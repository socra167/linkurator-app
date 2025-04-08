package com.team8.project2.domain.admin.dto

data class StatsResDto(
    private val totalCurationViews: Long,
    private val totalCurationLikes: Long,
    private val totalPlaylistViews: Long,
    private val totalPlaylistLikes: Long
)
