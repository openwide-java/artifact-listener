package fr.openwide.maven.artifact.notifier.core.business.url.hibernate;

import org.hibernate.type.Type;

import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkStatus;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

public class ExternalLinkWrapperInterceptor extends AbstractPropertyChangeInterceptor<ExternalLinkWrapper> {

	private static final long serialVersionUID = 1L;
	
	private static final String URL_FIELD_NAME = "url";
	private static final String STATUS_FIELD_NAME = "status";
	private static final String CONSECUTIVE_FAILURES_FIELD_NAME = "consecutiveFailures";
	private static final String LAST_CHECK_DATE_FIELD_NAME = "lastCheckDate";
	private static final String LAST_STATUS_CODE_FIELD_NAME = "lastStatusCode";

	@Override
	protected Class<ExternalLinkWrapper> getObservedClass() {
		return ExternalLinkWrapper.class;
	}

	@Override
	protected String getObservedFieldName() {
		return URL_FIELD_NAME;
	}

	@Override
	protected boolean onChange(Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		// If the link's URL field changes, we reset its other properties
		for (int i = 0; i < currentState.length; ++i) {
			if (STATUS_FIELD_NAME.equals(propertyNames[i])) {
				currentState[i] = ExternalLinkStatus.ONLINE;
			} else if (CONSECUTIVE_FAILURES_FIELD_NAME.equals(propertyNames[i])) {
				currentState[i] = 0;
			} else if (LAST_CHECK_DATE_FIELD_NAME.equals(propertyNames[i]) ||
				LAST_STATUS_CODE_FIELD_NAME.equals(propertyNames[i])) {
				currentState[i] = null;
			}
		}
		return true;
	}
}