package fr.openwide.maven.artifact.notifier.web.application.common.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class FollowingStatsModel extends LoadableDetachableModel<FollowingStats> {
	
	private static final long serialVersionUID = -7823908580420416409L;
	
	@SpringBean
	private IUserService userService;
	
	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	public FollowingStatsModel() {
		super();
		Injector.get().inject(this);
	}
	
	@Override
	protected FollowingStats load() {
		return new FollowingStats(userService.count(), followedArtifactService.count());
	}
	
}
