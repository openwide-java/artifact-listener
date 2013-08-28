package fr.openwide.maven.artifact.notifier.web.application.navigation.util;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IEmailAddressService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
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
	
	public static final String SEARCH_TERM_PARAMETER = "term";
	
	public static final String PAGE_NUMBER_PARAMETER = "page";
	
	public static final String HASH_PARAMETER = "hash";
	
	public static PageParameters getSearchPageParameters(IModel<String> searchTermModel) {
		PageParameters parameters = new PageParameters();
		String term = searchTermModel.getObject();
		if (StringUtils.hasText(term)) {
			parameters.add(SEARCH_TERM_PARAMETER, term);
		}
		return parameters;
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
	
	public static long extractPageNumberParameter(PageParameters parameters) {
		long pageNumber;
		try {
			pageNumber = parameters.get(PAGE_NUMBER_PARAMETER).toLong();
		} catch (Exception e) {
			pageNumber = 0;
		}
		return pageNumber;
	}
	
	private LinkUtils() {
	}
}
