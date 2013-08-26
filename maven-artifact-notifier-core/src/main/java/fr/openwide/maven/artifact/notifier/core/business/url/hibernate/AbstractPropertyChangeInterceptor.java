package fr.openwide.maven.artifact.notifier.core.business.url.hibernate;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public abstract class AbstractPropertyChangeInterceptor<T> extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		if (getObservedClass().equals(entity.getClass())) {
			for (int i = 0; i < propertyNames.length; ++i) {
				if (propertyNames[i].equals(getObservedFieldName())) {
					
					boolean dirty = false;
					if (currentState[i] == null) {
						if (previousState[i] != null) {
							dirty = true;
						}
					} else if (!currentState[i].equals(previousState[i])) {
						dirty = true;
					}
					
					if (dirty) {
						return onChange(currentState, previousState, propertyNames, types);
					}
					break;
				}
			}
		}
		return false;
	}

	protected abstract Class<T> getObservedClass();
	
	protected abstract String getObservedFieldName();
	
	protected abstract boolean onChange(Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);
}
