package fr.openwide.maven.artifact.notifier.core.business.project.dao;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;

public interface IProjectVersionDao extends IGenericEntityDao<Long, ProjectVersion> {

	ProjectVersion getByProjectAndVersion(Project project, String version);
}
