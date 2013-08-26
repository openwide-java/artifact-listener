package fr.openwide.maven.artifact.notifier.web.application.common.template.model;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;

public class HierarchicalNavigationMenuItem extends NavigationMenuItem {

	private static final long serialVersionUID = 1372603188892024225L;
	
	private List<NavigationMenuItem> subMenuItems = Lists.newArrayList();
	
	public HierarchicalNavigationMenuItem(IModel<String> labelModel) {
		super(labelModel);
	}
	
	public HierarchicalNavigationMenuItem(IModel<String> labelModel, Class<? extends Page> pageClass) {
		super(labelModel, pageClass);
	}
	
	public HierarchicalNavigationMenuItem(IModel<String> labelModel, Class<? extends Page> pageClass, PageParameters pageParameters) {
		super(labelModel, pageClass, pageParameters);
	}
	
	public HierarchicalNavigationMenuItem(IModel<String> labelModel, IPageLinkDescriptor pageLinkDescriptor) {
		super(labelModel, pageLinkDescriptor);
	}

	public void addSousMenu(NavigationMenuItem subMenuItem) {
		subMenuItems.add(subMenuItem);
	}
	
	public List<NavigationMenuItem> getSubMenuItems() {
		return subMenuItems;
	}
	
	@Override
	public void detach() {
		super.detach();
		
		if (subMenuItems != null) {
			for (NavigationMenuItem subMenuItem : subMenuItems) {
				subMenuItem.detach();
			}
		}
	}

}
