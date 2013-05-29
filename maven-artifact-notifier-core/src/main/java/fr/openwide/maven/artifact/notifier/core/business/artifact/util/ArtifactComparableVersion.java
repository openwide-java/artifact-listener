package fr.openwide.maven.artifact.notifier.core.business.artifact.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ComparableVersion;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public class ArtifactComparableVersion extends ComparableVersion {

	private static final Pattern DATE_PATTERN = Pattern.compile("^((19|20)\\d{2})(0?[1-9]|1[012])(0?[1-9]|[12]\\d|3[01])\\..*$");
	
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);
			return sdf;
		}
	};
	
	private ArtifactVersion version;
	
	private Date date;
	
	public ArtifactComparableVersion(ArtifactVersion version) {
		super(version.getVersion());

		this.version = version;
		if (DATE_PATTERN.matcher(version.getVersion()).matches()) {
			try {
				this.date = DATE_FORMAT.get().parse(version.getVersion());
			} catch (ParseException e) {
			}
		}
	}
	
	public int compareTo(ArtifactComparableVersion o) {
		if ((getDate() == null) == (o.getDate() == null)) {
			return super.compareTo(o);
		} else if (getDate() == null){
			return version.getLastUpdateDate().compareTo(o.getDate());
		}
		return getDate().compareTo(o.getVersion().getLastUpdateDate());
	}

	public Date getDate() {
		return CloneUtils.clone(date);
	}

	public ArtifactVersion getVersion() {
		return version;
	}
}
