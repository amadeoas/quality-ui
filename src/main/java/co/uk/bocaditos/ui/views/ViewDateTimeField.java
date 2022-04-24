package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;


/**
 * Date time field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewDateTimeField<B> extends DateTimePicker implements ViewField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2281909543404163329L;
	
	static final String ERROR_MSG = "Invalid date and time field {0}; value {1}";

	private final transient BaseViewdata data;


	public ViewDateTimeField(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("field {0} is not for an e-mail field", field);
		}
		this.data = new BaseViewdata(this, field, path);
		addValidations(binder, path);
	}

	@Override
	public int getOrder() {
		return this.data.getOrder();
	}
	
	public String getName() {
		return this.data.getFieldName();
	}
	
	public static boolean isValidType(final Field field) {
		return LocalDateTime.class.isAssignableFrom(field.getType());
	}
	  
	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<LocalDateTime>> validators = new ArrayList<>();
		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);

		setRequiredIndicatorVisible(!ObjectUtils.isEmpty(notNulls));
		validators.add(new AbstractValidator<LocalDateTime>(ERROR_MSG) {

			@Override
			public ValidationResult apply(final LocalDateTime value, final ValueContext context) {
				String msg = null;

				if (ObjectUtils.isEmpty(value)) {
					if (!ObjectUtils.isEmpty(notNulls)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notNulls[0].message())) {
							msg = ERROR_MSG;
						} else {
							msg = notNulls[0].message();
						}
					}
					
					if (msg != null) {
						return ValidationResult.error(
								MessageFormat.format(msg, data.getFieldName(), value));
					}
				}
				
				return ValidationResult.ok();
			}
			
		});
		this.data.build(this, binder, validators);
	}

} // end class ViewDateTimeField
