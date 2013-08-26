package fr.openwide.maven.artifact.notifier.core.business.artifact.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ComparableVersion;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.IComparableVersion;

public class MavenCentralComparableVersion extends ComparableVersion {

	private static final Pattern DATE_PATTERN = Pattern.compile("^((19|20)\\d{2})(0?[1-9]|1[012])(0?[1-9]|[12]\\d|3[01])\\..*$");
	
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);
			return sdf;
		}
	};
	
	private IComparableVersion version;
	
	private Date date;
	
	public MavenCentralComparableVersion(IComparableVersion version) {
		super(version.getVersion());

		this.version = version;
		if (DATE_PATTERN.matcher(version.getVersion()).matches()) {
			try {
				this.date = DATE_FORMAT.get().parse(version.getVersion());
			} catch (ParseException e) {
			}
		}
	}
	
	public int compareTo(MavenCentralComparableVersion o) {
		if ((getDate() == null) == (o.getDate() == null)) {
			int result = super.compareTo(o);
			// NOTE: The superclass's compareTo can return 0 for two versions like 2 and 2.0, or *-rc1 and *-RC1.
			// If we simply return its result, one version is present in DB but never inserted in the artifact's set of versions.
			// In order to be consistent with Central and to avoid a FK error on delete, we string compare the versions if such is the case.
			if (result != 0) {
				return result;
			}
			return version.getVersion().compareTo(o.getVersion().getVersion());
		} else if (getDate() == null) {
			return version.getLastUpdateDate().compareTo(o.getDate());
		}
		return getDate().compareTo(o.getVersion().getLastUpdateDate());
	}

	public Date getDate() {
		return CloneUtils.clone(date);
	}

	public IComparableVersion getVersion() {
		return version;
	}
}
