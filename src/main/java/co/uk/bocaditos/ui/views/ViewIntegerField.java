package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.RegexpValidator;


/**
 * Integer field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewIntegerField<B> extends TextField implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 832105939883946538L;

    private final transient BaseViewdata data;


	public ViewIntegerField(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("Field {0} is not an string", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(field.getName());

		final int length = path.length();
		final String placeholder 
				= ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

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
		return (String.class == field.getType());
	}

	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<String>> validators = new ArrayList<>();
		// To say that a string field is only valid when it matches a certain regular expression
		final Pattern[] patters = this.data.getAnnotationsByType(Pattern.class);

		if (patters != null && patters.length > 0) {
			validators.add(new RegexpValidator(patters[0].message(), patters[0].regexp()));
		}

		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);
		// Max/Min
		// To say that a numerical field is only valid when it’s value is above or below a certain value
		final Min[] mins = this.data.getAnnotationsByType(Min.class);
		// To say that a numerical field is only valid when it’s value is above or below a certain value
		final Max[] maxs = this.data.getAnnotationsByType(Max.class);

		final int min = ObjectUtils.isEmpty(mins) ? 0 : (int) mins[0].value();
		final int max = ObjectUtils.isEmpty(maxs) ? Integer.MAX_VALUE : (int) maxs[0].value();

		setRequired(!ObjectUtils.isEmpty(notNulls));
		validators.add(new AbstractValidator<String>(ViewLongField.ERROR_MSG) {

			@Override
			public ValidationResult apply(final String value, final ValueContext context) {
				String msg = null;

				if (ObjectUtils.isEmpty(value)) {
					if (!ObjectUtils.isEmpty(notNulls)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notNulls[0].message())) {
							msg = ViewLongField.ERROR_MSG;
						} else {
							msg = notNulls[0].message();
						}
					}
				} else {
					final int v = Integer.parseInt(value);
	
					if (min > v) {
						if (!ObjectUtils.isEmpty(mins)) {
							msg =  mins[0].message();
						} else {
							msg = ViewLongField.ERROR_MSG;
						}
					} else if (max < v) {
						if (!ObjectUtils.isEmpty(maxs)) {
							msg =  maxs[0].message();
						} else {
							msg = ViewLongField.ERROR_MSG;
						}
					}
				}
				
				if (msg != null) {
					return ValidationResult.error(
							MessageFormat.format(msg, data.getFieldName(), value, min, max));
				}
				
				return ValidationResult.ok();
			}

		});
		build(binder, validators);
	}

	private void build(final Binder<B> binder, final List<Validator<String>> validators) {
		final BindingBuilder<B, String> builder = binder.forField(this);
		
		builder.withConverter(
		        Integer::valueOf,
		        String::valueOf,
		        // Text to use instead of the NumberFormatException mess
		        "Please enter a number");
	    for (final Validator<String> v: validators) {
	    	builder.withValidator(v);
	    }
		builder.bind(this.data.getFieldName());
	}

} // end class ViewIntegerField
