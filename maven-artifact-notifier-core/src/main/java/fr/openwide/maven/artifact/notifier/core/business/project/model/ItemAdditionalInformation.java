package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.google.common.collect.Lists;

import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

@Embeddable
public class ItemAdditionalInformation implements Serializable {

	private static final long serialVersionUID = -5168406926914345665L;

	// NOTE: The orphanRemoval does not work here
	// See the workaround in fr.openwide.maven.artifact.notifier.web.application.url.model.ExternalLinkWrapperWrapModel
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper websiteUrl;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper issueTrackerUrl;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper scmUrl;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ExternalLinkWrapper changelogUrl;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<ProjectLicense> licenses = Lists.newArrayList();

	public ExternalLinkWrapper getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(ExternalLinkWrapper websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public ExternalLinkWrapper getScmUrl() {
		return scmUrl;
	}

	public void setScmUrl(ExternalLinkWrapper scmUrl) {
		this.scmUrl = scmUrl;
	}

	public ExternalLinkWrapper getIssueTrackerUrl() {
		return issueTrackerUrl;
	}

	public void setIssueTrackerUrl(ExternalLinkWrapper issueTrackerUrl) {
		this.issueTrackerUrl = issueTrackerUrl;
	}

	public ExternalLinkWrapper getChangelogUrl() {
		return changelogUrl;
	}

	public void setChangelogUrl(ExternalLinkWrapper changelogUrl) {
		this.changelogUrl = changelogUrl;
	}

	public List<ProjectLicense> getLicenses() {
		return Collections.unmodifiableList(licenses);
	}

	public void addLicense(ProjectLicense license) {
		if (license != null) {
			licenses.add(license);
		}
	}

	public void setLicenses(List<ProjectLicense> licenses) {
		this.licenses.clear();
		this.licenses.addAll(licenses);
	}
}

