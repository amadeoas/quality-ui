package co.uk.bocaditos.ui.views;

import org.springframework.core.env.Environment;

import com.vaadin.flow.component.select.Select;


/**
 * The route representation.
 * 
 * @author aasco
 */
public class Route extends Value<Route> {

	/**
	 * <id>;<schema_host>
	 * 
	 * @param env the environment.
	 */
	public Route(final Select<Route> select, final Environment env, final String viewId, 
			final String value) {
		super(select, env, viewId, value);
	}
	
	public String getSchemeHost() {
		return getValue();
	}

} // end class Route
