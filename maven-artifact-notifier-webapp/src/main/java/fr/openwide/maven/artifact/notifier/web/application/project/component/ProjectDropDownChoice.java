package fr.openwide.maven.artifact.notifier.web.application.project.component;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.retzlaff.select2.ISelect2AjaxAdapter;

import fr.openwide.core.wicket.more.markup.html.select2.GenericSelect2AjaxDropDownSingleChoice;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;

public class ProjectDropDownChoice extends GenericSelect2AjaxDropDownSingleChoice<Project> {

	private static final long serialVersionUID = -6782229493391720861L;

	public static final IChoiceRenderer<Project> CHOICE_RENDERER = new ProjectChoiceRenderer();

	public ProjectDropDownChoice(String id, IModel<Project> model) {
		this(id, model, new ProjectSelect2AjaxAdapter(CHOICE_RENDERER));
	}
	
	public ProjectDropDownChoice(String id, IModel<Project> model, ISelect2AjaxAdapter<Project> adapter) {
		super(id, model, adapter);
	}

	private static class ProjectChoiceRenderer extends ChoiceRenderer<Project> {

		private static final long serialVersionUID = -489354478759279358L;

		@Override
		public Object getDisplayValue(Project project) {
			return project != null ? project.getName() : null;
		}

		@Override
		public String getIdValue(Project project, int index) {
			if (project != null) {
				return String.valueOf(project.getId());
			}
			return null;
		}
	}
}