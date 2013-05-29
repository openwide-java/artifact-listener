package fr.openwide.maven.artifact.notifier.web.application.common.template.styles;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.lesscss.LessCssResourceReference;
import fr.openwide.core.wicket.more.markup.html.template.css.jqueryui.JQueryUiCssResourceReference;

public final class StylesLessCssResourceReference extends LessCssResourceReference {

	private static final long serialVersionUID = -6024518060296236148L;

	private static final StylesLessCssResourceReference INSTANCE = new StylesLessCssResourceReference();

	private StylesLessCssResourceReference() {
		super(StylesLessCssResourceReference.class, "styles.less");
	}
	
	@Override
	public Iterable<? extends HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = Lists.newArrayListWithExpectedSize(1);
		dependencies.add(CssHeaderItem.forReference(JQueryUiCssResourceReference.get()));
		return dependencies;
	}

	public static StylesLessCssResourceReference get() {
		return INSTANCE;
	}

}
