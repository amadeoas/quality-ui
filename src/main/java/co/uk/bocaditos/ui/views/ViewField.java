package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.Component;


/**
 * .
 * 
 * @author aasco
 */
public interface ViewField {

	public static final String LABLE = "_lable";
	public static final String PLACEHOLDER = "_placeholder";
	public static final String ORDER = "_order";
	public static final String TOOLTIP = "tooltip";
	
	// Default
	public static final int DEFAULT_ORDER = 0;
	
	
	public int getOrder();
	
	public static <T extends Component> String getTranslation(final T comp, 
			final StringBuilder key, final Object... params) {
		return getTranslation(comp, key.toString(), params);
	}
	
	public static <T extends Component> String getTranslation(final T comp, 
			final String key, final Object... params) {
		try {
			return comp.getTranslation(key, params);
		} catch (final Exception e) {
			return null;
		}
	}
	
	public static <T extends Component> String getTranslation(final String defaultValue, 
			final T comp, final StringBuilder key, final Object... params) {
		return getTranslation(defaultValue, comp, key.toString(), params);
	}
	
	public static <T extends Component> String getTranslation(final String defaultValue, 
			final T comp, final String key, final Object... params) {
		final String value = getTranslation(comp, key, params);

		if (notDefined(value)) {
			return defaultValue;
		}

		return value;
	}
	
	public static <T extends Component> boolean getTranslation(final boolean defaultValue, 
			final T comp, final StringBuilder key, final Object... params) {
		final String value = getTranslation(comp, key, params);

		if (notDefined(value)) {
			return defaultValue;
		}

		return Boolean.parseBoolean(value);
	}
	
	public static <T extends Component> int getTranslation(final int defaultValue, 
			final T comp, final StringBuilder key, final Object... params) {
		return getTranslation(defaultValue, comp, key.toString(), params);
	}
	
	public static <T extends Component> int getTranslation(final int defaultValue, 
			final T comp, final String key, final Object... params) {
		final String value = getTranslation(comp, key, params);

		if (notDefined(value)) {
			return defaultValue;
		}

		return Integer.parseInt(value);
	}
	
	public static boolean notDefined(final String value) {
		return (value == null || value.endsWith(DcmI18NProvider.NULL_SUFIX));
	}

} // end class ViewField
