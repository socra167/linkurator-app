package com.team8.project2.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsResDto {
    private long totalCurationViews;
    private long totalCurationLikes;
    private long totalPlaylistViews;
    private long totalPlaylistLikes;
}
