package fr.openwide.maven.artifact.notifier.core.business.url.dao;

import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

@Service("externalLinkWrapperDao")
public class ExternalLinkWrapperDaoImpl extends GenericEntityDaoImpl<Long, ExternalLinkWrapper> implements IExternalLinkWrapperDao {

}