package fr.openwide.maven.artifact.notifier.web.application.console.importation.page;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.commons.util.mime.MediaType;
import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.util.init.service.IProjectImportDataService;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.page.resource.ProjectImportModelResourceReference;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.template.ConsoleImportTemplate;

public class ConsoleImportProjectPage extends ConsoleImportTemplate {
	
	private static final long serialVersionUID = 2614078069284835152L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleImportProjectPage.class);
	
	public static final String EXCEL_CONTENT_TYPE = MediaType.APPLICATION_MS_EXCEL.mime();
	
	private IModel<List<FileUpload>> fileUploadsModel;
	
	@SpringBean
	private IProjectImportDataService projectImportDataService;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public ConsoleImportProjectPage(PageParameters parameters) {
		super(parameters);
		
		addHeadPageTitleKey("console.import.project");
		
		// File select form
		final FileUploadField fileSelect = new FileUploadField("fileSelectInput", this.fileUploadsModel);
		fileSelect.setLabel(new ResourceModel("console.import.project.file"));
		fileSelect.add(AttributeModifier.replace("accept", getAcceptAttribute()));
		
		Form<Void> form = new Form<Void>("fileSelectForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				File file = null;
				try {
					FileUpload fileUpload = fileSelect.getFileUpload();
					
					if (fileUpload == null) {
						getSession().error(getString("console.import.project.error.noFile"));
						return;
					}
					
					file = File.createTempFile("uploaded-", ".xls", configurer.getTmpDirectory());
					fileUpload.writeTo(file);
					
					projectImportDataService.importProjects(file);
					
					Session.get().success(getString("console.import.project.success"));
				} catch (Exception e) {
					LOGGER.error("Unable to parse " + fileSelect.getFileUpload().getClientFileName() + " file", e);
					
					Session.get().error(getString("console.import.project.error"));
				} finally {
					FileUtils.deleteQuietly(file);
				}
			}
		};
		form.add(fileSelect);
		
		// Example excel file download link
		form.add(new ResourceLink<Void>("downloadTemplate", ProjectImportModelResourceReference.get()));
		
		form.add(new SubmitLink("importButton"));
		
		add(form);
	}
	
	protected IModel<String> getAcceptAttribute() {
		return Model.of(MediaType.APPLICATION_OPENXML_EXCEL.mime() + "," + MediaType.APPLICATION_MS_EXCEL.mime());
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuItemPageClass() {
		return ConsoleImportProjectPage.class;
	}
}
