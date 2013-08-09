package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.spring.util.lucene.search.LuceneUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Repository("artifactDao")
public class ArtifactDaoImpl extends GenericEntityDaoImpl<Long, Artifact> implements IArtifactDao {

	private static final QArtifact qArtifact = QArtifact.artifact;
	
	private static final QArtifactVersion qArtifactVersion = QArtifactVersion.artifactVersion;
	
	@Autowired
	private IHibernateSearchService hibernateSearchService;
	
	@Override
	public List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersion)
			.where(qArtifactVersion.artifact.eq(artifact))
			.where(qArtifactVersion.lastUpdateDate.gt(date))
			.orderBy(qArtifactVersion.lastUpdateDate.desc());
		
		return query.list(qArtifactVersion);
	}
	
	@Override
	public Artifact getByGroupIdArtifactId(String groupId, String artifactId) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifact)
			.where(qArtifact.group.groupId.eq(groupId))
			.where(qArtifact.artifactId.eq(artifactId));
		
		return query.uniqueResult(qArtifact);
	}
	
	@Override
	public List<Artifact> listMostFollowedArtifacts(int limit) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifact)
			.where(qArtifact.deprecationStatus.eq(ArtifactDeprecationStatus.NORMAL))
			.orderBy(qArtifact.followersCount.desc())
			.limit(limit);
		
		return query.list(qArtifact);
	}
	
	@Override
	public List<Artifact> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException {
		String[] searchFields = new String[] {
				Binding.artifact().artifactId().getPath(),
				Binding.artifact().group().groupId().getPath()
		};
		
		QueryBuilder queryBuilder = Search.getFullTextEntityManager(getEntityManager()).getSearchFactory().buildQueryBuilder()
				.forEntity(Artifact.class).get();
		
		Query luceneQuery = queryBuilder.keyword().onField(Binding.artifact().deprecationStatus().getPath()).matching(ArtifactDeprecationStatus.NORMAL).createQuery();
		
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Binding.artifact().group().groupId().getPath(), SortField.STRING))
				.add(new SortField(Binding.artifact().artifactId().getPath(), SortField.STRING))
				.build(); 
		Sort sort = new Sort(sortFields.toArray(new SortField[sortFields.size()]));
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern, luceneQuery, limit, offset, sort);
	}
	
	@Override
	public List<Artifact> searchAutocompleteWithoutProject(String searchPattern, Integer limit, Integer offset) throws ServiceException {
		String[] searchFields = new String[] {
				Binding.artifact().artifactId().getPath(),
				Binding.artifact().group().groupId().getPath()
		};
		
		QueryBuilder queryBuilder = Search.getFullTextEntityManager(getEntityManager()).getSearchFactory().buildQueryBuilder()
				.forEntity(Artifact.class).get();
		
		Query notDeprecatedQuery = queryBuilder.keyword().onField(Binding.artifact().deprecationStatus().getPath()).matching(ArtifactDeprecationStatus.NORMAL).createQuery();
		Query withoutProjectQuery = queryBuilder.keyword().onField(Binding.artifact().project().getPath()).matching(null).createQuery();

		BooleanJunction<?> booleanJunction = queryBuilder.bool()
				.must(notDeprecatedQuery)
				.must(withoutProjectQuery);
		
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Binding.artifact().group().groupId().getPath(), SortField.STRING))
				.add(new SortField(Binding.artifact().artifactId().getPath(), SortField.STRING))
				.build(); 
		Sort sort = new Sort(sortFields.toArray(new SortField[sortFields.size()]));
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern, booleanJunction.createQuery(), limit, offset, sort);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Artifact> searchByName(String searchTerm, ArtifactDeprecationStatus deprecation, Integer limit, Integer offset) {
		FullTextQuery query = getSearchByNameQuery(searchTerm, deprecation);
		
		// Sort
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Binding.artifact().group().getPath() + '.' + ArtifactGroup.GROUP_ID_SORT_FIELD_NAME, SortField.STRING))
				.add(new SortField(Artifact.ARTIFACT_ID_SORT_FIELD_NAME, SortField.STRING))
				.build();
		query.setSort(new Sort(sortFields.toArray(new SortField[sortFields.size()])));
		
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		
		return (List<Artifact>) query.getResultList();
	}
	
	@Override
	public int countSearchByName(String searchTerm, ArtifactDeprecationStatus deprecation) {
		return getSearchByNameQuery(searchTerm, deprecation).getResultSize();
	}
	
	private FullTextQuery getSearchByNameQuery(String searchTerm, ArtifactDeprecationStatus deprecation) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
		
		QueryBuilder artifactQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Artifact.class).get();
		
		BooleanJunction<?> booleanJunction = artifactQueryBuilder.bool();

		if (deprecation != null) {
			booleanJunction.must(artifactQueryBuilder
					.keyword()
					.onField(Binding.artifact().deprecationStatus().getPath())
					.matching(deprecation)
					.createQuery());
		}
		
		if (StringUtils.hasText(searchTerm)) {
			booleanJunction.must(artifactQueryBuilder
					.keyword()
					.fuzzy().withPrefixLength(1).withThreshold(0.8F)
					.onField(Binding.artifact().artifactId().getPath())
					.andField(Binding.artifact().group().groupId().getPath())
					.matching(searchTerm)
					.createQuery());
		} else {
			booleanJunction.must(artifactQueryBuilder.all().createQuery());
		}
		
		return fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), Artifact.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Artifact> searchRecommended(String searchTerm, Integer limit, Integer offset) throws ServiceException {
		FullTextQuery query = getSearchRecommendedQuery(searchTerm);
		if (query == null) {
			return Lists.newArrayListWithExpectedSize(0);
		}
		
		// Sort
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Binding.artifact().followersCount().getPath(), SortField.LONG, true))
				.add(new SortField(Binding.artifact().group().getPath() + '.' + ArtifactGroup.GROUP_ID_SORT_FIELD_NAME, SortField.STRING))
				.add(new SortField(Artifact.ARTIFACT_ID_SORT_FIELD_NAME, SortField.STRING))
				.build();
		
		query.setSort(new Sort(sortFields.toArray(new SortField[sortFields.size()])));
		
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		
		return (List<Artifact>) query.getResultList();
	}
	
	@Override
	public int countSearchRecommended(String searchTerm) throws ServiceException {
		FullTextQuery query = getSearchRecommendedQuery(searchTerm);
		if (query == null) {
			return 0;
		}
		return query.getResultSize();
	}
	
	private FullTextQuery getSearchRecommendedQuery(String searchTerm) throws ServiceException {
		if (!StringUtils.hasText(searchTerm)) {
			return null;
		}
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
		
		QueryBuilder artifactQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Artifact.class).get();
		
		BooleanJunction<?> booleanJunction = artifactQueryBuilder.bool();
		
		booleanJunction.must(artifactQueryBuilder
				.keyword()
				.onField(Binding.artifact().deprecationStatus().getPath())
				.matching(ArtifactDeprecationStatus.NORMAL)
				.createQuery());
		
		try {
			searchTerm = LuceneUtils.getSimilarityQuery(searchTerm, 0.8F);
			String[] fields = new String[] {
					Binding.artifact().artifactId().getPath(),
					Binding.artifact().group().groupId().getPath()
			};
			Analyzer analyzer = Search.getFullTextEntityManager(getEntityManager()).getSearchFactory().getAnalyzer(Artifact.class);
			
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);
			parser.setDefaultOperator(MultiFieldQueryParser.AND_OPERATOR);

			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(parser.parse(searchTerm), BooleanClause.Occur.MUST);
			
			booleanJunction.must(booleanQuery);
		} catch (ParseException e) {
			throw new ServiceException(String.format("Error parsing request: %1$s", searchTerm), e);
		}
		
		return fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), Artifact.class);
	}
}
