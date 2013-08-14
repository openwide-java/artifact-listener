package fr.openwide.maven.artifact.notifier.core.business.project.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectLicense;

@Repository("projectLicenseDao")
public class ProjectLicenseDaoImpl extends GenericEntityDaoImpl<Long, ProjectLicense> implements IProjectLicenseDao {
	
}