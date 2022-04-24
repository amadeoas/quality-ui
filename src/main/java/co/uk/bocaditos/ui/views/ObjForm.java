package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.GeneratedValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;


/**
 * General an object form.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ObjForm<B> extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3195220548407290771L;
	
	private static final Logger logger = LoggerFactory.getLogger(ObjForm.class);
	
	private final Binder<B> binder;


	public ObjForm(final Class<B> clazz, final StringBuilder path,
			@SuppressWarnings("rawtypes") final ValueChangeListener changeListener) 
			throws ViewException {
		this.binder = new Binder<>(clazz);
		add(build(clazz, path, changeListener));
	}
	
	public Component[] build(final Class<B> clazz, final StringBuilder path,
			@SuppressWarnings("rawtypes") final ValueChangeListener changeListener) 
			throws ViewException {
		final List<Component> components = new ArrayList<>();
		final int length;

		path.append("object.")
			.append(Character.toLowerCase(clazz.getSimpleName().charAt(0)))
			.append(clazz.getSimpleName().substring(1)).append('.');
		length = path.length();
		loadFields(clazz, path, components, changeListener);
		path.setLength(length);
		components.sort((o1, o2) -> ((ViewField) o1).getOrder() - ((ViewField) o2).getOrder());

		return components.toArray(new Component[components.size()]);
	}

	public void update(final B value) {
		this.binder.setBean(value);
	}
	
	public static String setName(final Method method) {
		return method.getName().replace("get", "set");
	}
	
	protected Binder<B> binder() {
		return this.binder;
	}

	protected String getLable(final StringBuilder path, final Field field) {
		path.append(field.getName()).append('.');

		final int length = path.length();
		String lable = getTranslation(path.append(ViewField.LABLE).toString());

		path.setLength(length);
		if (ObjectUtils.isEmpty(lable)) {
			final String name = field.getName();
			final StringBuilder n = new StringBuilder(name.length() + 4);
			int fromIndex = 1;
			int toIndex;
	
			while ((toIndex = name.indexOf(fromIndex)) != -1) {
				if (n.length() > 0) {
					n.append(' ');
				} else {
					n.append(Character.toUpperCase(name.charAt(0)));
					
				}
				n.append(name.substring(fromIndex, toIndex));
			}
			lable = n.toString();
		}

		return lable;
	}
	
	private void loadFields(final Class<?> clazz, final StringBuilder path, 
			final List<Component> components, 
			@SuppressWarnings("rawtypes") final ValueChangeListener changeListener) {
		for (final Class<?> cl : getClasses(clazz)) {
			final Method[] methods = cl.getMethods();
			final int length = path.length();
	
			for (final Method method : methods) {
				if (isValidField(clazz, method)) {
					final Field field = getField(clazz, getFieldName(method));
	
					if (field != null) {
						builField(path, field, components, changeListener);
						path.setLength(length);
					}
				}
			}
		}
	}
	
	private static boolean isValidField(final Class<?> clazz, final Method method) {
		try {
			if ((Modifier.isPublic(method.getModifiers()) 
					&& (method.getParameterTypes() == null || method.getParameterTypes().length == 0))
				&& (method.getName().startsWith("get") 
					|| (method.getName().startsWith("is") 
						&& Boolean.class.isAssignableFrom(method.getReturnType().getClass())))) {
				clazz.getMethod(setName(method), method.getReturnType());

				return true;
			}
		} catch (final SecurityException | NoSuchMethodException e) {
			// Nothing to do
		}
		
		return false;
	}
	
	private static Class<?>[] getClasses(final Class<?> clazz) {
		if (clazz.getSuperclass() != Object.class) {
			return new Class<?>[] {clazz, clazz.getSuperclass()}; // Consider super class if not Object
		}

		return new Class<?>[] {clazz}; // Consider super class if not Object
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void builField(final StringBuilder path, final Field field, 
			final List<Component> components, final ValueChangeListener changeListener) {
		final AbstractField<?, ?> f;

		if (ViewTextField.isValidType(field)) {
			if (ViewEmailField.isValidType(field)) {
				f = new ViewEmailField<>(this.binder, field, path, getLable(path, field));
			} else {
				f = new ViewTextField<>(this.binder, field, path, getLable(path, field)); 
			}
		} else if (ViewDoubleField.isValidType(field)) {
			f = new ViewDoubleField<>(this.binder, field, path, getLable(path, field));
		} else if (field.getType() == Boolean.class) {
			f = new Checkbox(getLable(path, field));
		} else if (ViewDateField.isValidType(field)) {
			f = new ViewDateField<>(this.binder, field, path, getLable(path, field));
		} else if (ViewDateTimeField.isValidType(field)) {
			f = new ViewDateTimeField<>(this.binder, field, path, getLable(path, field));
		} else if (ViewEnumField.isValidType(field)) {
			f = new ViewEnumField(this.binder, field.getType(), field, path, getLable(path, field));
		} else if (Collection.class.isAssignableFrom(field.getClass())) {
			// It is a collection
			logger.warn("Unsuported field {} of type {}; field was ignored", field.getName(), 
					field.getType().getSimpleName());

			return;
		} else {
			// Object
			builObjField(path, field, components, changeListener);

			return;
		}

		components.add(f);
		f.addValueChangeListener(changeListener);
	}

	/**
	 * Build the form for an object within an object.
	 * 
	 * @param path the object path.
	 * @param field the field that contain that object.
	 * @param components overall components.
	 */
	private void builObjField(final StringBuilder path, final Field field, 
			final List<Component> components, 
			@SuppressWarnings("rawtypes") final ValueChangeListener changeListener) {
		final int length = path.length();
		final List<Component> subComponents = new ArrayList<>();
		final OwnDetails objView;

		path.append(field.getName()).append('.');
		loadFields(field.getType(), path, subComponents, changeListener);
		objView = new OwnDetails(field, path, subComponents);
		path.setLength(length);
		if (objView.getOrder() % 2 == 0) { // TODO: this is not the best way to do it, maybe use a different layout than FormLayout
			components.add(objView.getEmpty());
		}
		components.add(objView);
	}
	
	private static String getFieldName(final Method getter) {
		final String name = getter.getName().substring("get".length());

		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
	
	private static <T> Field getField(final Class<T> clazz, final String name) 
			throws ViewException {
		try {
			final Class<?>[] classes = {clazz, null};
			final int size;

			if (clazz.getSuperclass() != Object.class) {
				classes[1] = clazz.getSuperclass();
				size = 2;
			} else {
				size = 1;
			}

			for (int index = 0; index < size; ++index) {
				final Class<?> c = classes[index];
				final Field field = c.getDeclaredField(name);
	
				if (ObjectUtils.isEmpty(field.getAnnotation(JsonIgnore.class)) 
						&& ObjectUtils.isEmpty(field.getAnnotation(GeneratedValue.class))) {
					return field;
				}
			}
		} catch (final NoSuchFieldException | SecurityException e) {
			logger.warn("No field {} in class {}", name, clazz.getName());
		}

		return null;
	}

} // end class ObjForm
