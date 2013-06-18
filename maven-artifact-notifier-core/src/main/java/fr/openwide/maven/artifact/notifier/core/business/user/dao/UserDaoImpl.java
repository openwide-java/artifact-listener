package fr.openwide.maven.artifact.notifier.core.business.user.dao;

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

import fr.openwide.core.jpa.security.business.person.dao.AbstractPersonDaoImpl;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QFollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Repository("personDao")
public class UserDaoImpl extends AbstractPersonDaoImpl<User> implements IUserDao {

	private static final QArtifactVersionNotification qArtifactVersionNotification = QArtifactVersionNotification.artifactVersionNotification;
	
	private static final QFollowedArtifact qFollowedArtifact = QFollowedArtifact.followedArtifact;
	
	public UserDaoImpl() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> search(String searchTerm, Integer limit, Integer offset) {
		FullTextQuery query = getSearchQuery(searchTerm);
		
		query.setSort(new Sort(new SortField(Binding.user().userName().getPath(), SortField.STRING)));
		
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		
		return (List<User>) query.getResultList();
	}
	
	@Override
	public int countSearch(String searchTerm) {
		return getSearchQuery(searchTerm).getResultSize();
	}
	
	private FullTextQuery getSearchQuery(String searchTerm) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
		
		QueryBuilder userQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(User.class).get();
		
		BooleanJunction<?> booleanJunction = userQueryBuilder.bool();
		
		if (StringUtils.hasText(searchTerm)) {
			booleanJunction.must(userQueryBuilder
					.keyword()
					.fuzzy().withPrefixLength(1).withThreshold(0.8F)
					.onField(Binding.user().userName().getPath())
					.andField(Binding.user().fullName().getPath())
					.matching(searchTerm)
					.createQuery());
		} else {
			booleanJunction.must(userQueryBuilder.all().createQuery());
		}
		
		return fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), User.class);
	}
	
	@Override
	public List<ArtifactVersionNotification> listLastNotifications(User user, long limit) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersionNotification)
			.where(qArtifactVersionNotification.user.eq(user))
			.orderBy(qArtifactVersionNotification.creationDate.desc())
			.limit(limit);
		
		return query.list(qArtifactVersionNotification);
	}
	
	@Override
	public List<ArtifactVersionNotification> listNotificationsAfterDate(User user, Date date) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersionNotification)
			.where(qArtifactVersionNotification.user.eq(user))
			.where(qArtifactVersionNotification.creationDate.after(date))
			.orderBy(qArtifactVersionNotification.creationDate.desc());
		
		return query.list(qArtifactVersionNotification);
	}
	
	@Override
	public FollowedArtifact getFollowedArtifact(User user, Artifact artifact) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qFollowedArtifact)
			.where(qFollowedArtifact.user.eq(user))
			.where(qFollowedArtifact.artifact.eq(artifact));
		
		return query.singleResult(qFollowedArtifact);
	}
}
