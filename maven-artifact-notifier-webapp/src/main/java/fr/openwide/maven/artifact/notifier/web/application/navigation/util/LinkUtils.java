package fr.openwide.maven.artifact.notifier.web.application.navigation.util;

import java.io.Serializable;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IEmailAddressService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

/**
 * Utilitaire de gestion des liens au sein de l'application.
 * 
 * De manière générale, on centralise ici :
 * - tous les noms de paramètres qui vont être utilisés
 * - toutes les méthodes permettant de construire des liens qui ont un sens métier et sont réutilisables dans l'application
 */
public final class LinkUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkUtils.class);
	
	public static final String ID_PARAMETER = "id";
	
	public static final String GROUP_ID_PARAMETER = "groupId";
	
	public static final String ARTIFACT_ID_PARAMETER = "artifactId";
	
	public static final String HASH_PARAMETER = "hash";
	
	public static final String OPENID_SESSION_CREDENTIALS = "openIdSessionCredentials";

	public static PageParameters getUserPageParameters(User user) {
		PageParameters parameters = new PageParameters();
		parameters.add(ID_PARAMETER, user.getId());
		return parameters;
	}

	public static PageParameters getUserGroupPageParameters(UserGroup userGroup) {
		PageParameters parameters = new PageParameters();
		parameters.add(ID_PARAMETER, userGroup.getId());
		return parameters;
	}
	
	public static PageParameters getArtifactPageParameters(Artifact artifact) {
		PageParameters parameters = new PageParameters();
		if (artifact != null) {
			parameters.add(GROUP_ID_PARAMETER, artifact.getGroup().getGroupId());
			parameters.add(ARTIFACT_ID_PARAMETER, artifact.getArtifactId());
		}
		return parameters;
	}
	
	public static Artifact extractArtifactPageParameter(IArtifactService artifactService, PageParameters parameters,
			Class<? extends Page> redirectPageClass) {
		String groupId = parameters.get(LinkUtils.GROUP_ID_PARAMETER).toString();
		String artifactId = parameters.get(LinkUtils.ARTIFACT_ID_PARAMETER).toString();
		Artifact artifact = artifactService.getByArtifactKey(new ArtifactKey(groupId, artifactId));
		
		if (artifact == null) {
			LOGGER.error("Unable to retrieve artifact from url parameters");
			MavenArtifactNotifierSession.get().error(
					Application.get().getResourceSettings().getLocalizer().getString("common.error.noItem", null));
			
			throw new RestartResponseException(redirectPageClass);
		}
		return artifact;
	}
	
	public static PageParameters getUserHashPageParameters(User user) {
		PageParameters parameters = new PageParameters();
		parameters.add(HASH_PARAMETER, user.getNotificationHash());
		return parameters;
	}
	
	public static User extractUserFromHashPageParameter(IUserService userService, PageParameters parameters,
			Class<? extends Page> redirectPageClass) {
		String hash = parameters.get(LinkUtils.HASH_PARAMETER).toString();
		User user = userService.getByNotificationHash(hash);

		if (user == null) {
			LOGGER.error("Unable to get user from hash");
			MavenArtifactNotifierSession.get().error(
					Application.get().getResourceSettings().getLocalizer().getString("common.error.noItem", null));

			throw new RestartResponseException(redirectPageClass);
		}
		return user;
	}
	
	public static PageParameters getEmailHashPageParameters(EmailAddress emailAddress) {
		PageParameters parameters = new PageParameters();
		parameters.add(HASH_PARAMETER, emailAddress.getEmailHash());
		return parameters;
	}
	
	public static EmailAddress extractEmailFromHashPageParameter(IEmailAddressService emailAddressService, PageParameters parameters,
			Class<? extends Page> redirectPageClass) {
		String hash = parameters.get(LinkUtils.HASH_PARAMETER).toString();
		EmailAddress emailAddress = emailAddressService.getByHash(hash);

		if (emailAddress == null) {
			LOGGER.error("Unable to get email address from hash");
			MavenArtifactNotifierSession.get().error(
					Application.get().getResourceSettings().getLocalizer().getString("common.error.noItem", null));

			throw new RestartResponseException(redirectPageClass);
		}
		return emailAddress;
	}
	
	public static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>>
			E extractGenericEntityParameter(IGenericEntityService<K, E> entityService,
					PageParameters parameters, Class<K> keyClass, Class<? extends Page> redirectPageClass) {
		E entity;

		try {
			K entityId = parameters.get(LinkUtils.ID_PARAMETER).to(keyClass);
			entity = entityService.getById(entityId);
		} catch (Exception e) {
			entity = null;
		}
		if (entity == null) {
			LOGGER.error("Unable to get entity from id");
			MavenArtifactNotifierSession.get().error(
					Application.get().getResourceSettings().getLocalizer().getString("common.error.noItem", null));
			
			throw new RestartResponseException(redirectPageClass);
		}

		return entity;
	}
	
	public static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>>
			E extractGenericEntityParameter(IGenericEntityService<K, E> entityService,
					PageParameters parameters, Class<K> keyClass) {
		return extractGenericEntityParameter(entityService, parameters, keyClass, MavenArtifactNotifierApplication.get().getHomePage());
	}

	public static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>> IModel<E> extractGenericEntityModelParameter(
			IGenericEntityService<K, E> entityService, PageParameters parameters, Class<K> keyClass, Class<? extends Page> redirectPageClass) {
		return new GenericEntityModel<K, E>(extractGenericEntityParameter(entityService, parameters, keyClass, redirectPageClass));
	}
	
	public static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>> IModel<E> extractGenericEntityModelParameter(
			IGenericEntityService<K, E> entityService, PageParameters parameters, Class<K> keyClass) {
		return new GenericEntityModel<K, E>(extractGenericEntityParameter(entityService, parameters, keyClass));
	}
	
	private LinkUtils() {
	}
}
