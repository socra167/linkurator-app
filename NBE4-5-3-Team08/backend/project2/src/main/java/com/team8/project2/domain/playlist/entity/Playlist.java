package com.team8.project2.domain.playlist.entity;

import com.team8.project2.domain.curation.tag.entity.Tag;
import com.team8.project2.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 플레이리스트(Playlist) 엔티티 클래스입니다.
 * 사용자가 생성한 플레이리스트 정보를 저장합니다.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    /**
     * 플레이리스트의 고유 ID (자동 생성)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 플레이리스트 제목 (필수값)
     */
    @Column(nullable = false)
    private String title;

    /**
     * 플레이리스트 설명 (필수값)
     */
    @Column(nullable = false)
    private String description;

    /**
     * 플레이리스트 공개 여부 (기본값: 공개)
     */
    @Column(nullable = false)
    private boolean isPublic = true;

    /**
     * ✅ 플레이리스트 조회수 및 좋아요 수 추가
     */
    @Column(nullable = false)
    private long viewCount = 0L; // 기본값 0

    @Column(nullable = false)
    private long likeCount = 0L; // 기본값 0

    /**
     * 플레이리스트에 포함된 항목 목록 (1:N 관계)
     */
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<PlaylistItem> items = new ArrayList<>();

    /**
     * 플레이리스트 정보를 수정하는 메서드
     * @param title 변경할 제목 (null일 경우 변경 없음)
     * @param description 변경할 설명 (null일 경우 변경 없음)
     * @param isPublic 변경할 공개 여부 (null일 경우 변경 없음)
     */
    public void updatePlaylist(String title, String description, Boolean isPublic) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (isPublic != null) this.isPublic = isPublic;
    }

    /**
     * 플레이리스트 연관 추천 태그
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "PlaylistTag",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * ✅ 태그 목록을 문자열 Set으로 변환하여 반환
     */

    public Set<String> getTagNames() {
        return this.tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateLikeCount(long newLikeCount) {
        this.likeCount = newLikeCount;
    }

}