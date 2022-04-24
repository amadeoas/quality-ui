package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.validator.EmailValidator;


/**
 * Email field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewEmailField<B> extends EmailField implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6431761269355148262L;
	
	private static final String ERROR_MSG = "Invalid e-mail address {0}";

	private final Field field;
	private final int order;


	public ViewEmailField(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("field {0} is not for an e-mail field", field);
		}
		setName(field.getName());
		this.field = field;
		addValidations(binder, path);

		final int length = path.length();
		final String placeholder 
				= ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		
		final String strOrder = ViewField.getTranslation(this, path.append(ViewField.ORDER));
		
		path.setLength(length);
		if (!ObjectUtils.isEmpty(strOrder)) {
			this.order = Integer.valueOf(strOrder);
		} else {
			this.order = 0;
		}
	}
	
	@Override
	public int getOrder() {
		return this.order;
	}
	
	public static boolean isValidType(final Field field) {
		if (String.class != field.getType()) {
			return false;
		}

		final Email[] emails = field.getAnnotationsByType(Email.class);

		return !ObjectUtils.isEmpty(emails);
	}
  
	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> bind, final StringBuilder path) {
		final List<Validator<String>> validators = new ArrayList<>();
		// To say that a field must not be null
		final NotNull[] notNulls = this.field.getAnnotationsByType(NotNull.class);
		// To say that a string field must not be the empty string (i.e. it must have at least one character)
		final NotBlank[] notBlanks = this.field.getAnnotationsByType(NotBlank.class);
		// To say that a string field must be a valid email address
		final int length = path.length();
		String msg = getTranslation(path.append("errorMessage").toString());

		path.setLength(length);
		if (ObjectUtils.isEmpty(msg)) {
			msg = this.field.getAnnotationsByType(Email.class)[0].message();
			if (ObjectUtils.isEmpty(msg)) {
				msg = ERROR_MSG;
			}
		}
		setRequired(!ObjectUtils.isEmpty(notNulls) || !ObjectUtils.isEmpty(notBlanks));
		validators.add(new EmailValidator(msg) {

			@Override
			public ValidationResult apply(final String value, final ValueContext context) {
				String msg = null;

				if (ObjectUtils.isEmpty(value)) {
					if (!ObjectUtils.isEmpty(notNulls)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notNulls[0].message())) {
							msg = ERROR_MSG;
						} else {
							msg = notNulls[0].message();
						}
					} else if (!ObjectUtils.isEmpty(notBlanks)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notBlanks[0].message())) {
							msg = ERROR_MSG;
						} else {
							msg = notBlanks[0].message();
						}
					}
					
					if (msg != null) {
						return ValidationResult.error(
								MessageFormat.format(msg, field.getName(), value));
					}
					
					return ValidationResult.ok();
				}

				return super.apply(value, context);
			}

		});
		build(bind, validators);
	}

	private void build(final Binder<B> bind, final List<Validator<String>> validators) {
		final BindingBuilder<B, String> builder = bind.forField(this);

	    for (final Validator<String> v: validators) {
	    	builder.withValidator(v);
	    }
		builder.bind(this.field.getName());
	}

} // end class ViewEmailField
