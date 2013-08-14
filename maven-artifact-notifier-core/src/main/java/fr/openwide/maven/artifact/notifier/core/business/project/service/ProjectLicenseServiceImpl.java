package fr.openwide.maven.artifact.notifier.core.business.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.project.dao.IProjectLicenseDao;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectLicense;

@Service("projectLicenseService")
public class ProjectLicenseServiceImpl extends GenericEntityServiceImpl<Long, ProjectLicense> implements IProjectLicenseService {
	
	@Autowired
	public ProjectLicenseServiceImpl(IProjectLicenseDao projectLicenseDao) {
		super(projectLicenseDao);
	}
}
