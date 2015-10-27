package fr.openwide.maven.artifact.notifier.web.application.navigation.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

public class ArtifactVersionLastUpdateDateModel extends LoadableDetachableModel<Map<Date, Set<ArtifactVersion>>> {
	
	// Inutilisé pour l'instant, à voir
	
	private static final long serialVersionUID = 8972339645238539261L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	@SpringBean
	private IArtifactVersionService artifactVersionService;

	public ArtifactVersionLastUpdateDateModel() {
		super();
		Injector.get().inject(this);
	}

	@Override
	protected Map<Date, Set<ArtifactVersion>> load() {
		Map<Date, Set<ArtifactVersion>> result = Maps.newTreeMap(Collections.reverseOrder());
		List<ArtifactVersion> versionList = artifactVersionService.listRecentReleases(configurer.getLastUpdatesArtifactsLimit());

		Date previousDate = null;
		int daysCount = 0;
		for (ArtifactVersion version : versionList) {
			if (previousDate == null || !DateUtils.isSameDay(previousDate, version.getLastUpdateDate())) {
				if (daysCount >= configurer.getLastUpdatesDaysLimit()) {
					break;
				}
				previousDate = version.getLastUpdateDate();
				result.put(previousDate, Sets.<ArtifactVersion>newTreeSet());
				daysCount++;
			}
			result.get(previousDate).add(version);
		}
		return result;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
	}
}
