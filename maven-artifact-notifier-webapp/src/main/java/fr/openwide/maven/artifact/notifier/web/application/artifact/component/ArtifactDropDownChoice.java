package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.retzlaff.select2.Select2Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.select2.AbstractLongIdGenericEntitySelect2AjaxAdapter;
import fr.openwide.core.wicket.more.markup.html.select2.GenericSelect2AjaxDropDownSingleChoice;
import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.core.wicket.more.markup.html.select2.util.Select2Utils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

public class ArtifactDropDownChoice extends GenericSelect2AjaxDropDownSingleChoice<Artifact> {

	private static final long serialVersionUID = -6782229493391720861L;

	private static final IChoiceRenderer<Artifact> CHOICE_RENDERER = new ArtifactChoiceRenderer();

	public ArtifactDropDownChoice(String id, IModel<Artifact> model) {
		super(id, model, new ArtifactSelect2AjaxAdapter(CHOICE_RENDERER));
		setWidth(DropDownChoiceWidth.XLARGE);
	}
	
	@Override
	protected void fillSelect2Settings(Select2Settings settings) {
		super.fillSelect2Settings(settings);
		settings.setDropdownCssClass(Select2Utils.CSS_DROP_XLARGE);
	}

	private static class ArtifactChoiceRenderer implements IChoiceRenderer<Artifact> {
		private static final long serialVersionUID = -4610661508328127491L;

		@Override
		public Object getDisplayValue(Artifact object) {
			if (object != null) {
				StringBuilder builder = new StringBuilder()
					.append(object.getGroup().getGroupId())
					.append(":")
					.append(object.getArtifactId());
				return builder.toString();
			}
			return null;
		}

		@Override
		public String getIdValue(Artifact object, int index) {
			if (object != null) {
				return String.valueOf(object.getId());
			}
			return null;
		}
	}

	private static class ArtifactSelect2AjaxAdapter extends AbstractLongIdGenericEntitySelect2AjaxAdapter<Artifact> {
		private static final long serialVersionUID = -4266223663082792490L;

		private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactSelect2AjaxAdapter.class);

		@SpringBean
		private IArtifactService artifactService;

		public ArtifactSelect2AjaxAdapter(IChoiceRenderer<Artifact> choiceRenderer) {
			super(Artifact.class, choiceRenderer);
		}

		@Override
		public List<Artifact> getChoices(int start, int count, String term) {
			try {
				return artifactService.searchAutocomplete(term, count, start);
			} catch (ServiceException e) {
				LOGGER.error("Error while searching for artifacts");
				return Lists.newArrayListWithExpectedSize(0);
			}
		}
	}
}