package fr.openwide.maven.artifact.notifier.core.util.init.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.more.util.init.service.AbstractImportDataServiceImpl;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

@Service("importDataService")
public class ImportDataServiceImpl extends AbstractImportDataServiceImpl {

	@Override
	protected List<String> getGenericListItemPackagesToScan() {
		return Lists.newArrayList();
	}

	@Override
	protected void importMainBusinessItems(Map<String, Map<String, GenericEntity<Long, ?>>> idsMapping, Workbook workbook) {
		doImportItem(idsMapping, workbook, Authority.class);
		doImportItem(idsMapping, workbook, UserGroup.class);
		doImportItem(idsMapping, workbook, User.class);
		doImportItem(idsMapping, workbook, ArtifactGroup.class);
		doImportItem(idsMapping, workbook, Artifact.class);
		doImportItem(idsMapping, workbook, ArtifactVersion.class);
		doImportItem(idsMapping, workbook, FollowedArtifact.class);
		doImportItem(idsMapping, workbook, ArtifactVersionNotification.class);
		doImportItem(idsMapping, workbook, EmailAddress.class);
	}
}