package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;


/**
 * Boolean field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewCheckbox<B> extends Checkbox implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8758225955244006597L;

	private final transient BaseViewdata data;

	
	public ViewCheckbox(final Binder<B> binder, final Field field, final StringBuilder path, 
			final String lable) {
		super(lable);
		if (!isValidType(field)) {
			throw new ViewException("Field {0} is not for a boolean field", field);
		}
		this.data = new BaseViewdata(this, field, path);
		setName(field.getName());
		
		final int length = path.length();
		final String initialValue = ViewField.getTranslation(this, path.append("initialValue"));
		
		path.setLength(length);
		if (!ObjectUtils.isEmpty(initialValue)) {
			setValue(Boolean.valueOf(initialValue));
		}

		build(binder);
	}
	
	@Override
	public int getOrder() {
		return this.data.getOrder();
	}
	
	public static boolean isValidType(final Field field) {
		return Boolean.class.isAssignableFrom(field.getType());
	}

	private void build(final Binder<B> binder) {
		final BindingBuilder<B, Boolean> builder = binder.forField(this);
		
		builder.bind(this.data.getField().getName());
	}

} // end class ViewCheckbox
