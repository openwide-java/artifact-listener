package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;
import java.util.Locale;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.retzlaff.select2.Select2Settings;

import com.google.common.collect.ImmutableList;

import fr.openwide.core.wicket.more.markup.html.select2.GenericSelect2DropDownSingleChoice;
import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class LocaleDropDownChoice extends GenericSelect2DropDownSingleChoice<Locale> {

	private static final long serialVersionUID = -6782229493391720861L;
	
	private static final LocaleChoiceRenderer CHOICE_RENDERER = new LocaleChoiceRenderer();
	
	public LocaleDropDownChoice(String id, IModel<Locale> model) {
		super(id, model, new LocaleChoiceList(), CHOICE_RENDERER);
		setNullValid(false);
		setWidth(DropDownChoiceWidth.SMALL);
	}
	
	@Override
	protected void fillSelect2Settings(Select2Settings settings) {
		super.fillSelect2Settings(settings);
		settings.setAllowClear(false);
		settings.setMinimumResultsForSearch(Integer.MAX_VALUE);
	}

	private static class LocaleChoiceRenderer implements IChoiceRenderer<Locale> {

		private static final long serialVersionUID = 2534709458895245968L;

		@Override
		public Object getDisplayValue(Locale object) {
			return object.getDisplayName(MavenArtifactNotifierSession.get().getLocale());
		}

		@Override
		public String getIdValue(Locale object, int index) {
			return object.getLanguage();
		}
		
	}
	
	private static class LocaleChoiceList extends LoadableDetachableModel<List<Locale>> {

		private static final long serialVersionUID = 4991853466150310164L;

		@SpringBean
		private MavenArtifactNotifierConfigurer configurer;
		
		public LocaleChoiceList() {
			Injector.get().inject(this);
		}
		
		@Override
		protected List<Locale> load() {
			return ImmutableList.copyOf(configurer.getAvailableLocales());
		}
	}
}