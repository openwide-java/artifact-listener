package fr.openwide.maven.artifact.notifier.core.business.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.project.dao.IProjectDao;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;

@Service("projectService")
public class ProjectServiceImpl extends GenericEntityServiceImpl<Long, Project> implements IProjectService {
	
	private IProjectDao projectDao;

	@Autowired
	public ProjectServiceImpl(IProjectDao projectDao) {
		super(projectDao);
		this.projectDao = projectDao;
	}

}
