package fr.openwide.maven.artifact.notifier.web.application.url.service;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkStatus;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;
import fr.openwide.maven.artifact.notifier.core.business.url.service.IExternalLinkWrapperService;
import fr.openwide.maven.artifact.notifier.core.business.url.service.ILinkCheckerService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Service("linkCheckerService")
public class LinkCheckerServiceImpl implements ILinkCheckerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkCheckerServiceImpl.class);

	@Autowired
	private IExternalLinkWrapperService externalLinkWrapperService;
	
	@Autowired
	private ConfigurableApplicationContext applicationContext;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	// Public methods
	
	@Override
	public void checkAllLinks() throws ServiceException, SecurityServiceException {
		List<ExternalLinkWrapper> links = externalLinkWrapperService.list();
		
		runTasksInParallel(createTasksByDomain(links), 10, TimeUnit.HOURS);
	}
	
	@Override
	public void checkLink(final ExternalLinkWrapper link) throws ServiceException, SecurityServiceException {
		if (!ExternalLinkStatus.DEAD_LINK.equals(link.getStatus())) {
			StatusLine status = sendRequest(link, true);
			if (status != null && status.getStatusCode() == HttpStatus.SC_METHOD_NOT_ALLOWED) {
				status = sendRequest(link, false);
			}
			
			link.setLastCheckDate(new Date());
			if (status != null && status.getStatusCode() == HttpStatus.SC_OK) {
				onSuccessfulCheck(link);
			} else {
				onCheckFailure(link, status);
			}
			externalLinkWrapperService.update(link);
		}
	}
	
	// Private methods
	
	private Collection<Callable<Void>> createTasksByDomain(List<ExternalLinkWrapper> links) throws ServiceException, SecurityServiceException {
		Collections.sort(links);
		Collection<Callable<Void>> tasks = Lists.newArrayList();
		
		int fromIndex = 0;
		String currentDomain = null;
		for (int i = 0; i < links.size(); ++i) {
			ExternalLinkWrapper link = links.get(i);
			String domain = getDomainName(link.getUrl());
			if (domain == null) { // URL has an invalid syntax: the check will fail
				link.setStatus(ExternalLinkStatus.DEAD_LINK);
				externalLinkWrapperService.update(link);
				continue;
			}
			
			if (currentDomain == null || !currentDomain.equals(domain)) {
				currentDomain = domain;
				
				if (fromIndex != i) {
					Collection<Long> idList = toIdList(links.subList(fromIndex, i));
					fromIndex = i;
					
					tasks.add(new DomainLinksCheckTask(applicationContext, idList));
				}
			}
		}
		if (fromIndex != links.size()) {
			Collection<Long> idList = toIdList(links.subList(fromIndex, links.size()));
			
			tasks.add(new DomainLinksCheckTask(applicationContext, idList));
		}
		
		return tasks;
	}
	
	private String getDomainName(String url) {
		try {
			URI uri = new URI(url);
			return uri.getHost();
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	private void onSuccessfulCheck(final ExternalLinkWrapper link) {
		link.setConsecutiveFailures(0);
		link.setStatus(ExternalLinkStatus.ONLINE);
		link.setLastStatusCode(null);
	}
	
	private void onCheckFailure(final ExternalLinkWrapper link, final StatusLine status) {
		link.setConsecutiveFailures(link.getConsecutiveFailures() + 1);
		if (status != null) {
			link.setLastStatusCode(status.getStatusCode());
		}
		
		if (link.getConsecutiveFailures() >= configurer.getDeadLinkRequiredConsecutiveFailures()) {
			link.setStatus(ExternalLinkStatus.DEAD_LINK);
		} else {
			link.setStatus(ExternalLinkStatus.OFFLINE);
		}
	}
	
	private StatusLine sendRequest(final ExternalLinkWrapper link, boolean httpHead) {
		CloseableHttpClient client = null;
		
		try {
			client = HttpClientBuilder.create().setUserAgent(configurer.getUserAgent()).build();
			HttpUriRequest method = (httpHead ? new HttpHead(link.getUrl()) : new HttpGet(link.getUrl()));
			method.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			
				HttpResponse response = client.execute(method);
				return response.getStatusLine();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder()
				.append("An error occurred while performing a ")
				.append(httpHead ? "HEAD" : "GET")
				.append(" request on ")
				.append(link.getUrl());
			LOGGER.error(sb.toString(), e);
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					LOGGER.error("Unable to close the HTTP client", e);
				}
			}
		}
		return null;
	}
	
	private static int getThreadPoolSize() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	private static void runTasksInParallel(Collection<? extends Callable<Void>> tasks, long timeout, TimeUnit timeoutUnit) throws ServiceException {
		final int threadPoolSize = getThreadPoolSize();
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(
				threadPoolSize, threadPoolSize,
				100, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		
		executor.prestartAllCoreThreads();
		
		try {
			List<Future<Void>> futures = executor.invokeAll(tasks, timeout, timeoutUnit);
			for (Future<Void> future : futures) {
				future.get(); // Check that no error has occurred
			}
		} catch (Exception e) {
			throw new ServiceException("Interrupted request", e);
		} finally {
			try {
				executor.shutdown();
			} catch (Exception e) {
				LOGGER.warn("An error occurred while shutting down threads", e);
			}
		}
	}
	
	private static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, E>>
			Collection<K> toIdList(Collection<E> entities) {
		return Collections2.transform(entities, new Function<E, K>() {

			@Override
			@Nullable
			public K apply(@Nullable E entity) {
				return entity.getId();
			}
		});
	}
}
