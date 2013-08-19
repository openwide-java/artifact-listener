package fr.openwide.maven.artifact.notifier.core.business.project.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;

public interface IProjectVersionService extends IGenericEntityService<Long, ProjectVersion> {

	ProjectVersion getByProjectAndVersion(Project project, String version);

	void linkWithArtifactVersions(ProjectVersion projectVersion) throws ServiceException, SecurityServiceException;
}