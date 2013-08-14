package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Date;

public interface IComparableVersion {
	
	String getVersion();
	
	Date getLastUpdateDate();
}
