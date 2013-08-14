package fr.openwide.maven.artifact.notifier.web.application.console.importation.page;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.template.ConsoleImportTemplate;

public class ConsoleImportProjectPage extends ConsoleImportTemplate {
	
	private static final long serialVersionUID = 2614078069284835152L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleImportProjectPage.class);
	
	private IModel<List<FileUpload>> fileUploadsModel;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public ConsoleImportProjectPage(PageParameters parameters) {
		super(parameters);
		
		addHeadPageTitleKey("console.import.project");
		
		// File select form
		final FileUploadField fileSelect = new FileUploadField("fileSelectInput", this.fileUploadsModel);
		Form<Void> fileSelectForm = new Form<Void>("fileSelectForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				File pomFile = null;
				try {
					FileUpload fileUpload = fileSelect.getFileUpload();
					
					if (fileUpload == null) {
						getSession().error(getString("console.import.project.error.noFile"));
						return;
					}
					
					pomFile = File.createTempFile("uploaded-pom-", ".xml", configurer.getTmpDirectory());
					fileUpload.writeTo(pomFile);
					
					// TODO: import
					
				} catch (Exception e) {
					LOGGER.error("Unable to parse " + fileSelect.getFileUpload().getClientFileName() + " file", e);
					
					Session.get().error(getString("console.import.project.error"));
				} finally {
					FileUtils.deleteQuietly(pomFile);
				}
			}
		};
		fileSelectForm.add(fileSelect);
		
		fileSelectForm.add(new SubmitLink("fileSelectSubmit"));
		
		add(new Label("fileSelectLabel", new ResourceModel("console.import.project.file")));
		add(fileSelectForm);
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuItemPageClass() {
		return ConsoleImportProjectPage.class;
	}
}
