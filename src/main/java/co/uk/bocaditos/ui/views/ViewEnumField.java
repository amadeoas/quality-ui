package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;


/**
 * Enumeration field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 * @param <E> the field enumeration type.
 */
public class ViewEnumField<B, E extends Enum<E>> extends Select<E> implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2530038469421232346L;

	private static final Logger logger = LoggerFactory.getLogger(ViewEnumField.class);
	
	private static final String ERROR_MSG = "Missing enumeration value";

	private final transient BaseViewdata data;
	private E[] values;


//	public static class Bean<E> {
//
//		private E field;
//
//
//		public E getField() {
//            return this.field;
//        }
//
//        public void setField(final E field) {
//            this.field = field;
//        }
//
//	}
//
//
	public ViewEnumField(final Binder<B> binder, final Class<E> clazz, final Field field, 
			final StringBuilder path, final String lable) {
		if (!isValidType(field)) {
			throw new ViewException("Field {0} is not an string", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(this.data.getFieldName());
		this.values = clazz.getEnumConstants();
		setItems(this.values);
		addValidations(binder, path);
		
		final int length = path.length();
		String value;
		
		value = ViewField.getTranslation(this, path.append(ViewField.LABLE));
		path.setLength(length);
		if (ObjectUtils.isEmpty(value)) {
			value = buildLabel(value);
		}
		setLabel(value);

		setValue(getTranslation(path.append("selection").toString()));
		path.setLength(length);
	}
	
	@Override
	public int getOrder() {
		return this.data.getOrder();
	}

	public static boolean isValidType(final Field field) {
		return field.getType().isEnum();
	}

	@SuppressWarnings("serial")
	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<E>> validators = new ArrayList<>();
		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);

		setRequired(!ObjectUtils.isEmpty(notNulls));
		validators.add(new AbstractValidator<E>(ERROR_MSG) {

			@Override
			public ValidationResult apply(final E value, final ValueContext context) {
				String msg = null;

				if (ObjectUtils.isEmpty(value) && !ObjectUtils.isEmpty(notNulls)) {
					// Must not be null
					if (ObjectUtils.isEmpty(notNulls[0].message())) {
						msg = ERROR_MSG;
					} else {
						msg = notNulls[0].message();
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
	
	private void setValue(String name) {
		if (!ObjectUtils.isEmpty(name)) {
			boolean notFound = true;

			name = name.toUpperCase();
			for (final E value : this.values) {
				if (value.name().equals(name)) {
					setValue(value);
					notFound = false;

					break;
				}
			}
			
			if (notFound) {
				logger.warn("Enumeration selection {} doesn't exist for enumeration {}", 
						name, this.data.getFieldType());
			}
		}
	}
	
	private String buildLabel(final String name) {
		final String[] parts = name.split("(?=\\p{Upper})");
		final StringBuilder buf = new StringBuilder(name.length() + parts.length - 1);

		parts[0] = Character.toUpperCase(name.charAt(0)) + parts[0].substring(1);
		for (final String str :parts) {
			buf.append(str)
				.append(' ');
		}

		return buf.toString();
	}

} // end class ViewEnumField


/**
 * Representation of a enumeration value with its associated title, text to be displayed.
 * 
 * @author aasco
 *
 * @param <E> an enumeration type.
 */
class EnumSelection<E extends Enum<E>> {

	private String title;
	private E value;
	

	EnumSelection(final E value, String title) {
		this.title = title;
		this.value = value;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public E getValue() {
		return this.value;
	}
	
	public String value() {
		return this.title;
	}
	
	public String toString() {
		return this.title;
	}

} // end class EnumSelection
