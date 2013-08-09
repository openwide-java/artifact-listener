package fr.openwide.maven.artifact.notifier.core.business.artifact.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.IComparableVersion;

public class MavenCentralVersionComparator implements Comparator<IComparableVersion>, Serializable {

	private static final long serialVersionUID = -6062668831010678541L;

	private static final MavenCentralVersionComparator INSTANCE = new MavenCentralVersionComparator();

	private static final Comparator<IComparableVersion> REVERSE_ORDER_INSTANCE = Collections.reverseOrder(INSTANCE);

	@Override
	public int compare(IComparableVersion v1, IComparableVersion v2) {
		int order = 0;
		
		if (v1 == null && v2 == null) {
			order = 0;
		} else if (v1 == null) {
			order = 1;
		} else if (v2 == null) {
			order = -1;
		} else {
			MavenCentralComparableVersion cv1 = new MavenCentralComparableVersion(v1);
			MavenCentralComparableVersion cv2 = new MavenCentralComparableVersion(v2);
			order = cv1.compareTo(cv2);
		}
		
		return order;
	}

	public static MavenCentralVersionComparator get() {
		return INSTANCE;
	}

	public static Comparator<IComparableVersion> reverse() {
		return REVERSE_ORDER_INSTANCE;
	}
}
