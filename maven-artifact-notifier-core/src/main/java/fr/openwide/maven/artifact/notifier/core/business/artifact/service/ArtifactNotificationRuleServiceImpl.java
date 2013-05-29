package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactNotificationRuleDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;

@Service("artifactNotificationRuleService")
public class ArtifactNotificationRuleServiceImpl extends GenericEntityServiceImpl<Long, ArtifactNotificationRule> implements IArtifactNotificationRuleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactNotificationRuleServiceImpl.class);
	
	private static final int MAX_RULE_LENGTH = 50;
	
	private IArtifactNotificationRuleDao artifactNotificationRuleDao;
	
	@Autowired
	public ArtifactNotificationRuleServiceImpl(IArtifactNotificationRuleDao artifactNotificationRuleDao) {
		super(artifactNotificationRuleDao);
		this.artifactNotificationRuleDao = artifactNotificationRuleDao;
	}
	
	@Override
	public ArtifactNotificationRule getByFollowedArtifactAndRegex(FollowedArtifact followedArtifact, String regex) {
		return artifactNotificationRuleDao.getByFollowedArtifactAndRegex(followedArtifact, regex);
	}
	
	@Override
	public boolean isRuleValid(String regex) {
		boolean valid = false;
		try {
			Pattern.compile(regex);
			valid = true;
		} catch (PatternSyntaxException e) {
			LOGGER.warn("Invalid regex", e);
		}
		valid = valid && regex.length() <= MAX_RULE_LENGTH;
		return valid;
	}
	
	@Override
	public void changeRuleType(ArtifactNotificationRule rule, ArtifactNotificationRuleType type) throws ServiceException,
			SecurityServiceException {
		if (!rule.getType().equals(type)) {
			rule.setType(type);
			update(rule);
		}
	}
	
	@Override
	public boolean checkRulesForVersion(String version, List<ArtifactNotificationRule> rules) {
		boolean compliance = true;
		for (ArtifactNotificationRule rule : rules) {
			boolean matches = version.matches(rule.getRegex());
			compliance = compliance && (matches ^ rule.getType().equals(ArtifactNotificationRuleType.IGNORE));
		}
		return compliance;
	}
}
