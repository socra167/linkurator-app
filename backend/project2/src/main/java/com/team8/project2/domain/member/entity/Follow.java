package com.team8.project2.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Follow {

	@EmbeddedId
	@Column(name = "followId")
	private FollowId id;

	@ManyToOne()
	@JoinColumn(name = "followerId", insertable=false, updatable=false)
	private Member follower;

	@ManyToOne
	@JoinColumn(name = "followeeId", insertable=false, updatable=false)
	private Member followee;

	@Setter(AccessLevel.PRIVATE)
	@CreatedDate
	private LocalDateTime followedAt;

	@Getter
	@Setter
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class FollowId {
		private Long followerId;
		private Long followeeId;
	}


	public void setFollowerAndFollowee(Member follower, Member followee) {
		FollowId followId = new FollowId();
		followId.setFollowerId(follower.getId());
		followId.setFolloweeId(followee.getId());
		this.id = followId;
		this.follower = follower;
		this.followee = followee;
	}

	public LocalDateTime getFollowedAt() {
		return followedAt;
	}

	public String getFolloweeName() {
		return followee.getUsername();
	}

	public String getFolloweeProfileImage() {
		return followee.getProfileImage();
	}
}
