package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.markup.html.basic.CoreLabel;
import fr.openwide.core.wicket.more.markup.html.basic.EnclosureContainer;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.AbstractArtifactPomSearchDataProvider;

public class ArtifactPomSearchResultsPanel extends Panel {

	private static final long serialVersionUID = 2168203516395191437L;
	
	private List<IPageable> dataViews = Lists.newArrayList();
	
	private IModel<List<ArtifactBean>> artifactsModel = new ListModel<ArtifactBean>();
	
	public ArtifactPomSearchResultsPanel(String id, final IModel<PomBean> pomBeanModel) {
		super(id);

		artifactsModel.setObject(Lists.<ArtifactBean>newArrayList());
		
		CoreLabel pomGroupId = new CoreLabel("pomGroupId", BindingModel.of(pomBeanModel, Binding.pomBean().groupId())).hideIfEmpty();
		CoreLabel pomArtifactId = new CoreLabel("pomArtifactId", BindingModel.of(pomBeanModel, Binding.pomBean().artifactId())).hideIfEmpty();
		EnclosureContainer pomIdEnclosure = new EnclosureContainer("pomIdEnclosure").components(pomGroupId, pomArtifactId);
		pomIdEnclosure.add(pomGroupId, pomArtifactId);
		add(pomIdEnclosure);
		
		addAlertContainer(pomBeanModel);
		
		addDataView("dependencies", "dependencies-data-view", new AbstractArtifactPomSearchDataProvider(pomBeanModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactBean> loadData() {
				return getPomBean().getDependencies();
			}
		});
		
		addDataView("dependencyMgmt", "dependency-mgmt-data-view", new AbstractArtifactPomSearchDataProvider(pomBeanModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactBean> loadData() {
				return getPomBean().getDependencyManagement();
			}
		});
		
		addDataView("plugins", "plugins-data-view", new AbstractArtifactPomSearchDataProvider(pomBeanModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactBean> loadData() {
				return getPomBean().getPlugins();
			}
		});
		
		addDataView("pluginMgmt", "plugin-mgmt-data-view", new AbstractArtifactPomSearchDataProvider(pomBeanModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactBean> loadData() {
				return getPomBean().getPluginManagement();
			}
		});
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			protected void onConfigure() {
				boolean result = true;
				for (IPageable pageable : dataViews) {
					DataView<?> dataView = (DataView<?>) pageable;
					result = result && dataView.getDataProvider().size() == 0;
				}
				setVisible(result);
			}
		});
	}
	
	private void addAlertContainer(final IModel<PomBean> pomBeanModel) {
		WebMarkupContainer alertContainer = new WebMarkupContainer("alertContainer") {
			private static final long serialVersionUID = 8321541131664241535L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(pomBeanModel.getObject() != null && pomBeanModel.getObject().getInvalidArtifacts().size() > 0);
			}
		};
		IModel<List<ArtifactBean>> invalidArtifactsModel = new LoadableDetachableModel<List<ArtifactBean>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactBean> load() {
				if (pomBeanModel.getObject() != null) {
					return Lists.newArrayList(pomBeanModel.getObject().getInvalidArtifacts());
				}
				return Lists.newArrayList();
			}
		};
		alertContainer.add(new ListView<ArtifactBean>("invalidArtifacts", invalidArtifactsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArtifactBean> item) {
				ArtifactBean artifactBean = item.getModelObject();
				item.add(new Label("artifactUniqueId", String.format("%s:%s", artifactBean.getGroupId(), artifactBean.getArtifactId())));
			}
		});
		add(alertContainer);
	}
	
	private void addDataView(String id, final String className, IDataProvider<ArtifactBean> dataProvider) {
		// Data view
		final DataView<ArtifactBean> artifactDataView = new ArtifactBeanDataView("dataView", dataProvider);
		dataViews.add(artifactDataView);
		
		// Fragment
		Fragment fragment = new Fragment(id, "dataViewFragment", this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(artifactDataView.getDataProvider().size() != 0);
			}
		};
		fragment.add(new Label("title", new ResourceModel("artifact.follow.pom." + id)),
					artifactDataView);
		add(fragment);
	}

	public List<IPageable> getDataViews() {
		return dataViews;
	}
	
	@Override
	protected void detachModel() {
		if (artifactsModel != null) {
			artifactsModel.detach();
		}
		super.detachModel();
	}
}
