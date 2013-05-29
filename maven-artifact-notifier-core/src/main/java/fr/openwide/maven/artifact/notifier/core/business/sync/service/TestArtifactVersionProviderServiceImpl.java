package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchApiService;

//@Service("artifactVersionProviderService")
public class TestArtifactVersionProviderServiceImpl implements IArtifactVersionProviderService {
	
	@Autowired
	private IMavenCentralSearchApiService mavenCentralSearchService;
	
	@Override
	public List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException {
		List<ArtifactVersionBean> result = Lists.newArrayList();
		
		result.add(createVersionBean(groupId, artifactId, "1.0"));
		result.add(createVersionBean(groupId, artifactId, "2.0"));
		result.add(createVersionBean(groupId, artifactId, "3.0"));
		return result;
	}

	private ArtifactVersionBean createVersionBean(String groupId, String artifactId, String version) {
		return createVersionBean(groupId, artifactId, version, new Date().getTime());
	}
	
	private ArtifactVersionBean createVersionBean(String groupId, String artifactId, String version, long timestamp) {
		ArtifactVersionBean versionBean = new ArtifactVersionBean();
		versionBean.setGroupId(groupId);
		versionBean.setArtifactId(artifactId);
		versionBean.setVersion(version);
		versionBean.setTimestamp(timestamp);
		return versionBean;
	}
}
