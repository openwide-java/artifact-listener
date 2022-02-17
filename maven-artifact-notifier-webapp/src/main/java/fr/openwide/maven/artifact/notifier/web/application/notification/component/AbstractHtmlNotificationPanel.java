package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationUrlBuilderService;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public abstract class AbstractHtmlNotificationPanel<T> extends GenericPanel<T> {

	private static final long serialVersionUID = -3576134833190785445L;

	private static final String STYLE_FONT = "font-size: 12px; line-height: 18px; font-family: 'Open Sans', 'Helvetica Neue', Helvetica, Arial, sans-serif; ";

	protected static final String STYLE_ROOT = STYLE_FONT + "background: #FFFFFF; color: #333333;";
	protected static final String STYLE_MAIN_CONTAINER = "width: 550px; margin: 20px auto; border: 1px solid #CCCCCC; border-radius: 5px;";
	protected static final String STYLE_MAIN_TITLE = "margin: 0; padding: 15px; background: #3498DB; color: #FFFFFF; border-bottom: 1px solid #2980B9; border-radius: 5px 5px 0 0;";

	protected static final String STYLE_LINK = "color: #3498DB; text-decoration: none;";
	protected static final String STYLE_LINK_FOOTER = "color: #4EB2F5; text-decoration: none;";

	protected static final String STYLE_TITLE = "margin: 0; padding: 10px 15px; background: #EEEEEE; color: #D35400; border-bottom: 1px solid #CCCCCC;";
	protected static final String STYLE_CONTENT = "padding: 15px; background: #FFFFFF; min-height: 120px";
	protected static final String STYLE_UNSUBSCRIBE = "padding: 5px 15px; background: #EEE; border-top: 1px solid #DDDDDD; font-size: 11px; line-height: 16px;";
	protected static final String STYLE_FOOTER = "padding: 10px 15px; background: #34495E; color: #EEEEEE; border-top: 1px solid #2C3E50; border-radius: 0 0 5px 5px; text-align: right;";

	protected static final String STYLE_TABLE = "width:100%; border: 1px solid #DDDDDD; border-top: none; border-left: none; border-radius: 4px; border-spacing: 0; border-collapse: separate;";
	protected static final String STYLE_TABLE_TH = STYLE_FONT + "border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD; padding: 10px 8px; text-align: left; vertical-align: bottom; color: #555555; font-weight: bold;";
	protected static final String STYLE_TABLE_TD = STYLE_FONT + "border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD; padding: 10px 8px; text-align: left;";
	protected static final String STYLE_TABLE_TOP_LEFT_RADIUS = "border-top-left-radius: 4px;";
	protected static final String STYLE_TABLE_TOP_RIGHT_RADIUS = "border-top-right-radius: 4px;";
	protected static final String STYLE_TABLE_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius: 4px;";

	protected static final String STYLE_ALERT_INFO = "background: #D9EDF7; border: 1px solid #BCE8F1; color: #3A87AD; border-radius: 4px; padding: 8px 14px";
	protected static final String STYLE_ALERT_DANGER = "background: #F7D9D9; border: 1px solid #F1BCBC; color: #AD3A3A; border-radius: 4px; padding: 8px 14px; font-size: 18px; line-height: 24px";

	@SpringBean
	protected INotificationUrlBuilderService notificationUrlBuilderService;

	public AbstractHtmlNotificationPanel(String id, IModel<T> model) {
		super(id, model);

		WebMarkupContainer root = new TransparentWebMarkupContainer("root");
		root.add(new StyleAttributeAppender(STYLE_ROOT));
		add(root);

		root.add(new CustomWebMarkupContainer("unmaintained", STYLE_ALERT_DANGER));

		WebMarkupContainer mainContainer = new TransparentWebMarkupContainer("mainContainer");
		mainContainer.add(new StyleAttributeAppender(STYLE_MAIN_CONTAINER));
		root.add(mainContainer);

		mainContainer.add(new CustomWebMarkupContainer("mainTitle", STYLE_MAIN_TITLE));

		WebMarkupContainer footer = new CustomWebMarkupContainer("footer", STYLE_FOOTER);
		mainContainer.add(footer);

		ExternalLink aboutLink = new ExternalLink("aboutLink", notificationUrlBuilderService.getAboutUrl());
		aboutLink.add(new StyleAttributeAppender(STYLE_LINK_FOOTER));
		footer.add(aboutLink);
	}

	protected String getHomeUrl() {
		return notificationUrlBuilderService.getHomeUrl();
	}
}
