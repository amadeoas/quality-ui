package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;


/**
 * Date field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewDateField<B> extends DatePicker implements ViewField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2281909543404163328L;
	
	static final String ERROR_MSG = "Invalid date field {0}; value {1}";

	private final transient BaseViewdata data;


	public ViewDateField(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("field {0} is not for an date field", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(this.data.getFieldName());
		addValidations(binder, path);

		final int length = path.length();
		final String placeholder 
				= ViewField.getTranslation(null, this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		
		String value;

		value = ViewField.getTranslation(null, this, "view.general.datePicker.placeholder");
		if (!ObjectUtils.isEmpty(value)) {
			setPlaceholder(value);
		}
		value = ViewField.getTranslation(null, this, "view.general.datePicker.helperText");
		if (!ObjectUtils.isEmpty(value)) {
			setHelperText(value);
		}
		value = ViewField.getTranslation(null, this, "view.general.datePicker.locale");
		if (!ObjectUtils.isEmpty(value)) {
			setLocale(new Locale(value.toLowerCase(), value.toUpperCase()));
		}
	}

	@Override
	public int getOrder() {
		return this.data.getOrder();
	}
	
	public static boolean isValidType(final Field field) {
		return LocalDate.class.isAssignableFrom(field.getType());
	}
	  
	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<LocalDate>> validators = new ArrayList<>();
		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);

		setRequired(!ObjectUtils.isEmpty(notNulls));
		validators.add(new AbstractValidator<LocalDate>(ERROR_MSG) {

			@Override
			public ValidationResult apply(final LocalDate value, final ValueContext context) {
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

} // end class ViewDateField
