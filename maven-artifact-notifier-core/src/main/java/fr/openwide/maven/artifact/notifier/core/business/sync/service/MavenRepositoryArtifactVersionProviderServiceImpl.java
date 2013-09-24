package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.solr.common.util.DateUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

//@Service("artifactVersionProviderService")
public class MavenRepositoryArtifactVersionProviderServiceImpl implements IArtifactVersionProviderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MavenRepositoryArtifactVersionProviderServiceImpl.class);
	
	private static final String LAST_MODIFIED_HEADER = "Last-Modified";
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException {
		String url = String.format(configurer.getArtifactRepositoryMetadataUrl(), groupId.replace(".", "/"), artifactId);

		Document doc;
		try {
			doc = Jsoup.connect(url).header(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE).get();
		} catch (IOException e) {
			throw new ServiceException("IOException: " + e.getMessage(), e);
		}
		return parseMavenMetadata(doc);
	}
	
	private List<ArtifactVersionBean> parseMavenMetadata(Document doc) {
		String groupId = doc.getElementsByTag("groupId").text();
		String artifactId = doc.getElementsByTag("artifactId").text();
		if (!StringUtils.hasText(groupId) || !StringUtils.hasText(artifactId)) {
			return Lists.newArrayListWithCapacity(0);
		}
		
		Elements versions = doc.getElementsByTag("version");
		List<ArtifactVersionBean> artifactList = Lists.newArrayList();
		for (Element version : versions) {
			
			ArtifactVersionBean artifactVersionBean = new ArtifactVersionBean();
			artifactVersionBean.setGroupId(groupId);
			artifactVersionBean.setArtifactId(artifactId);
			artifactVersionBean.setVersion(version.text());
			artifactVersionBean.setId(groupId + ":" + artifactId + ":" + version.text());
			
			// Gets and convert the last update date
			Long lastUpdateDate = retrieveLastUpdateDate(artifactVersionBean);
			if (lastUpdateDate == null) {
				continue;
			}
			artifactVersionBean.setTimestamp(lastUpdateDate);
			
			artifactList.add(artifactVersionBean);
		}
		
		return artifactList;
	}
	
	private Long retrieveLastUpdateDate(ArtifactVersionBean artifactVersionBean) {
		CloseableHttpClient client = null;
		
		try {
			client = HttpClientBuilder.create().setUserAgent(configurer.getUserAgent()).build();
			String url = String.format(configurer.getArtifactVersionRepositoryPomUrl(),
					artifactVersionBean.getGroupId().replace(".", "/"),
					artifactVersionBean.getArtifactId(),
					artifactVersionBean.getVersion());
			HttpHead httpHead = new HttpHead(url);
			httpHead.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			
			// The possible errors here are considered as secondary and will not be seen by the user
			// We may want to reconsider it
			HttpResponse response = client.execute(httpHead);
			Header lastUpdateDate = response.getFirstHeader(LAST_MODIFIED_HEADER);
			if (lastUpdateDate != null) {
				return DateUtil.parseDate(lastUpdateDate.getValue()).getTime();
			}
			LOGGER.error("An error occurred while retrieving the last update date for " + artifactVersionBean.getId());
		} catch (Exception e) {
			LOGGER.error("An error occurred while retrieving the last update date for " + artifactVersionBean.getId(), e);
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
}
