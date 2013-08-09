package fr.openwide.maven.artifact.notifier.core.business.project.dao;

import java.util.List;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;

public interface IProjectDao extends IGenericEntityDao<Long, Project> {

	List<Project> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException;

	List<Project> searchByName(String searchTerm, Integer limit, Integer offset);

	int countSearchByName(String searchTerm);
}
