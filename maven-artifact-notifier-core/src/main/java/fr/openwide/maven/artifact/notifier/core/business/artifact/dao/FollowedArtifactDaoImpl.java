package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.QUser;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Repository("followedArtifactDao")
public class FollowedArtifactDaoImpl extends GenericEntityDaoImpl<Long, FollowedArtifact> implements IFollowedArtifactDao {
	
	private static final QUser qUser = QUser.user;
	
	@Override
	public List<User> listFollowers(Artifact artifact) {
		JPAQuery<User> query = new JPAQuery<>(getEntityManager());
		
		query
			.select(qUser)
			.from(qUser)
			.where(qUser.followedArtifacts.any().artifact.eq(artifact));
		
		return query.fetch();
	}
}
