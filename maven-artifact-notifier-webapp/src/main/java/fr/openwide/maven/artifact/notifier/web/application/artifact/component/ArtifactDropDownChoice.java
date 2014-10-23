package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.retzlaff.select2.ISelect2AjaxAdapter;
import org.retzlaff.select2.Select2Settings;

import fr.openwide.core.wicket.more.markup.html.select2.GenericSelect2AjaxDropDownSingleChoice;
import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;

public class ArtifactDropDownChoice extends GenericSelect2AjaxDropDownSingleChoice<Artifact> {

	private static final long serialVersionUID = -6782229493391720861L;

	public static final IChoiceRenderer<Artifact> CHOICE_RENDERER = new ArtifactChoiceRenderer();

	public ArtifactDropDownChoice(String id, IModel<Artifact> model) {
		this(id, model, new ArtifactSelect2AjaxAdapter(CHOICE_RENDERER));
	}
	
	public ArtifactDropDownChoice(String id, IModel<Artifact> model, ISelect2AjaxAdapter<Artifact> adapter) {
		super(id, model, adapter);
		setWidth(DropDownChoiceWidth.XLARGE);
	}
	
	@Override
	protected void fillSelect2Settings(Select2Settings settings) {
		super.fillSelect2Settings(settings);
	}

	private static class ArtifactChoiceRenderer implements IChoiceRenderer<Artifact> {
		private static final long serialVersionUID = -4610661508328127491L;

		@Override
		public Object getDisplayValue(Artifact artifact) {
			return artifact != null ? artifact.getArtifactKey().getKey() : null;
		}

		@Override
		public String getIdValue(Artifact object, int index) {
			if (object != null) {
				return String.valueOf(object.getId());
			}
			return null;
		}
	}
}