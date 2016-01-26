package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import fr.openwide.core.jpa.externallinkchecker.business.model.ExternalLinkWrapper;

@Embeddable
public class VersionAdditionalInformation implements Serializable {

	private static final long serialVersionUID = -5168406926914345665L;
	
	// NOTE: The orphanRemoval does not work here
	// See the workaround in fr.openwide.maven.artifact.notifier.web.application.url.model.ExternalLinkWrapperWrapModel
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper changelogUrl;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper releaseNotesUrl;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper announceUrl;
	
	public ExternalLinkWrapper getChangelogUrl() {
		return changelogUrl;
	}
	
	public void setChangelogUrl(ExternalLinkWrapper changelogUrl) {
		this.changelogUrl = changelogUrl;
	}
	
	public ExternalLinkWrapper getReleaseNotesUrl() {
		return releaseNotesUrl;
	}

	public void setReleaseNotesUrl(ExternalLinkWrapper releaseNotesUrl) {
		this.releaseNotesUrl = releaseNotesUrl;
	}

	public ExternalLinkWrapper getAnnounceUrl() {
		return announceUrl;
	}

	public void setAnnounceUrl(ExternalLinkWrapper announceUrl) {
		this.announceUrl = announceUrl;
	}

}
