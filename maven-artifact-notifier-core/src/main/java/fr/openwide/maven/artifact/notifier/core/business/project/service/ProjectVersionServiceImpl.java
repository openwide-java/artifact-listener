package fr.openwide.maven.artifact.notifier.core.business.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.project.dao.IProjectVersionDao;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionStatus;

@Service("projectVersionService")
public class ProjectVersionServiceImpl extends GenericEntityServiceImpl<Long, ProjectVersion> implements IProjectVersionService {
	
	@Autowired
	private IArtifactVersionService artifactVersionService;
	
	private IProjectVersionDao projectVersionDao;

	@Autowired
	public ProjectVersionServiceImpl(IProjectVersionDao projectVersionDao) {
		super(projectVersionDao);
		this.projectVersionDao = projectVersionDao;
	}
	
	@Override
	public void delete(ProjectVersion projectVersion) throws ServiceException, SecurityServiceException {
		List<ArtifactVersion> artifactVersions = artifactVersionService.listByProjectVersion(projectVersion);
		for (ArtifactVersion artifactVersion : artifactVersions) {
			artifactVersion.setProjectVersion(null);
			artifactVersionService.update(artifactVersion);
		}
		super.delete(projectVersion);
	}
	
	@Override
	public ProjectVersion getByProjectAndVersion(Project project, String version) {
		return projectVersionDao.getByProjectAndVersion(project, version);
	}
	
	@Override
	public void linkWithArtifactVersions(ProjectVersion projectVersion) throws ServiceException, SecurityServiceException {
		boolean onMavenCentral = false;
		Date lastUpdateDate = null;
		for (Artifact artifact : projectVersion.getProject().getArtifacts()) {
			ArtifactVersion artifactVersion = artifactVersionService.getByArtifactAndVersion(artifact, projectVersion.getVersion());
			
			if (artifactVersion != null) {
				artifactVersion.setProjectVersion(projectVersion);
				artifactVersionService.update(artifactVersion);
				
				lastUpdateDate = artifactVersion.getLastUpdateDate();
				onMavenCentral = true;
			}
		}
		if (onMavenCentral) {
			projectVersion.setLastUpdateDate(lastUpdateDate);
			projectVersion.setStatus(ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL);
			update(projectVersion);
		}
	}
}
