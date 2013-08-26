package fr.openwide.maven.artifact.notifier.core.business.url.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.url.dao.IExternalLinkWrapperDao;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

@Service("externalLinkWrapperService")
public class ExternalLinkWrapperServiceImpl extends GenericEntityServiceImpl<Long, ExternalLinkWrapper> implements IExternalLinkWrapperService {

	@Autowired
	public ExternalLinkWrapperServiceImpl(IExternalLinkWrapperDao externalLinkWrapperDao) {
		super(externalLinkWrapperDao);
	}
}