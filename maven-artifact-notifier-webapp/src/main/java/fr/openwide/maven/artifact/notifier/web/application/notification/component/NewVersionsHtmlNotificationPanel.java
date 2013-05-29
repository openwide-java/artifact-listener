package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class NewVersionsHtmlNotificationPanel extends AbstractRegisteredEmailHtmlNotificationPanel<List<ArtifactVersionNotification>> {

	private static final long serialVersionUID = 1676372998526497114L;
	
	private static final String ISO8601_PATTERN = "yyyy-MM-dd";
	
	private static final ThreadLocal<SimpleDateFormat> ISO8601_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		public SimpleDateFormat get() {
			return new SimpleDateFormat(ISO8601_PATTERN);
		};
	};
	
	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	public NewVersionsHtmlNotificationPanel(String id, IModel<List<ArtifactVersionNotification>> notificationsModel) {
		this(id, notificationsModel, Model.of((EmailAddress) null));
	}
	
	public NewVersionsHtmlNotificationPanel(String id, IModel<List<ArtifactVersionNotification>> notificationsModel,
			IModel<EmailAddress> emailAddressModel) {
		super(id, notificationsModel, emailAddressModel);
		
		// Title
		WebMarkupContainer titleContainer = new CustomWebMarkupContainer("titleContainer", STYLE_TITLE);
		add(titleContainer);
		
		titleContainer.add(new Label("date", ISO8601_DATE_FORMAT.get().format(new Date())));
		
		WebMarkupContainer contentContainer = new CustomWebMarkupContainer("contentContainer", STYLE_CONTENT);
		add(contentContainer);
		
		// Intro
		contentContainer.add(new CustomWebMarkupContainer("intro", STYLE_ALERT_INFO));
		
		// Table
		WebMarkupContainer newVersionsTable = new CustomWebMarkupContainer("newVersionsTable", STYLE_TABLE);
		contentContainer.add(newVersionsTable);
		
		// 	>	Headers
		WebMarkupContainer groupIdHeader = new CustomWebMarkupContainer("groupIdHeader", STYLE_TABLE_TH);
		groupIdHeader.add(new StyleAttributeAppender(STYLE_TABLE_TOP_LEFT_RADIUS));
		newVersionsTable.add(groupIdHeader);
		newVersionsTable.add(new CustomWebMarkupContainer("artifactIdHeader", STYLE_TABLE_TH));
		WebMarkupContainer versionHeader = new CustomWebMarkupContainer("versionHeader", STYLE_TABLE_TH);
		versionHeader.add(new StyleAttributeAppender(STYLE_TABLE_TOP_RIGHT_RADIUS));
		newVersionsTable.add(versionHeader);
		
		// 	>	Content
		newVersionsTable.add(new ListView<ArtifactVersionNotification>("newVersions", getModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<ArtifactVersionNotification> item) {
				ArtifactVersion version = item.getModelObject().getArtifactVersion();
				IModel<ArtifactVersionNotification> versionNotificationModel = item.getModel();
				
				Label groupId = new CustomLabel("groupId", BindingModel.of(versionNotificationModel,
						Binding.artifactVersionNotification().artifactVersion().artifact().group().groupId()), STYLE_TABLE_TD);
				if (item.getIndex() == NewVersionsHtmlNotificationPanel.this.getModelObject().size() - 1) {
					groupId.add(new StyleAttributeAppender(STYLE_TABLE_BOTTOM_LEFT_RADIUS));
				}
				item.add(groupId);
				
				WebMarkupContainer artifactIdContainer = new CustomWebMarkupContainer("artifactIdContainer", STYLE_TABLE_TD);
				item.add(artifactIdContainer);
				ExternalLink artifactIdLink = new ExternalLink("artifactIdLink",
						notificationUrlBuilderService.getArtifactDescriptionUrl(version.getArtifact()));
				artifactIdLink.add(new StyleAttributeAppender(STYLE_LINK));
				artifactIdLink.add(new Label("artifactIdLabel", BindingModel.of(versionNotificationModel,
						Binding.artifactVersionNotification().artifactVersion().artifact().artifactId())));
				artifactIdContainer.add(artifactIdLink);
				
				WebMarkupContainer versionContainer = new CustomWebMarkupContainer("versionContainer", STYLE_TABLE_TD);
				item.add(versionContainer);
				versionContainer.add(new CustomLabel("versionLabel", BindingModel.of(versionNotificationModel,
						Binding.artifactVersionNotification().artifactVersion().version()), STYLE_LABEL_INFO));
			}
		});
	}
}
