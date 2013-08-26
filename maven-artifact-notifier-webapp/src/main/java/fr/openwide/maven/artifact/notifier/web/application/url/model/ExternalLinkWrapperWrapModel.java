package fr.openwide.maven.artifact.notifier.web.application.url.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;
import fr.openwide.maven.artifact.notifier.core.business.url.service.IExternalLinkWrapperService;
import fr.openwide.maven.artifact.notifier.core.util.init.service.ProjectImportDataServiceImpl;

public class ExternalLinkWrapperWrapModel extends AbstractWrapModel<String> {

	private static final long serialVersionUID = 6365460591958814644L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectImportDataServiceImpl.class);

	@SpringBean
	private IExternalLinkWrapperService externalLinkWrapperService;
	
	private IModel<ExternalLinkWrapper> wrappedModel;
	
	public ExternalLinkWrapperWrapModel(IModel<ExternalLinkWrapper> wrappedModel) {
		Args.notNull(wrappedModel, "wrappedModel");
		this.wrappedModel = wrappedModel;

		Injector.get().inject(this);
	}
	
	@Override
	public String getObject() {
		ExternalLinkWrapper link = wrappedModel.getObject(); 
		return link != null ? link.getUrl() : null;
	}
	
	@Override
	public void setObject(String url) {
		if (url != null && wrappedModel.getObject() == null) {
			wrappedModel.setObject(new ExternalLinkWrapper(url));
		} else if (url != null) {
			wrappedModel.getObject().setUrl(url);
		} else {
			// NOTE: This is a workaround to the orphanRemoval bug on one-to-one relationship
			// https://hibernate.atlassian.net/browse/HHH-5559
			// https://hibernate.atlassian.net/browse/HHH-6484
			ExternalLinkWrapper link = wrappedModel.getObject();
			wrappedModel.setObject(null);
			if (link != null) {
				try {
					externalLinkWrapperService.delete(link);
				} catch (Exception e) {
					LOGGER.error("An error occurred while deleting the link " + link.getUrl());
				}
			}
		}
	}
	
	@Override
	public IModel<?> getWrappedModel() {
		return wrappedModel;
	}
}
