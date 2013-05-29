package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.Date;
import java.util.List;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Repository("artifactDao")
public class ArtifactDaoImpl extends GenericEntityDaoImpl<Long, Artifact> implements IArtifactDao {

	private static final QArtifact qArtifact = QArtifact.artifact;
	
	private static final QArtifactVersion qArtifactVersion = QArtifactVersion.artifactVersion;
	
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Artifact> searchByName(String searchTerm, Integer limit, Integer offset) {
		FullTextQuery query = getSearchByArtifactIdQuery(searchTerm);
		
		// Tri
		Sort sort = new Sort(
				new SortField(Binding.artifact().group().getPath() + '.' + ArtifactGroup.GROUP_ID_SORT_FIELD_NAME, SortField.STRING),
				new SortField(Artifact.ARTIFACT_ID_SORT_FIELD_NAME, SortField.STRING)
		);
		query.setSort(sort);
		
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		
		return (List<Artifact>) query.getResultList();
	}
	
	@Override
	public int countSearchByName(String searchTerm) {
		return getSearchByArtifactIdQuery(searchTerm).getResultSize();
	}
	
	private FullTextQuery getSearchByArtifactIdQuery(String searchTerm) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
		
		QueryBuilder artifactQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Artifact.class).get();
		
		BooleanJunction<?> booleanJunction = artifactQueryBuilder.bool();
		
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
}
