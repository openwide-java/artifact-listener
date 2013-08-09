package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import com.google.common.collect.Lists;

@Embeddable
public class ItemAdditionalInformation implements Serializable {

	private static final long serialVersionUID = -5168406926914345665L;

	@Column
	private String websiteUrl;

	@Column
	private String issueTrackerUrl;

	@Column
	private String scmUrl;

	@Column
	private String changelogUrl;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<ProjectLicense> licenses = Lists.newArrayList();

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public String getScmUrl() {
		return scmUrl;
	}

	public void setScmUrl(String scmUrl) {
		this.scmUrl = scmUrl;
	}

	public String getIssueTrackerUrl() {
		return issueTrackerUrl;
	}

	public void setIssueTrackerUrl(String issueTrackerUrl) {
		this.issueTrackerUrl = issueTrackerUrl;
	}

	public String getChangelogUrl() {
		return changelogUrl;
	}

	public void setChangelogUrl(String changelogUrl) {
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

