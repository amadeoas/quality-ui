package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.AbstractValidator;


/**
 * Double field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewDoubleField<B> extends NumberField implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final transient BaseViewdata data;

	
	public ViewDoubleField(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("Field {0} is not a number", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(this.data.getFieldName());

		final int length = path.length();
		final String placeholder = ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		addValidations(binder, path);
	}
	
	@Override
	public int getOrder() {
		return this.data.getOrder();
	}
	
	public static boolean isValidType(final Field field) {
		return field.getType() == Integer.class || field.getType() == Long.class
				|| field.getType() == Double.class || field.getType() == Float.class;
	}

	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<Double>> validators = new ArrayList<>();
		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);
		// Max/Min
		// To say that a numerical field is only valid when it’s value is above or below a certain value
		final DecimalMin[] mins = this.data.getAnnotationsByType(DecimalMin.class);
		// To say that a numerical field is only valid when it’s value is above or below a certain value
		final DecimalMax[] maxs = this.data.getAnnotationsByType(DecimalMax.class);

		final double min = ObjectUtils.isEmpty(mins) ? 0d : Double.parseDouble(mins[0].value());
		final double max = ObjectUtils.isEmpty(maxs) 
				? Double.MAX_VALUE : Double.parseDouble(maxs[0].value());

		setRequired(!ObjectUtils.isEmpty(notNulls));
		setMin(min);
		setMax(max);
		validators.add(new AbstractValidator<Double>(ViewLongField.ERROR_MSG) {

			@Override
			public ValidationResult apply(final Double value, final ValueContext context) {
				String msg = null;

				if (value == null) {
					if (!ObjectUtils.isEmpty(notNulls)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notNulls[0].message())) {
							msg = ViewLongField.ERROR_MSG;
						} else {
							msg = notNulls[0].message();
						}
					}
				} else if (min > value) {
					if (!ObjectUtils.isEmpty(mins)) {
						msg =  mins[0].message();
					} else {
						msg = ViewLongField.ERROR_MSG;
					}
				} else if (max < value) {
					if (!ObjectUtils.isEmpty(maxs)) {
						msg =  maxs[0].message();
					} else {
						msg = ViewLongField.ERROR_MSG;
					}
				}
				
				if (msg != null) {
					return ValidationResult.error(
							MessageFormat.format(msg, data.getFieldName(), value, min, max));
				}
				
				return ValidationResult.ok();
			}
			
		});
		this.data.build(this, binder, validators);
	}

} // end class ViewDoubleField
