package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.retzlaff.select2.Select2Settings;

import com.google.inject.internal.Lists;

import fr.openwide.core.wicket.more.markup.html.select2.GenericSelect2DropDownSingleChoice;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;

public class ArtifactDeprecationStatusDropDownChoice extends GenericSelect2DropDownSingleChoice<ArtifactDeprecationStatus> {

	private static final long serialVersionUID = -6782229493391720861L;

	public ArtifactDeprecationStatusDropDownChoice(String id, IModel<ArtifactDeprecationStatus> model) {
		super(id, (model != null) ? model : Model.of(ArtifactDeprecationStatus.NORMAL),
				new ArtifactDeprecationStatusChoiceList(), new ArtifactDeprecationStatusChoiceRenderer());
		setNullValid(false);
	}
	
	@Override
	protected void fillSelect2Settings(Select2Settings settings) {
		super.fillSelect2Settings(settings);
		settings.setAllowClear(false);
		settings.setMinimumResultsForSearch(Integer.MAX_VALUE);
	}

	private static class ArtifactDeprecationStatusChoiceRenderer extends EnumChoiceRenderer<ArtifactDeprecationStatus> {
		
		private static final long serialVersionUID = -2756536733838634068L;
		
	}
	
	private static class ArtifactDeprecationStatusChoiceList extends LoadableDetachableModel<List<ArtifactDeprecationStatus>> {

		private static final long serialVersionUID = 4991853466150310164L;

		@Override
		protected List<ArtifactDeprecationStatus> load() {
			
			List<ArtifactDeprecationStatus> list = Lists.newArrayList();
			Collections.addAll(list, ArtifactDeprecationStatus.values());
			
			return list;
		}
		
	}

}