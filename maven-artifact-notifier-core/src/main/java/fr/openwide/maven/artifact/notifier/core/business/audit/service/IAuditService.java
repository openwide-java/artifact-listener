package fr.openwide.maven.artifact.notifier.core.business.audit.service;

import fr.openwide.maven.artifact.notifier.core.business.audit.model.AuditSummary;

public interface IAuditService {
	
	void refreshAuditSummaryForCreate(AuditSummary auditSummary);
	
	void refreshAuditSummaryForUpdate(AuditSummary auditSummary);
}
