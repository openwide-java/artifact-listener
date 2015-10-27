package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.StringUtils;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.model.ExternalLinks;

public class AboutPage extends MainTemplate {

	private static final long serialVersionUID = -6767518941118385548L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(AboutPage.class)
				.build();
	}

	public AboutPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("about.pageTitle"), linkDescriptor()));
		
		add(new Label("pageTitle", new ResourceModel("about.pageTitle")));
		
		final Model<ExternalLinks> externalLinksModel = Model.of(ExternalLinks.get(configurer));
		
		add(new Label("content", new StringResourceModel("about.content", externalLinksModel)).setEscapeModelStrings(false));
		
		WebMarkupContainer gitHubProjectContainer = new WebMarkupContainer("gitHubProjectContainer") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(StringUtils.hasText(externalLinksModel.getObject().getGitHubProject()));
			}
		};
		add(gitHubProjectContainer);
		gitHubProjectContainer.add(new Label("gitHubProjectInsert", new StringResourceModel("about.insert.gitHubProject", externalLinksModel)).setEscapeModelStrings(false));
		
		add(new Label("hireInsert", new StringResourceModel("about.insert.hire", externalLinksModel)).setEscapeModelStrings(false));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return AboutPage.class;
	}
}
