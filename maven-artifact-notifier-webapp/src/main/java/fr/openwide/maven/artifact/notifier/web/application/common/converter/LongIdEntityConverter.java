package fr.openwide.maven.artifact.notifier.web.application.common.converter;

import java.util.Locale;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.springframework.util.StringUtils;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.service.IEntityService;

public class LongIdEntityConverter<E extends GenericEntity<Long, ?>> extends AbstractConverter<E> {
	
	private static final long serialVersionUID = -6119059653808653326L;

	private final Class<E> targetType;
	
	@SpringBean
	private IEntityService entityService;

	public LongIdEntityConverter(Class<E> targetType) {
		Injector.get().inject(this);
		this.targetType = targetType;
	}
	
	@Override
	public String convertToString(E value, Locale locale) {
		if (value == null) {
			return null;
		}
		return convertToString(value.getId());
	}

	@Override
	public E convertToObject(String value, Locale locale) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return entityService.getEntity(targetType, convertToId(value, locale));
	}
	
	private String convertToString(Long value) {
		if (value == null) {
			return null;
		}
		return String.valueOf(value);
	}
	
	private Long convertToId(String value, Locale locale) {
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			throw newConversionException(e.getMessage(), value, locale); // NOSONAR : l'API Wicket oblige à perdre la stacktrace
		}
	}

	@Override
	protected Class<E> getTargetType() {
		return targetType;
	}

}
