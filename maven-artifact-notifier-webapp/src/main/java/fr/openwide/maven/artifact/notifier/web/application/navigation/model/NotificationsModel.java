package fr.openwide.maven.artifact.notifier.web.application.navigation.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class NotificationsModel extends LoadableDetachableModel<Map<Date, Set<ArtifactVersionNotification>>> {

	private static final long serialVersionUID = 8972339645238539261L;
	
	@SpringBean
	private IUserService userService;

	private IModel<User> userModel;

	public NotificationsModel(IModel<User> userModel) {
		super();
		this.userModel = userModel;
		
		Injector.get().inject(this);
	}

	@Override
	protected Map<Date, Set<ArtifactVersionNotification>> load() {
		Map<Date, Set<ArtifactVersionNotification>> result = Maps.newTreeMap(Collections.reverseOrder());
		List<ArtifactVersionNotification> notificationList = userService.listRecentNotifications(userModel.getObject());

		Date previousDate = null;
		for (ArtifactVersionNotification notification : notificationList) {
			if (previousDate == null || !DateUtils.isSameDay(previousDate, notification.getCreationDate())) {
				previousDate = notification.getCreationDate();
				result.put(previousDate, Sets.<ArtifactVersionNotification>newTreeSet());
			}
			result.get(previousDate).add(notification);
		}
		return result;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (userModel != null) {
			userModel.detach();
		}
	}
}
