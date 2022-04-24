package co.uk.bocaditos.ui.views;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.springframework.core.env.Environment;

import com.vaadin.flow.component.select.Select;


/**
 * List of values.
 * 
 * @author aasco
 * 
 * @param <V> .
 */
public class Values<V extends Value<V>> extends ArrayList<V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 163638368461896320L;


	Values(final Environment env, Class<V> clazz, final Select<V> select, final String viewId) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {
		final String n = clazz.getSimpleName().toLowerCase();
		final String[] parts = getProperty(env, viewId + "." + n + 's').split(",");
		final Class<?>[] paramClasses = {Select.class, Environment.class, String.class, 
				String.class};
		final Constructor<V> builder = clazz.getConstructor(paramClasses);
		final StringBuilder name = new StringBuilder();

		for (final String str : parts) {
			add(builder.newInstance(select, env, viewId, str));
		}
		name.append(ObjView.PROP_PRE).append(viewId)
			.append('.').append(n)
			.append('.').append(ViewField.LABLE);
        select.setLabel(ViewField.getTranslation(clazz.getSimpleName(), select, name));
        select.setItems(this);
        select.getStyle().set(EntryPointWrapper.WIDTH, "40%");
        select.setRequiredIndicatorVisible(true);
	}
	
	private String getProperty(final Environment env, final String key) {
		final String value = env.getProperty(key);
		
		if (value == null) {
			throw new ViewException("Failed to get property {0}", key);
		}
		
		return value;
	}

} // end class Values
