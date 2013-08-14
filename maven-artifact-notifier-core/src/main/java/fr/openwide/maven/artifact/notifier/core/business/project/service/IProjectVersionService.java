package fr.openwide.maven.artifact.notifier.core.business.project.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;

public interface IProjectVersionService extends IGenericEntityService<Long, ProjectVersion> {

	ProjectVersion getByProjectAndVersion(Project project, String version);
}