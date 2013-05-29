package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchApiService;

@Service("artifactVersionProviderService")
public class MavenCentralSearchApiArtifactVersionProviderServiceImpl implements IArtifactVersionProviderService {
	
	@Autowired
	private IMavenCentralSearchApiService mavenCentralSearchService;
	
	@Override
	public List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException {
		return mavenCentralSearchService.getArtifactVersions(groupId, artifactId);
	}

}
