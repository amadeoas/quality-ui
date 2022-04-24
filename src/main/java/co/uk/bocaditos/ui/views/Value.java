package co.uk.bocaditos.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.env.Environment;

import com.vaadin.flow.component.select.Select;


/**
 * URL path representation, i.e including parameters.
 */
public abstract class Value<T> {
	
	static final String DIVIDER = ";";

	static final int INDEX_ID = 0;
	static final int INDEX_VALUE = 1;
	
	private static final List<String> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>(0));

	private final String id;
	private final String value;
	private final String title;
	private final List<String> params;


	/**
	 * <id>;<value>[;<param>[;<param>...]]
	 * 
	 * @param env the environment.
	 */
	protected Value(final Select<T> select, final Environment env, final String viewId, 
			final String value) {
		// <id>;<value>[,...]
		final String[] parts = value.split(DIVIDER);
		
		this.id = parts[INDEX_ID].toLowerCase();
		if (parts.length == 1) {
			this.value = this.title = parts[0];
			this.params = EMPTY_LIST;
		} else {
			this.value = parts[INDEX_VALUE];
			this.title = select.getTranslation("view" + '.' + viewId + "." 
					+ getClass().getSimpleName().toLowerCase() + "." + this.id);
			if (parts.length > 2) {
				this.params = new ArrayList<>(parts.length - 2);
				for (int index = 2; index < parts.length; ++index) {
					
				}
			} else {
				this.params = EMPTY_LIST;
			}
		}
	}
	
	public final String getId() {
		return this.id;
	}
	
	public final String getValue() {
		return this.value;
	}
	
	public final String getTitle() {
		return this.title;
	}
	
	public final List<String> getPArams() {
		return this.params;
	}
	
	@Override
	public final String toString() {
		return this.title;
	}
	
} // end class View
