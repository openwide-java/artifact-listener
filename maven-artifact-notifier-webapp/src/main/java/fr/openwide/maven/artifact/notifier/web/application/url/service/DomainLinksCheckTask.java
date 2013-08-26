package fr.openwide.maven.artifact.notifier.web.application.url.service;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import fr.openwide.core.jpa.util.EntityManagerUtils;
import fr.openwide.core.spring.util.SpringBeanUtils;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;
import fr.openwide.maven.artifact.notifier.core.business.url.service.IExternalLinkWrapperService;
import fr.openwide.maven.artifact.notifier.core.business.url.service.ILinkCheckerService;

public class DomainLinksCheckTask implements Callable<Void> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DomainLinksCheckTask.class);

	private static final int SESSION_LIMIT = 1000;
	
	@Autowired
	private ILinkCheckerService linkCheckerService;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private EntityManagerUtils entityManagerUtils;
	
	@Autowired
	private IExternalLinkWrapperService externalLinkWrapperService;
	
	private Collection<Long> ids;
	
	public DomainLinksCheckTask(ApplicationContext applicationContext, Collection<Long> ids) {
		this.ids = ids;
		SpringBeanUtils.autowireBean(applicationContext, this);
	}

	@Override
	public Void call() throws Exception {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		
		return template.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				Session session = entityManagerUtils.getEntityManager().unwrap(Session.class);
				session.setFlushMode(FlushMode.COMMIT);
				
				int count = 0;
				for (Long id : ids) {
					// We flush the session to avoid a memory overhead if there is a huge amount of links within the same domain
					if (count >= SESSION_LIMIT) {
						session.flush();
						count = 0;
					}
					try {
						ExternalLinkWrapper link = externalLinkWrapperService.getById(id);
						linkCheckerService.checkLink(link);
						++count;
					} catch (Exception e) {
						LOGGER.error("An error occurred while checking links", e);
					}
				}
				
				return null;
			}
		});
	}
}
