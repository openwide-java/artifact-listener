package fr.openwide.maven.artifact.notifier.core.business.project.dao;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.QProjectVersion;

@Repository("projectVersionDao")
public class ProjectVersionDaoImpl extends GenericEntityDaoImpl<Long, ProjectVersion> implements IProjectVersionDao {
	
	private static final QProjectVersion qProjectVersion = QProjectVersion.projectVersion;
	
	@Override
	public ProjectVersion getByProjectAndVersion(Project project, String version) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qProjectVersion)
			.where(qProjectVersion.project.eq(project))
			.where(qProjectVersion.version.eq(version));
		
		return query.uniqueResult(qProjectVersion);
	}
}