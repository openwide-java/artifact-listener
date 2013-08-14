package fr.openwide.maven.artifact.notifier.web.application.common.component.navigation;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;

import com.google.common.collect.Lists;

/**
 * Un {@link BookmarkablePageLink} qui exploite les {@link DynamicPageParameters}.
 */
public class DynamicBookmarkablePageLink<T> extends BookmarkablePageLink<T> {

	private static final long serialVersionUID = -7297463634865525448L;
	
	private final DynamicPageParameters dynamicParameters;
	
	private final List<String> mandatoryParameters = Lists.newArrayList();
	
	public <C extends Page> DynamicBookmarkablePageLink(String id, Class<C> pageClass, DynamicPageParameters dynamicParameters) {
		super(id, pageClass);
		this.dynamicParameters = dynamicParameters;
	}
	
	@Override
	public PageParameters getPageParameters() {
		return dynamicParameters.buildStaticPageParameters();
	}
	
	public DynamicBookmarkablePageLink<T> mandatoryParameter(String parameterName) {
		Args.notNull(parameterName, "parameterName");
		
		mandatoryParameters.add(parameterName);
		
		return this;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		PageParameters staticPageParameters = getPageParameters();
		for (String mandatoryParameter : mandatoryParameters) {
			if (staticPageParameters.get(mandatoryParameter).isNull()) {
				setVisible(false);
				return;
			}
		}
		
		setVisible(true);
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		dynamicParameters.detach();
	}

}
