package fr.openwide.maven.artifact.notifier.core.business.artifact.util;

import java.util.Date;

import com.google.common.collect.Ordering;

import fr.openwide.core.commons.util.ordering.AbstractNullSafeComparator;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public class ArtifactVersionLastUpdateDateComparator extends AbstractNullSafeComparator<ArtifactVersion> {

	private static final long serialVersionUID = -7679829686548651732L;
	
	private static final ArtifactVersionLastUpdateDateComparator INSTANCE = new ArtifactVersionLastUpdateDateComparator();
	
	private static final Ordering<Date> ORDERING = Ordering.<Date>natural().nullsFirst();

	@Override
	protected int compareNotNullObjects(ArtifactVersion left, ArtifactVersion right) {
		return ORDERING.compare(left.getLastUpdateDate(), right.getLastUpdateDate());
	}
	
	public static ArtifactVersionLastUpdateDateComparator get() {
		return INSTANCE;
	}

}