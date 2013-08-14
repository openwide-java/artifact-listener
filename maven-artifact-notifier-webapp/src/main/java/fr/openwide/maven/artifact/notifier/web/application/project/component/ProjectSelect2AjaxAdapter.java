package fr.openwide.maven.artifact.notifier.web.application.project.component;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.select2.AbstractLongIdGenericEntitySelect2AjaxAdapter;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;

public class ProjectSelect2AjaxAdapter extends AbstractLongIdGenericEntitySelect2AjaxAdapter<Project> {

	private static final long serialVersionUID = -2067969926451983702L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectSelect2AjaxAdapter.class);

	@SpringBean
	private IProjectService projectService;

	public ProjectSelect2AjaxAdapter(IChoiceRenderer<Project> choiceRenderer) {
		super(Project.class, choiceRenderer);
	}

	@Override
	public List<Project> getChoices(int start, int count, String term) {
		try {
			return projectService.searchAutocomplete(term, count, start);
		} catch (ServiceException e) {
			LOGGER.error("Error while searching for projects");
			return Lists.newArrayListWithExpectedSize(0);
		}
	}
}