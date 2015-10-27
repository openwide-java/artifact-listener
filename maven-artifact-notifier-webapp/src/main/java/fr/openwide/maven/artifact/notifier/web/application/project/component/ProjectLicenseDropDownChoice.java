package fr.openwide.maven.artifact.notifier.web.application.project.component;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectLicense;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectLicenseService;
import fr.openwide.maven.artifact.notifier.web.application.common.component.GenericSelect2DropDownMultipleChoice;

public class ProjectLicenseDropDownChoice extends GenericSelect2DropDownMultipleChoice<ProjectLicense> {

	private static final long serialVersionUID = 1005415196382301008L;
	
	public static final IChoiceRenderer<ProjectLicense> CHOICE_RENDERER = new ProjectLicenseChoiceRenderer();

	public ProjectLicenseDropDownChoice(String id, IModel<? extends Collection<ProjectLicense>> model) {
		super(id, model, new ChoicesModel(), CHOICE_RENDERER);
		setWidth(DropDownChoiceWidth.XLARGE);
	}
	
	private static class ChoicesModel extends LoadableDetachableModel<List<ProjectLicense>> {
		private static final long serialVersionUID = 1L;
		
		@SpringBean
		private IProjectLicenseService projectLicenseService;
		
		public ChoicesModel() {
			Injector.get().inject(this);
		}
		
		@Override
		protected List<ProjectLicense> load() {
			return projectLicenseService.list();
		}
	}

	private static class ProjectLicenseChoiceRenderer extends ChoiceRenderer<ProjectLicense> {

		private static final long serialVersionUID = -489354478759279358L;

		@Override
		public Object getDisplayValue(ProjectLicense license) {
			if (license != null) {
				StringBuilder sb = new StringBuilder(license.getShortLabel())
						.append(" / ")
						.append(license.getLabel());
				return sb.toString();
			}
			return null;
		}

		@Override
		public String getIdValue(ProjectLicense license, int index) {
			if (license != null) {
				return String.valueOf(license.getId());
			}
			return null;
		}
	}
}