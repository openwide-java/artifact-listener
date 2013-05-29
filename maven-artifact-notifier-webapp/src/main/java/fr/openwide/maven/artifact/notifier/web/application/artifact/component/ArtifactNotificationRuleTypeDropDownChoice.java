package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.retzlaff.select2.Select2Settings;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic.GenericSelect2DropDownSingleChoice;

public class ArtifactNotificationRuleTypeDropDownChoice extends GenericSelect2DropDownSingleChoice<ArtifactNotificationRuleType> {

	private static final long serialVersionUID = -6782229493391720861L;

	public ArtifactNotificationRuleTypeDropDownChoice(String id, IModel<ArtifactNotificationRuleType> model) {
		super(id, (model != null) ? model : Model.of(ArtifactNotificationRuleType.COMPLY),
				new TicketSearchStateChoiceList(), new TicketSearchStateChoiceRenderer());
		setNullValid(false);
	}
	
	@Override
	protected void fillSelect2Settings(Select2Settings settings) {
		super.fillSelect2Settings(settings);
		settings.setAllowClear(false);
		settings.setMinimumResultsForSearch(Integer.MAX_VALUE);
	}

	private static class TicketSearchStateChoiceRenderer extends EnumChoiceRenderer<ArtifactNotificationRuleType> {
		
		private static final long serialVersionUID = -2756536733838634068L;
		
	}
	
	private static class TicketSearchStateChoiceList extends LoadableDetachableModel<List<ArtifactNotificationRuleType>> {

		private static final long serialVersionUID = 4991853466150310164L;

		@Override
		protected List<ArtifactNotificationRuleType> load() {
			
			List<ArtifactNotificationRuleType> list = new ArrayList<ArtifactNotificationRuleType>();
			Collections.addAll(list, ArtifactNotificationRuleType.values());
			
			return list;
		}
		
	}

}