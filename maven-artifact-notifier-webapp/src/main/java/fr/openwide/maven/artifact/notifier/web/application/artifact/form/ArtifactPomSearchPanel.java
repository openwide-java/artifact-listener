package fr.openwide.maven.artifact.notifier.web.application.artifact.form;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.spring.config.CoreConfigurer;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.autosize.AutosizeBehavior;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchApiService;

public class ArtifactPomSearchPanel extends Panel {

	private static final long serialVersionUID = 6273289257800090393L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactPomSearchPanel.class);
	
	@SpringBean
	private IMavenCentralSearchApiService mavenCentralSearchApiService;
	
	@SpringBean
	private CoreConfigurer configurer;
	
	private IModel<List<FileUpload>> fileUploadsModel;
	
	private IModel<String> pomContentModel;
	
	private IModel<PomBean> pomBeanModel;
	
	public ArtifactPomSearchPanel(String id, final List<IPageable> pageableList, final IModel<PomBean> pomBeanModel) {
		super(id);
		
		this.pomBeanModel = pomBeanModel;
		this.fileUploadsModel = new ListModel<FileUpload>();
		this.pomContentModel = Model.of();
		
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
						getSession().error(getString("artifact.follow.pom.error.noFile"));
						return;
					}
					
					pomFile = File.createTempFile("uploaded-pom-", ".xml", configurer.getTmpDirectory());
					fileUpload.writeTo(pomFile);
					
					PomBean pomBean = mavenCentralSearchApiService.searchFromPom(pomFile);
					ArtifactPomSearchPanel.this.pomBeanModel.setObject(pomBean);
					ArtifactPomSearchPanel.this.pomContentModel.setObject(null);
				} catch (Exception e) {
					LOGGER.error("Unable to parse " + fileSelect.getFileUpload().getClientFileName() + " file", e);
					
					Session.get().error(getString("artifact.follow.pom.error"));
				} finally {
					FileUtils.deleteQuietly(pomFile);
				}
				
				// Lors de la soumission d'un formulaire de recherche, on retourne sur la première page
				for (IPageable pageable : pageableList) {
					pageable.setCurrentPage(0);
				}
			}
		};
		fileSelectForm.add(fileSelect);
		
		fileSelectForm.add(new SubmitLink("fileSelectSubmit"));
		
		add(new Label("fileSelectLabel", new ResourceModel("artifact.follow.pom.search.file")));
		add(fileSelectForm);
		
		// File content form
		Form<Void> fileContentForm = new Form<Void>("fileContentForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				if (ArtifactPomSearchPanel.this.pomContentModel.getObject() == null) {
					getSession().error(getString("artifact.follow.pom.error.noContent"));
					return;
				}
				
				try {
					PomBean pomBean = mavenCentralSearchApiService.searchFromPom(ArtifactPomSearchPanel.this.pomContentModel.getObject());
					ArtifactPomSearchPanel.this.pomBeanModel.setObject(pomBean);
				} catch (Exception e) {
					LOGGER.error("Unable to parse the pom content from the provided string", e);
					
					Session.get().error(getString("artifact.follow.pom.error"));
				}
				
				// Lors de la soumission d'un formulaire de recherche, on retourne sur la première page
				for (IPageable pageable : pageableList) {
					pageable.setCurrentPage(0);
				}
			}
		};
		
		final TextArea<String> fileContent = new TextArea<String>("fileContentInput", this.pomContentModel);
		
		fileContent.setOutputMarkupId(true);
		fileContent.add(new AutosizeBehavior());
		fileContentForm.add(fileContent);
		
		fileContentForm.add(new SubmitLink("fileContentSubmit"));
		
		add(new Label("fileContentLabel", new ResourceModel("artifact.follow.pom.search.content")));
		add(fileContentForm);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (fileUploadsModel != null) {
			this.fileUploadsModel.detach();
		}
		if (pomContentModel != null) {
			this.pomContentModel.detach();
		}
		if (pomBeanModel != null) {
			this.pomBeanModel.detach();
		}
	}
}
