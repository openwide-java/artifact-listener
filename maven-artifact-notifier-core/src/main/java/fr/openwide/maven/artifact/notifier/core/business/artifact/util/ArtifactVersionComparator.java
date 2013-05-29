package fr.openwide.maven.artifact.notifier.core.business.artifact.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public class ArtifactVersionComparator implements Comparator<ArtifactVersion>, Serializable {

	private static final long serialVersionUID = -6062668831010678541L;

	private static final ArtifactVersionComparator INSTANCE = new ArtifactVersionComparator();

	private static final Comparator<ArtifactVersion> REVERSE_ORDER_INSTANCE = Collections.reverseOrder(INSTANCE);

	@Override
	public int compare(ArtifactVersion v1, ArtifactVersion v2) {
		int order = 0;
		
		if (v1 == null && v2 == null) {
			order = 0;
		} else if (v1 == null) {
			order = 1;
		} else if (v2 == null) {
			order = -1;
		} else {
			ArtifactComparableVersion cv1 = new ArtifactComparableVersion(v1);
			ArtifactComparableVersion cv2 = new ArtifactComparableVersion(v2);
			order = cv1.compareTo(cv2);
		}
		
		return order;
	}

	public static ArtifactVersionComparator get() {
		return INSTANCE;
	}

	public static Comparator<ArtifactVersion> reverse() {
		return REVERSE_ORDER_INSTANCE;
	}
}
