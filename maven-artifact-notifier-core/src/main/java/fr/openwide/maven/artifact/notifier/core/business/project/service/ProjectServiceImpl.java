package fr.openwide.maven.artifact.notifier.core.business.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.audit.service.IAuditService;
import fr.openwide.maven.artifact.notifier.core.business.project.dao.IProjectDao;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionStatus;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project_;

@Service("projectService")
public class ProjectServiceImpl extends GenericEntityServiceImpl<Long, Project> implements IProjectService {
	
	@Autowired
	private IProjectVersionService projectVersionService;
	
	@Autowired
	private IArtifactService artifactService;
	
	@Autowired
	private IArtifactVersionService artifactVersionService;

	@Autowired
	private IAuditService auditService;
	
	private IProjectDao projectDao;
	
	@Autowired
	public ProjectServiceImpl(IProjectDao projectDao) {
		super(projectDao);
		this.projectDao = projectDao;
	}
	
	@Override
	protected void createEntity(Project project) throws ServiceException, SecurityServiceException {
		auditService.refreshAuditSummaryForCreate(project.getAuditSummary());
		super.createEntity(project);
	}
	
	@Override
	protected void updateEntity(Project project) throws ServiceException, SecurityServiceException {
		auditService.refreshAuditSummaryForUpdate(project.getAuditSummary());
		super.updateEntity(project);
	}

	@Override
	public void delete(Project project) throws ServiceException, SecurityServiceException {
		for (Artifact artifact : project.getArtifacts()) {
			artifact.setProject(null);
			artifactService.update(artifact);
		}
		super.delete(project);
	}
	
	@Override
	public Project getByName(String name) {
		return projectDao.getByField(Project_.name, name);
	}
	
	@Override
	public Project getByUri(String uri) {
		return projectDao.getByField(Project_.uri, uri);
	}
	
	@Override
	public List<Project> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException {
		return projectDao.searchAutocomplete(searchPattern, limit, offset);
	}
	
	@Override
	public List<Project> searchByName(String searchPattern, Integer limit, Integer offset) {
		return projectDao.searchByName(searchPattern, limit, offset);
	}
	
	@Override
	public int countSearchByName(String searchTerm) {
		return projectDao.countSearchByName(searchTerm);
	}
	
	@Override
	public void createProjectVersion(Project project, ProjectVersion projectVersion) throws ServiceException, SecurityServiceException {
		projectVersion.setStatus(ProjectVersionStatus.IN_PROGRESS);
		projectVersionService.create(projectVersion);
		
		// We update artifact versions matching the project version
		boolean onMavenCentral = false;
		for (Artifact artifact : project.getArtifacts()) {
			ArtifactVersion artifactVersion = artifactVersionService.getByArtifactAndVersion(artifact, projectVersion.getVersion());
			
			if (artifactVersion != null) {
				artifactVersion.setProjectVersion(projectVersion);
				artifactVersionService.update(artifactVersion);
				onMavenCentral = true;
			}
		}
		if (onMavenCentral) {
			projectVersion.setStatus(ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL);
			projectVersionService.update(projectVersion);
		}
		
		project.addVersion(projectVersion);
		update(project);
	}
	
	@Override
	public void deleteProjectVersion(Project project, ProjectVersion projectVersion) throws ServiceException, SecurityServiceException {
		project.removeVersion(projectVersion);
		update(project);
		
		projectVersionService.delete(projectVersion);
	}
	
	@Override
	public void addArtifact(Project project, Artifact artifact) throws ServiceException, SecurityServiceException {
		project.addArtifact(artifact);
		update(project);
	}
	
	@Override
	public void removeArtifact(Project project, Artifact artifact) throws ServiceException, SecurityServiceException {
		project.removeArtifact(artifact);
		update(project);
	}
}
