package fr.openwide.maven.artifact.notifier.core.business.task.model;

import fr.openwide.core.jpa.more.business.task.model.IQueueId;

public enum MavenArtifactNotifierTaskQueueId implements IQueueId {
	
	// Define queue IDs here.
	; 

	@Override
	public String getUniqueStringId() {
		return name();
	}

}
