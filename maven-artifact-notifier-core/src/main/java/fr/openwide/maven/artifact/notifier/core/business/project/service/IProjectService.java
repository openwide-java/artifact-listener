package fr.openwide.maven.artifact.notifier.core.business.project.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;

public interface IProjectService extends IGenericEntityService<Long, Project> {
	
	Project getByName(String name);
	
	Project getByUri(String uri);
	
	List<Project> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException;
	
	List<Project> searchByName(String searchPattern, Integer limit, Integer offset);
	
	int countSearchByName(String searchTerm);

	void createProjectVersion(Project project, ProjectVersion projectVersion) throws ServiceException, SecurityServiceException;

	void deleteProjectVersion(Project project, ProjectVersion projectVersion) throws ServiceException, SecurityServiceException;
	
	void addArtifact(Project project, Artifact artifact) throws ServiceException, SecurityServiceException;

	void removeArtifact(Project project, Artifact artifact) throws ServiceException, SecurityServiceException;
}