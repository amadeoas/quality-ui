package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;

import co.uk.bocaditos.ui.data.entity.FieldDef;
import dev.mett.vaadin.tooltip.Tooltips;


/**
 * Text field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewTextField<B> extends TextField implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 832105939883946538L;
	
	private static final String ERROR_MSG = "Invalid vaue \"{0}\"";

	private final transient BaseViewdata data;


	public ViewTextField(final Binder<B> binder, final Field field, 
			final StringBuilder path, final String lable) {
		super(lable);

		if (!isValidType(field)) {
			throw new ViewException("Field {0} is not an string", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(this.data.getFieldName());

		final int length = path.length();
		final String placeholder = ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		buildTooltip(path, null);
		addValidations(binder, path);
	}

	public ViewTextField(final Binder<B> binder, final co.uk.bocaditos.ui.data.entity.Field field, 
			final StringBuilder path, final String lable) {
		super(lable);

		this.data = new BaseViewdata(this, field, path);
		setName(this.data.getFieldName());

		final int length = path.length();
		final String placeholder = ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		buildTooltip(path, field);
		addValidations(binder, field, path);
	}
	
	private void buildTooltip(final StringBuilder path, final FieldDef<?> filedDef) {
		final int length = path.length();
		final String msg = getTranslation(path.append(ViewField.TOOLTIP), filedDef);

		path.setLength(length);
		if (!msg.endsWith("null")) {
//			final TooltipConfiguration tconf = new TooltipConfiguration(msg);
//			
//			tconf.setDuration(null, 20);
//			tconf.setContent(msg);
//			tconf.setFollowCursor(TC_FOLLOW_CURSOR.HORIZONTAL);
//			tconf.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
//			tconf.setShowOnCreate(false);
//
//			Tooltips.getCurrent().setTooltip(this, tconf);
			Tooltips.getCurrent().setTooltip(this, msg);
		}
	}
	
	public String getTranslation(final StringBuilder path, final FieldDef<?> filedDef) {
		if (filedDef == null) {
			return ViewField.getTranslation(this, path);
		}

		return ViewField.getTranslation(this, path, filedDef.getRegExp(), filedDef.getMinLength(), 
				filedDef.getMaxLength());
	}
	
	@Override
	public int getOrder() {
		return this.data.getOrder();
	}

	public static boolean isValidType(final Field field) {
		return (String.class == field.getType());
	}

	protected void addValidations(final Binder<B> binder, final FieldDef<?> filed, 
			final StringBuilder path) {
		final int length = path.length();
		final List<Validator<String>> validators = new ArrayList<>();
		String msg;

		if (filed.getRegExp() != null) {
			final String str = path.toString().substring(0, path.length() - 1);
			int offset = str.lastIndexOf('.');
			final String name;

			if (offset == -1) {
				name = str;
			} else {
				name = str.substring(++offset);
			}
			msg = MessageFormat.format(
					getTranslation(path.append("pattern.errorMessage").toString()), name, 
					filed.getRegExp());
			path.setLength(length);
			validators.add(new RegexpValidator(msg, filed.getRegExp()));
		}
		setRequired(filed.isRequired());

		msg = MessageFormat.format(
				getTranslation(path.append("length.errorMessage").toString()), 
				this.data.getFieldName(), filed.getMinLength(), filed.getMaxLength());
		path.setLength(length);
		addValidation(validators, msg, filed.getMinLength(), filed.getMaxLength());
		this.data.build(this, binder, validators);
	}

	protected void addValidations(final Binder<B> binder, final StringBuilder path) {
		final List<Validator<String>> validators = new ArrayList<>();
		// To say that a string field is only valid when it matches a certain regular expression
		final Pattern[] patters = this.data.getAnnotationsByType(Pattern.class);
		int minLength;
		String msg;

		if (!ObjectUtils.isEmpty(patters)) {
			msg = patters[0].message();
			if (msg.contains("constraints.Pattern.message")) {
				final int length = path.length();

				msg = MessageFormat.format(
						getTranslation(path.append("pattern.errorMessage").toString()), 
						this.data.getFieldName(), patters[0].regexp());
				path.setLength(length);
			}
			validators.add(new RegexpValidator(msg, patters[0].regexp()));
		}
		minLength = setRequired(path, validators);

		// Length
		final Size[] sizes = this.data.getAnnotationsByType(Size.class);
		int maxLength = Integer.MAX_VALUE;

		msg = null;
		if (!ObjectUtils.isEmpty(sizes)) {
			msg = sizes[0].message();
			if (msg.contains("constraints.Size.message")) {
				msg = null;
			}
			minLength = sizes[0].min();
			if (minLength > 0) {
				setRequired(true);
			}
			maxLength = sizes[0].max();
		}
		if (ObjectUtils.isEmpty(msg)) {
			final int length = path.length();

			msg = getTranslation(path.append("errorMessage").toString());
			path.setLength(length);
		}
		addValidation(validators, msg, minLength, maxLength);
		this.data.build(this, binder, validators);
	}
	
	void addValidation(final List<Validator<String>> validators, final String msg,
			final int minLength, final int maxLength) {
		validators.add(new StringLengthValidator(
				MessageFormat.format(msg, this.data.getFieldName(), minLength, maxLength), 
				minLength, maxLength));
	}

	@SuppressWarnings("serial")
	private int setRequired(final StringBuilder path, final List<Validator<String>> validators) {
		int minLength = 0;
		// To say that a field must not be null
		final NotNull[] notNulls = this.data.getAnnotationsByType(NotNull.class);
		// To say that a string field must not be the empty string (i.e. it must have at least one character)
		final NotBlank[] notBlanks = this.data.getAnnotationsByType(NotBlank.class);

		if (!ObjectUtils.isEmpty(notBlanks) || !ObjectUtils.isEmpty(notNulls)) {
			setRequired(true);
			minLength = 1;
		} else {
			final int length = path.length();
			final boolean value = ViewField.getTranslation(false, this, path.append("required"));
			
			if (value) {
				setRequired(true);
				minLength = 1;
			}
			path.setLength(length);
		}

		validators.add(new AbstractValidator<String>(ERROR_MSG) {

			@Override
			public ValidationResult apply(final String value, final ValueContext context) {
				String msg = null;

				if (ObjectUtils.isEmpty(value)) {
					if (value == null && !ObjectUtils.isEmpty(notNulls)) {
						// Must not be null
						if (ObjectUtils.isEmpty(notNulls[0].message())) {
							msg = ERROR_MSG;
						} else {
							msg = notNulls[0].message();
						}
					} else if (!ObjectUtils.isEmpty(notBlanks)) {
						// Not empty
						if (ObjectUtils.isEmpty(notBlanks[0].message())) {
							msg = ERROR_MSG;
						} else {
							msg = notBlanks[0].message();
						}
					}
				} 
				
				if (msg != null) {
					return ValidationResult.error(
							MessageFormat.format(msg, data.getFieldName(), value));
				}
				
				return ValidationResult.ok();
			}
			
		});

		return minLength;
	}

} // end class ViewTextField
