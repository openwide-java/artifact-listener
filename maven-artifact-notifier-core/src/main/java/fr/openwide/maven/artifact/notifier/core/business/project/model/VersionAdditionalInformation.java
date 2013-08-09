package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class VersionAdditionalInformation implements Serializable {

	private static final long serialVersionUID = -5168406926914345665L;
	
	@Column
	private String changelogUrl;
	
	@Column
	private String releaseNotesUrl;
	
	@Column
	private String announceUrl;
	
	public String getChangelogUrl() {
		return changelogUrl;
	}
	
	public void setChangelogUrl(String changelogUrl) {
		this.changelogUrl = changelogUrl;
	}
	
	public String getReleaseNotesUrl() {
		return releaseNotesUrl;
	}

	public void setReleaseNotesUrl(String releaseNotesUrl) {
		this.releaseNotesUrl = releaseNotesUrl;
	}

	public String getAnnounceUrl() {
		return announceUrl;
	}

	public void setAnnounceUrl(String announceUrl) {
		this.announceUrl = announceUrl;
	}

}
