package fr.openwide.maven.artifact.notifier.core.test.business.version.util;

import java.sql.Date;

import org.junit.Assert;
import org.junit.Test;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.util.MavenCentralVersionComparator;

public class TestMavenCentralComparableVersion {

	@Test
	public void testMavenCentralComparableVersion() {
		ArtifactVersion classic1 = new ArtifactVersion("1.2", new Date(1131452383000L));
		ArtifactVersion classic2 = new ArtifactVersion("3.0-rc3", new Date(1299460443000L));
		ArtifactVersion date1 = new ArtifactVersion("20041127.091804", new Date(1132834783000L));
		ArtifactVersion date2 = new ArtifactVersion("20060216.105226", new Date(1176203983000L));
		
		Assert.assertEquals(1, MavenCentralVersionComparator.get().compare(classic2, classic1));
		Assert.assertEquals(1, MavenCentralVersionComparator.get().compare(classic2, date1));
		Assert.assertEquals(1, MavenCentralVersionComparator.get().compare(classic1, date1));
		Assert.assertEquals(0, MavenCentralVersionComparator.get().compare(date1, date1));
		Assert.assertEquals(-1, MavenCentralVersionComparator.get().compare(date1, date2));
		Assert.assertEquals(1, MavenCentralVersionComparator.get().compare(date2, classic1));
		Assert.assertEquals(-1, MavenCentralVersionComparator.get().compare(date2, classic2));
	}
	
}
