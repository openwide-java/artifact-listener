package fr.openwide.maven.artifact.notifier.web.application.common.component.navigation;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;

import fr.openwide.core.commons.util.functional.SerializablePredicate;
import fr.openwide.core.wicket.more.markup.html.basic.EnclosureBehavior;
import fr.openwide.core.wicket.more.markup.html.template.component.BodyBreadCrumbPanel;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;

public class MavenArtifactNotifierBodyBreadCrumbPanel extends BodyBreadCrumbPanel {

	private static final long serialVersionUID = 3560734634512197550L;

	public MavenArtifactNotifierBodyBreadCrumbPanel(String id,
			IModel<List<BreadCrumbElement>> prependedBreadCrumbElementsModel,
			IModel<List<BreadCrumbElement>> breadCrumbElementsModel) {
		super(id, prependedBreadCrumbElementsModel, breadCrumbElementsModel);
		
		remove(this.getBehaviors(EnclosureBehavior.class).toArray(new EnclosureBehavior[0]));
		
		add(new EnclosureBehavior().model(new MoreThanOneElementPredicate(), breadCrumbElementsModel));
	}
	
	private class MoreThanOneElementPredicate implements SerializablePredicate<Collection<?>> {
		private static final long serialVersionUID = 6959237159408573L;

		@Override
		public boolean apply(Collection<?> input) {
			return input != null && input.size() > 1;
		}
	}

}
