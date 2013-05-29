package fr.openwide.maven.artifact.notifier.web.application.console.page;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.internal.Lists;

import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.web.application.console.template.ConsoleNotificationTemplate;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmEmailHtmlNotificationDemoPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmRegistrationHtmlNotificationDemoPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.DeleteEmailHtmlNotificationDemoPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.NewVersionsAdditionalEmailHtmlNotificationDemoPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.NewVersionsHtmlNotificationDemoPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ResetPasswordHtmlNotificationDemoPage;

public class ConsoleNotificationIndexPage extends ConsoleNotificationTemplate {

	private static final long serialVersionUID = -6767518941118385548L;
	
	public static final String DEFAULT_USERNAME = "admin@artifact-listener.org";
	
	public ConsoleNotificationIndexPage(PageParameters parameters) {
		super(parameters);
		
		addHeadPageTitleKey("console.notifications");
		
		add(new ListView<PageProvider>("notifications", getNotificationPages()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void populateItem(ListItem<PageProvider> item) {
				Class<? extends Page> pageClass = (Class<? extends Page>) item.getModelObject().getPageClass();
				Link<Void> link = new BookmarkablePageLink<Void>("link", pageClass);
				link.add(new Label("label", new ResourceModel("console.notifications." + pageClass.getSimpleName(), pageClass.getSimpleName())));
				item.add(link);
			}
		});
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public boolean isVisible() {
				return getNotificationPages().isEmpty();
			}
		});
	}
	
	private List<PageProvider> getNotificationPages() {
		return Lists.newArrayList(
				new PageProvider(ConfirmRegistrationHtmlNotificationDemoPage.class),
				new PageProvider(ResetPasswordHtmlNotificationDemoPage.class),
				new PageProvider(ConfirmEmailHtmlNotificationDemoPage.class),
				new PageProvider(DeleteEmailHtmlNotificationDemoPage.class),
				new PageProvider(NewVersionsHtmlNotificationDemoPage.class),
				new PageProvider(NewVersionsAdditionalEmailHtmlNotificationDemoPage.class)
		);
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuItemPageClass() {
		return ConsoleNotificationIndexPage.class;
	}
}
