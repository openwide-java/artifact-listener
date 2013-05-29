package fr.openwide.maven.artifact.notifier.core.business.search.service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.CoreAdminParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.spring.util.lucene.search.LuceneUtils;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;
import fr.openwide.maven.artifact.notifier.core.business.search.util.MavenCentralSearchApiConstants;

@Service("mavenCentralSearchService")
public class MavenCentralSearchApiServiceImpl implements IMavenCentralSearchApiService {

	private static final Pattern VALID_ARTIFACT_ID_PART_PATTERN = Pattern.compile("^[-\\w]+(\\.[-\\w]+)*$");
	
	private static final int DEFAULT_MAX_ROWS = 100;
	
	private static final int MAX_CLAUSES = 20;
	
	@Autowired
	private HttpSolrServer solrServer;
	
	@Autowired
	private IPomParserService pomParserService;

	@Override
	public List<ArtifactBean> getArtifacts(String global, String groupId, String artifactId, int offset, int maxRows) throws ServiceException {
		String query = getSearchArtifactsQuery(global, groupId, artifactId);
		if (!StringUtils.hasText(query)) {
			return Lists.newArrayList();
		}
		SolrQuery solrQuery = new SolrQuery(query);

		QueryResponse response = query(solrQuery, offset, maxRows);
		return response.getBeans(ArtifactBean.class);
	}
	
	@Override
	public long countArtifacts(String global, String groupId, String artifactId) throws ServiceException {
		String query = getSearchArtifactsQuery(global, groupId, artifactId);
		if (!StringUtils.hasText(query)) {
			return 0;
		}
		SolrQuery solrQuery = new SolrQuery(query);
		
		QueryResponse response = query(solrQuery, 0, 0);
		return response.getResults().getNumFound();
	}
	
	@Override
	public long countArtifacts(String artifactId) throws ServiceException {
		return countArtifacts(null, null, artifactId);
	}
	
	private String getSearchArtifactsQuery(String global, String groupId, String artifactId) {
		StringBuilder querySb = new StringBuilder();
		
		if (StringUtils.hasText(global)) {
			querySb.append(global);
			querySb.append(" ");
		}
		
		if (StringUtils.hasText(groupId)) {
			querySb.append(LuceneUtils.queryToString(new TermQuery(new Term(MavenCentralSearchApiConstants.GROUP_FIELD, groupId))));
			querySb.append(" ");
		}
		
		if (StringUtils.hasText(artifactId)) {
			querySb.append(LuceneUtils.queryToString(new TermQuery(new Term(MavenCentralSearchApiConstants.ARTIFACT_FIELD, artifactId))));
			querySb.append(" ");
		}
		return querySb.toString();
	}
	
	@Override
	public List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException {
		return getArtifactVersions(groupId, artifactId, 0, DEFAULT_MAX_ROWS);
	}

	private List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId, int offset, int maxRows) throws ServiceException {
		if (!StringUtils.hasText(groupId) || ! StringUtils.hasText(artifactId)) {
			return Lists.newArrayListWithCapacity(0);
		}
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(new TermQuery(new Term(MavenCentralSearchApiConstants.GROUP_FIELD, groupId)), Occur.MUST);
		bq.add(new TermQuery(new Term(MavenCentralSearchApiConstants.ARTIFACT_FIELD, artifactId)), Occur.MUST);
		
		SolrQuery solrQuery = new SolrQuery(LuceneUtils.queryToString(bq));
		solrQuery.set(CoreAdminParams.CORE, MavenCentralSearchApiConstants.CORE_GAV);

		QueryResponse response = query(solrQuery, offset, maxRows);
		return response.getBeans(ArtifactVersionBean.class);
	}
	
	@Override
	public PomBean searchFromPom(String xml) throws ServiceException {
		PomBean pomBean = pomParserService.parse(xml);
		
		return filterPomBean(pomBean);
	}

	@Override
	public PomBean searchFromPom(File file) throws ServiceException {
		PomBean pomBean = pomParserService.parse(file);
		
		return filterPomBean(pomBean);
	}
	
	private QueryResponse query(SolrQuery query, int offset, int maxRows) throws ServiceException {
		query.set(CommonParams.START, offset)
			.set(CommonParams.ROWS, maxRows)
			.set(CommonParams.WT, solrServer.getParser().getWriterType());
		
		try {
			return solrServer.query(query);
		} catch (SolrServerException e) {
			throw new ServiceException("SolrServer: " + e.getMessage(), e);
		}
	}
	
	private boolean isValidId(String id) {
		return StringUtils.hasText(id) && VALID_ARTIFACT_ID_PART_PATTERN.matcher(id).matches();
	}

	private PomBean filterPomBean(PomBean pomBean) throws ServiceException {
		PomBean artifactsOnMavenCentral = new PomBean(pomBean);
		
		doFilterFromList(pomBean.getDependencies(), artifactsOnMavenCentral.getDependencies(), artifactsOnMavenCentral.getInvalidArtifacts());
		doFilterFromList(pomBean.getDependencyManagement(), artifactsOnMavenCentral.getDependencyManagement(), artifactsOnMavenCentral.getInvalidArtifacts());
		doFilterFromList(pomBean.getPlugins(), artifactsOnMavenCentral.getPlugins(), artifactsOnMavenCentral.getInvalidArtifacts());
		doFilterFromList(pomBean.getPluginManagement(), artifactsOnMavenCentral.getPluginManagement(), artifactsOnMavenCentral.getInvalidArtifacts());
		
		return artifactsOnMavenCentral;
	}
	
	private void doFilterFromList(List<ArtifactBean> originalArtifactList, List<ArtifactBean> validArtifactsList,
			Set<ArtifactBean> invalidArtifactsList) throws ServiceException {
		validArtifactsList.addAll(getValidArtifacts(originalArtifactList));
		
		List<ArtifactBean> localInvalidArtifactsList = Lists.newArrayList(originalArtifactList);
		localInvalidArtifactsList.removeAll(validArtifactsList);
		
		invalidArtifactsList.addAll(localInvalidArtifactsList);
	}
	
	private List<ArtifactBean> getValidArtifacts(List<ArtifactBean> artifactList) throws ServiceException {
		if (artifactList.isEmpty()) {
			return artifactList;
		}
		
		List<ArtifactBean> validArtifacts = Lists.newArrayList();
		List<List<ArtifactBean>> artifactListPartitions = Lists.partition(artifactList, MAX_CLAUSES);
		
		for (List<ArtifactBean> partialArtifactList : artifactListPartitions) {
			BooleanQuery artifactSearchQuery = new BooleanQuery();
			
			for (ArtifactBean artifactBean : partialArtifactList) {
				if (isValidId(artifactBean.getGroupId()) && isValidId(artifactBean.getArtifactId())) {
					BooleanQuery bq = new BooleanQuery();
					bq.add(new TermQuery(new Term(MavenCentralSearchApiConstants.GROUP_FIELD, artifactBean.getGroupId())), Occur.MUST);
					bq.add(new TermQuery(new Term(MavenCentralSearchApiConstants.ARTIFACT_FIELD, artifactBean.getArtifactId())), Occur.MUST);
					
					if (StringUtils.hasText(artifactBean.getType())) {
						bq.add(new TermQuery(new Term(MavenCentralSearchApiConstants.TYPE_FIELD, artifactBean.getType())), Occur.MUST);
					}
					artifactSearchQuery.add(bq, Occur.SHOULD);
				}
			}
			
			if (artifactSearchQuery.clauses().size() > 0) {
				SolrQuery solrQuery = new SolrQuery(LuceneUtils.queryToString(artifactSearchQuery));
				QueryResponse response = query(solrQuery, 0, partialArtifactList.size());
				
				validArtifacts.addAll(response.getBeans(ArtifactBean.class));
			}
		}
		
		Collections.sort(validArtifacts);
		return validArtifacts;
	}
}
