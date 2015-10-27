package fr.openwide.maven.artifact.notifier.core.config.spring;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MavenArtifactNotifierCoreSolrConfig {
	
	@Bean
	public HttpSolrClient solrClient(@Value("${solr.url}") String solrUrl,
			@Value("${solr.pool.maxTotalConnections}") Integer maxTotalConnections) throws MalformedURLException {
		HttpSolrClient solrClient = new HttpSolrClient(solrUrl);
		
		solrClient.setParser(new TextXMLResponseParser());
		solrClient.setMaxTotalConnections(maxTotalConnections);
		solrClient.setDefaultMaxConnectionsPerHost(maxTotalConnections);
		
		return solrClient;
	}

	/**
	 * @see http://stackoverflow.com/questions/27781294/expected-mime-type-application-xml-but-got-text-html
	 */
	public static class TextXMLResponseParser extends XMLResponseParser {
		public TextXMLResponseParser() {
		}

		@Override
		public String getContentType() {
			return "text/xml; charset=UTF-8";
		}
	}
}
