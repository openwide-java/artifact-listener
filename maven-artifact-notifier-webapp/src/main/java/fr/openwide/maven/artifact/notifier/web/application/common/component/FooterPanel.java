package fr.openwide.maven.artifact.notifier.web.application.common.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.StringUtils;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.emailobfuscator.ObfuscatedEmailLink;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.navigation.model.ExternalLinks;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.AboutPage;

public class FooterPanel extends Panel {
	
	private static final long serialVersionUID = 8349879446477301375L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public FooterPanel(String id) {
		super(id);
		add(new Label("openWide", new StringResourceModel("footer.links.openWide", Model.of(ExternalLinks.get(configurer)), (Object) null)).setEscapeModelStrings(false));
		add(new BookmarkablePageLink<Void>("aboutLink", AboutPage.class));
		WebMarkupContainer gitHubProjectContainer = new WebMarkupContainer("gitHubProjectContainer") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(StringUtils.hasText(ExternalLinks.get(configurer).getGitHubProject()));
			}
		};
		add(gitHubProjectContainer);
		gitHubProjectContainer.add(new Label("gitHubProject", new StringResourceModel("footer.links.gitHubProject", Model.of(ExternalLinks.get(configurer)), (Object) null)).setEscapeModelStrings(false));
		
		add(new Label("twitter", new StringResourceModel("footer.links.twitter", Model.of(ExternalLinks.get(configurer)), (Object) null)).setEscapeModelStrings(false));
		
		add(new ObfuscatedEmailLink("contactUsLink", Model.of(configurer.getLinkContactUs())));
	}
	
}
