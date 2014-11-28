package fr.openwide.maven.artifact.notifier.core.config.application;

import fr.openwide.core.spring.config.CoreConfigurer;

public class MavenArtifactNotifierConfigurer extends CoreConfigurer {

	public int getPortfolioItemsPerPage() {
		return getPropertyAsInteger("portfolio.itemsPerPage", 20);
	}
	
	public int getArtifactSearchItemsPerPage() {
		return getPropertyAsInteger("artifact.search.itemsPerPage", 20);
	}
	
	public int getAdvisableArtifactItemsLimit() {
		return getPropertyAsInteger("artifact.search.recommended.limit", 5);
	}
	
	public String getArtifactRepositoryMetadataUrl() {
		return getPropertyAsString("repository.sync.metadata.url", "http://repo1.maven.org/maven2/%s/%s/maven-metadata.xml");
	}
	
	public String getArtifactVersionRepositoryPomUrl() {
		return getPropertyAsString("repository.sync.version.pom.url", "http://repo1.maven.org/maven2/%1$s/%2$s/%3$s/%2$s-%3$s.pom");
	}
	
	public String getAuthenticationCallbackBaseUrl() {
		return getPropertyAsString("authentication.callback.baseUrl");
	}
	
	public String getTwitterClientKey() {
		return getPropertyAsString("authentication.twitter.key");
	}
	
	public String getTwitterClientSecret() {
		return getPropertyAsString("authentication.twitter.secret");
	}
	
	public String getGitHubClientKey() {
		return getPropertyAsString("authentication.gitHub.key");
	}
	
	public String getGitHubClientSecret() {
		return getPropertyAsString("authentication.gitHub.secret");
	}
	
	public String getDummyThreadContextServerName() {
		return getPropertyAsString("scheduler.dummyThreadContext.serverName", "localhost");
	}
	
	public Integer getDummyThreadContextServerPort() {
		return getPropertyAsInteger("scheduler.dummyThreadContext.serverPort", 8080);
	}
	
	public String getDummyThreadContextScheme() {
		return getPropertyAsString("scheduler.dummyThreadContext.scheme", "http");
	}
	
	public Integer getSynchronizationPauseDelayBetweenRequestsInMilliseconds() {
		return getPropertyAsInteger("repository.sync.pauseDelayInMilliseconds", 10);
	}
	
	public Integer getLastNotificationsLimit() {
		return getPropertyAsInteger("notifications.limit", 30);
	}
	
	public Integer getLastNotificationsDayCount() {
		return getPropertyAsInteger("notifications.dayCount", 7);
	}
	
	public String getLinkOpenWide() {
		return getPropertyAsString("link.openwide");
	}
	
	public String getLinkGitHubProject() {
		return getPropertyAsString("link.gitHubProject");
	}
	
	public String getLinkTwitterAccount() {
		return getPropertyAsString("link.twitter");
	}
	
	public String getLinkWicket() {
		return getPropertyAsString("link.wicket");
	}
	
	public String getLinkSpring() {
		return getPropertyAsString("link.spring");
	}
	
	public String getLinkHibernate() {
		return getPropertyAsString("link.hibernate");
	}
	
	public String getLinkHibernateSearch() {
		return getPropertyAsString("link.hibernateSearch");
	}
	
	public String getLinkJsoup() {
		return getPropertyAsString("link.jsoup");
	}
	
	public String getLinkTwitterBootstrap() {
		return getPropertyAsString("link.twitterBootstrap");
	}
	
	public String getLinkMavenCentral() {
		return getPropertyAsString("link.mavenCentral");
	}
	
	public String getLinkContactUs() {
		return getPropertyAsString("link.contactUs");
	}
	
	public String getMavenCentralSearchUrlGroup() {
		return getPropertyAsString("mavenCentralSearchUrl.group");
	}
	
	public String getMavenCentralSearchUrlArtifact() {
		return getPropertyAsString("mavenCentralSearchUrl.artifact");
	}
	
	public String getMavenCentralSearchUrlVersion() {
		return getPropertyAsString("mavenCentralSearchUrl.version");
	}
	
	public String getGoogleAnalyticsTrackingId() {
		return getPropertyAsString("google.analytics.trackingId");
	}
	
	public Integer getAverageDataRange() {
		return getPropertyAsInteger("statistics.averageDataRange", 30);
	}
	
	public Integer getMostFollowedArtifactsLimit() {
		return getPropertyAsInteger("artifact.mostFollowed.limit", 3);
	}
	
	public Integer getRecentReleasesLimit() {
		return getPropertyAsInteger("artifact.recentReleases.limit", 3);
	}
	
	public Integer getLastUpdatesArtifactsLimit() {
		return getPropertyAsInteger("artifact.lastUpdates.artifacts.limit", 3);
	}
	
	public int getLastUpdatesDaysLimit() {
		return getPropertyAsInteger("artifact.lastUpdates.days.limit", 3);
	}
	
	public Integer getDeadLinkRequiredConsecutiveFailures() {
		return getPropertyAsInteger("link.checker.deadLink.consecutiveFailures", 5);
	}
	
	public String getUserAgent() {
		return getPropertyAsString("userAgent", "Artifact Listener (https://www.artifact-listener.org/)");
	}
}
