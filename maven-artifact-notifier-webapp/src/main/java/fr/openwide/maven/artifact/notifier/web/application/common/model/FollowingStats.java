package fr.openwide.maven.artifact.notifier.web.application.common.model;

import java.io.Serializable;

public class FollowingStats implements Serializable {
	
	private static final long serialVersionUID = 2094739537159184340L;
	
	private Long userCount;
	
	private Long followedArtifactCount;
	
	public FollowingStats(Long userCount, Long followedArtifactCount) {
		this.userCount = userCount;
		this.followedArtifactCount = followedArtifactCount;
	}
	
	public Long getUserCount() {
		return userCount;
	}
	
	public void setUserCount(Long userCount) {
		this.userCount = userCount;
	}
	
	public Long getFollowedArtifactCount() {
		return followedArtifactCount;
	}
	
	public void setFollowedArtifactCount(Long followedArtifactCount) {
		this.followedArtifactCount = followedArtifactCount;
	}
	
}
