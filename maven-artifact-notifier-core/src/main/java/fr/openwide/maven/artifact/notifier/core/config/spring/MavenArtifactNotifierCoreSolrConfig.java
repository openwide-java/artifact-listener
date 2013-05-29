package fr.openwide.maven.artifact.notifier.core.config.spring;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MavenArtifactNotifierCoreSolrConfig {
	
	@Bean
	public HttpSolrServer solrServer(@Value("${solr.url}") String solrUrl,
			@Value("${solr.pool.maxTotalConnections}") Integer maxTotalConnections) throws MalformedURLException {
		HttpSolrServer solrServer = new HttpSolrServer(solrUrl);
		
		solrServer.setMaxTotalConnections(maxTotalConnections);
		solrServer.setDefaultMaxConnectionsPerHost(maxTotalConnections);
		solrServer.setParser(new XMLResponseParser());
		
		return solrServer;
	}
}
