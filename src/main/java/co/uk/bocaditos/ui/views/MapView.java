package co.uk.bocaditos.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

import co.uk.bocaditos.ui.data.entity.Field;


/**
 * .
 * 
 * @author aasco
 */
public class MapView extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3412060107392381502L;

	private static final Logger logger = LoggerFactory.getLogger(MapView.class);
	
	private static final String FIELD_SEPARATOR = ";";
	
	private final String name;
	private Binder<Map<String, String>> binder;
	private Boolean valid;
	private final ValidChangeListener changeListener;


	public MapView(final String name, final Environment env, 
			final ValidChangeListener changeListener) {
		this(name, buildMap(name, env), changeListener);
	}

	public MapView(final String name, final List<Field> fields, 
			final ValidChangeListener changeListener) {
		logger.info("Creating fields {} view...", name);
		if (changeListener == null) {
			throw new RuntimeException("Missing Change listener for '" + name + "' MapView");
		}
		this.name = name;
		this.changeListener = changeListener;
		update(null, fields);
		logger.info("Fields {} view was created", name);
	}

	public MapView(final String name, final ValidChangeListener changeListener) {
		logger.info("Creating empty fields {} view...", name);
		if (changeListener == null) {
			throw new RuntimeException("Missing Change listener for '" + name + "' MapView");
		}
		this.name = name;
		this.changeListener = changeListener;
		logger.info("Empty fields {} view was created", name);
	}

	public Map<String, String> getData() {
		if (this.binder == null) {
			return null;
		}

		return this.binder.getBean();
	}

	public boolean isValid() {
		return (this.binder == null) || this.binder.isValid();
	}

	public void update(final String requestName, final List<Field> fields) {
		this.binder = buildBinder(requestName, fields);
		validate();
	}

	public boolean exists() {
		return this.binder != null;
	}

	public void clear() {
		if (this.binder != null) {
			// TODO: add implementation
		}
	}

	protected String getLable(final StringBuilder path, final Field field) {
		path.append(field.getName().toLowerCase()).append('.');

		final int length = path.length();
		String lable = ViewField.getTranslation(field.getName(), this, 
				path.append(ViewField.LABLE).toString());

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
	
	protected static List<Field> buildMap(final String name, final Environment env) {
		final List<Field> headers = new ArrayList<>();
		final String value = env.getProperty(name);
		final String[] hs;

		if (value != null && !value.isEmpty()) {
			hs = value.trim().split(FIELD_SEPARATOR);
			for (String v : hs) {
				v = v.trim();
				if (v.isEmpty()) {
					continue;
				}
				headers.add(new Field(v));
			}
		}

		return headers;
	}
	
	private Binder<Map<String, String>> buildBinder(final String requestName, 
			final List<Field> fields) {
		getElement().removeAllChildren();
		if (fields == null || fields.isEmpty()) {
			return null;
		}

		final StringBuilder path = new StringBuilder();
		final List<Component> components = new ArrayList<>();
		Binder<Map<String, String>> binder = null;
//		Tooltip tooltip;
		final int length;

		path.append("view.").append(this.name).append('.');
		if (requestName != null && !requestName.isEmpty()) {
			path.append(requestName).append('.');
		}
		length = path.length();
		for (final Field field : fields) {
			if (binder == null) {
				binder = new Binder<>();
			}

			final ViewTextField<Map<String, String>> f 
					= new ViewTextField<Map<String, String>>(binder, field, path, getLable(path, field));

			components.add(f);
//			if ((tooltip = f.buildTooltip(path, field)) != null) {
//				// Tooltip should be added to layout as well 
//				components.add(tooltip);
//			}
			path.setLength(length);
		}
		add(components.toArray(new Component[components.size()]));
		this.binder = binder;
		this.binder.addStatusChangeListener(e -> validate());
		
		return binder;
	}

	private void validate() {
		if (this.binder == null) {
			return;
		}

		final boolean valid = this.binder.isValid();

		if (this.valid == null || this.valid != valid) {
			this.valid = valid;
			this.changeListener.valid(this.name, this.valid);
		}
	}

} // end class HeadersView
