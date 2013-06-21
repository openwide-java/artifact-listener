package fr.openwide.maven.artifact.notifier.core.business.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Indexed;

import fr.openwide.core.jpa.more.business.generic.model.GenericListItem;

@Entity
@Indexed
public class ProjectLicense extends GenericListItem<ProjectLicense> {

	private static final long serialVersionUID = -586542589672564851L;

	@Column
	private String licenseUrl;
	
	protected ProjectLicense() {
	}
	
	public ProjectLicense(String label) {
		super(label, 0);
	}

	public String getLicenseUrl() {
		return licenseUrl;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}
	
}