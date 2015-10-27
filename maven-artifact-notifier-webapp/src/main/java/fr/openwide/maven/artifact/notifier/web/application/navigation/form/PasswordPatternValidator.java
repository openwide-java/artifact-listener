package fr.openwide.maven.artifact.notifier.web.application.navigation.form;

import java.util.Collections;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public class PasswordPatternValidator extends PatternValidator {

	private static final long serialVersionUID = 315971574858314679L;
	
	private static final String PASSWORD_VALIDATION_PATTERN;
	
	static {
		PASSWORD_VALIDATION_PATTERN = ".{" + User.MIN_PASSWORD_LENGTH + "," + User.MAX_PASSWORD_LENGTH + "}";
	}
	
	public PasswordPatternValidator() {
		super(PASSWORD_VALIDATION_PATTERN);
	}
	
	@Override
	protected IValidationError decorate(IValidationError error, IValidatable<String> validatable) {
		((ValidationError) error).setKeys(Collections.singletonList("register.password.malformed"));
		return error;
	}
	
}
