package fr.openwide.maven.artifact.notifier.core.property;

import java.util.Date;

import fr.openwide.core.spring.property.model.AbstractPropertyIds;
import fr.openwide.core.spring.property.model.MutablePropertyId;

public final class MavenArtifactNotifierCorePropertyIds extends AbstractPropertyIds {
	
	/*
	 * Mutable Properties
	 */
	public static final MutablePropertyId<Date> LAST_SYNCHRONIZATION_DATE = mutable("lastSynchronizationDate");
	
	/*
	 * Immutable Properties
	 */

	// None
}
