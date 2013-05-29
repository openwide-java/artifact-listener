package fr.openwide.maven.artifact.notifier.web.application.common.template.model;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;

public class MavenArtifactNotifierNavigationMenuItem extends NavigationMenuItem {

	private static final long serialVersionUID = 1372603188892024225L;
	
	private List<NavigationMenuItem> sousMenus = Lists.newArrayList();
	
	public MavenArtifactNotifierNavigationMenuItem(IModel<String> labelModel) {
		super(labelModel);
	}
	
	public MavenArtifactNotifierNavigationMenuItem(IModel<String> labelModel, Class<? extends Page> pageClass) {
		super(labelModel, pageClass, null);
	}
	
	public MavenArtifactNotifierNavigationMenuItem(IModel<String> labelModel, Class<? extends Page> pageClass, PageParameters pageParameters) {
		super(labelModel, pageClass, pageParameters);
	}
	
	public void addSousMenu(NavigationMenuItem sousMenu) {
		sousMenus.add(sousMenu);
	}
	
	public List<NavigationMenuItem> getSousMenus() {
		return sousMenus;
	}

}
