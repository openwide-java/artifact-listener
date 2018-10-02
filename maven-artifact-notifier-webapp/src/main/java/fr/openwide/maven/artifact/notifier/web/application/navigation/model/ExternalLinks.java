package fr.openwide.maven.artifact.notifier.web.application.navigation.model;

import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

import java.io.Serializable;

public final class ExternalLinks implements Serializable {
	
	private static final long serialVersionUID = -7876094131642733161L;
	
	private static ExternalLinks instance;
	
	private final String smile;
	
	private final String gitHubProject;
	
	private final String twitter;
	
	private final String wicket;
	
	private final String spring;
	
	private final String hibernate;
	
	private final String hibernateSearch;
	
	private final String jsoup;
	
	private final String twitterBootstrap;
	
	private final String mavenCentral;
	
	private ExternalLinks(MavenArtifactNotifierConfigurer configurer) {
		smile = configurer.getLinkSmile();
		gitHubProject = configurer.getLinkGitHubProject();
		twitter = configurer.getLinkTwitterAccount();
		wicket = configurer.getLinkWicket();
		spring = configurer.getLinkSpring();
		hibernate = configurer.getLinkHibernate();
		hibernateSearch = configurer.getLinkHibernateSearch();
		jsoup = configurer.getLinkJsoup();
		twitterBootstrap = configurer.getLinkTwitterBootstrap();
		mavenCentral = configurer.getLinkMavenCentral();
	}
	
	public String getSmile() {
		return smile;
	}
	
	public String getGitHubProject() {
		return gitHubProject;
	}
	
	public String getTwitter() {
		return twitter;
	}
	
	public String getWicket() {
		return wicket;
	}
	
	public String getSpring() {
		return spring;
	}
	
	public String getHibernate() {
		return hibernate;
	}
	
	public String getHibernateSearch() {
		return hibernateSearch;
	}
	
	public String getJsoup() {
		return jsoup;
	}
	
	public String getTwitterBootstrap() {
		return twitterBootstrap;
	}
	
	public String getMavenCentral() {
		return mavenCentral;
	}
	
	public static ExternalLinks get(MavenArtifactNotifierConfigurer configurer) {
		if (instance != null) {
			return instance;
		}
		instance = new ExternalLinks(configurer);
		return instance;
	}
}
