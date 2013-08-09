package fr.openwide.maven.artifact.notifier.core.business.audit.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.audit.model.AuditSummary;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

@Service("auditService")
public class AuditServiceImpl implements IAuditService {
	
	@Autowired
	private IUserService userService;
	
	private Date getNow() {
		return new Date();
	}
	
	private User getAuthor() {
		return userService.getAuthenticatedUser();
	}

	@Override
	public void refreshAuditSummaryForCreate(AuditSummary auditSummary) {
		Date now = getNow();
		User author = getAuthor();
		
		// Si un créateur a déjà été spécifié, on ne l'écrase pas
		if (auditSummary.getCreationAuthor() == null) {
			auditSummary.setCreationAuthor(author);
		}
		if (auditSummary.getLastEditAuthor() == null) {
			auditSummary.setLastEditAuthor(author);
		}
		
		auditSummary.setCreationDate(now);
		auditSummary.setLastEditDate(now);
	}

	@Override
	public void refreshAuditSummaryForUpdate(AuditSummary auditSummary) {
		Date now = getNow();
		User author = getAuthor();
		
		auditSummary.setLastEditAuthor(author);
		auditSummary.setLastEditDate(now);
	}
}
