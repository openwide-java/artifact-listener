package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.Date;
import java.util.List;

import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact_;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Service("artifactService")
public class ArtifactServiceImpl extends GenericEntityServiceImpl<Long, Artifact> implements IArtifactService {
	
	private IArtifactDao artifactDao;
	
	@Autowired
	private IHibernateSearchService hibernateSearchService;

	@Autowired
	public ArtifactServiceImpl(IArtifactDao artifactDao) {
		super(artifactDao);
		this.artifactDao = artifactDao;
	}

	@Override
	public List<Artifact> listByArtifactGroup(ArtifactGroup group) {
		return listByField(Artifact_.group, group);
	}
	
	@Override
	public List<Artifact> listByStatus(ArtifactStatus status) {
		return listByField(Artifact_.status, status);
	}

	@Override
	public Artifact getByArtifactKey(ArtifactKey artifactKey) {
		return artifactDao.getByGroupIdArtifactId(artifactKey.getGroupId(), artifactKey.getArtifactId());
	}
	
	@Override
	public List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date) {
		return artifactDao.listArtifactVersionsAfterDate(artifact, date);
	}
	
	@Override
	public List<Artifact> searchAutocomplete(String searchPattern) throws ServiceException, SecurityServiceException {
		String[] searchFields = new String[] {
				Binding.artifact().artifactId().getPath(),
				Binding.artifact().group().groupId().getPath()
		};
		
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern);
	}
	

	@Override
	public List<Artifact> search(String searchPattern) {
		return search(searchPattern, null, null);
	}
	
	@Override
	public List<Artifact> search(String searchPattern, Integer limit, Integer offset) {
		return search(searchPattern, Lists.<SortField>newArrayListWithExpectedSize(0), limit, offset);
	}
	
	@Override
	public List<Artifact> search(String searchPattern, List<SortField> sort, Integer limit, Integer offset) {
		return artifactDao.searchByName(searchPattern, sort, limit, offset);
	}
	
	@Override
	public int countSearch(String searchTerm) {
		return artifactDao.countSearchByName(searchTerm);
	}
}
