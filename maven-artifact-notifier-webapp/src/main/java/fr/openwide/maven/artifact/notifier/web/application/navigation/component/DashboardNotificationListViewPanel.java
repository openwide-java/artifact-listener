package fr.openwide.maven.artifact.notifier.web.application.navigation.component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.inject.internal.Sets;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactVersionTagPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;

public class DashboardNotificationListViewPanel extends GenericPanel<Map<Date, Set<ArtifactVersionNotification>>>{

	private static final long serialVersionUID = 5554256047779428515L;
	
	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	public DashboardNotificationListViewPanel(String id, IModel<? extends Map<Date, Set<ArtifactVersionNotification>>> mapModel) {
		super(id, mapModel);
		setOutputMarkupId(true);
		
		IModel<List<Date>> wrapperModel = CollectionToListWrapperModel.of(getModelObject().keySet());
		add(new ListView<Date>("dayList", wrapperModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Date> item) {
				Date day = item.getModelObject();
				Set<ArtifactVersionNotification> notifSet = DashboardNotificationListViewPanel.this.getModelObject().get(day);
				
				Label dayLabel = new DateLabel("day", Model.of(day), DatePattern.SHORT_DATE);
				if (notifSet == null) {
					dayLabel = new Label("day");
					notifSet = Sets.newHashSet();
				}
				
				// Day label
				item.add(dayLabel);
				item.add(new ListView<ArtifactVersionNotification>("notificationList", CollectionToListWrapperModel.of(notifSet)) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<ArtifactVersionNotification> item) {
						final IModel<ArtifactVersionNotification> notificationModel = item.getModel();
						final IModel<Artifact> artifactModel = BindingModel.of(notificationModel,
								Binding.artifactVersionNotification().artifactVersion().artifact());
						
						// Artifact link
						Link<Void> artifactLink = ArtifactDescriptionPage
								.linkDescriptor(artifactModel)
								.link("artifactLink");
						artifactLink.add(new Label("id", BindingModel.of(artifactModel, Binding.artifact().artifactKey().key())));
						item.add(artifactLink);
						
						// Version link
						item.add(new ArtifactVersionTagPanel("version", BindingModel.of(notificationModel,  Binding.artifactVersionNotification().artifactVersion().version())));
						item.add(new ExternalLink("versionLink", new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;
							
							@Override
							protected String load() {
								Artifact artifact = artifactModel.getObject();
								ArtifactVersionNotification notification = notificationModel.getObject();
								return mavenCentralSearchUrlService.getVersionUrl(artifact.getGroup().getGroupId(),
										artifact.getArtifactId(), notification.getArtifactVersion().getVersion());
							}
							
						}));
						
					}
				});
			}
		});
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;
			
			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(getModelObject().size() == 0);
			}
		});
	}
}
