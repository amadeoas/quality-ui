package co.uk.bocaditos.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.env.Environment;

import com.vaadin.flow.component.select.Select;

import co.uk.bocaditos.ui.data.entity.Field;


/**
 * URL path representation, i.e including parameters.
 */
public class Path extends Value<Path> {
	
	private static final int INDEX_METHOD = 0;
	private static final int INDEX_REQUEST = 2;
	private static final int INDEX_RESPONSE = 3;

	private final String requestName;
	private final String method;
	private final Class<?> requestClass;
	private final Class<?> responseClass;
	private List<Field> headersParams;
	private List<Field> urlParams;


	/**
	 * <method>;<id>;<value>
	 * 
	 * @param env the environment.
	 * @throws ClassNotFoundException 
	 */
	public Path(final Select<Path> select, final Environment env, final String viewId, 
			final String value) throws ClassNotFoundException {
		super(select, env, viewId, buildPaths(value));
		
		this.requestName = value;

		// <method>;<context>[;<request_class>[;<reponse_class>]]
		final String baseKey = viewId + '.' + value;
		String v = env.getProperty(baseKey + ".path");
		final String[] parts = v.split(DIVIDER);

		this.method = parts[INDEX_METHOD].toUpperCase();
		this.requestClass = buildClass(parts.length <= INDEX_REQUEST ? null : parts[INDEX_REQUEST]);
		this.responseClass = buildClass(parts.length <= INDEX_RESPONSE ? null : parts[INDEX_RESPONSE]);

		this.headersParams = build(env, baseKey + ".headers");
		this.urlParams = build(env, baseKey + ".urlparams");
	}

	private static String buildPaths(final String value) {
		final int offset = value.indexOf(DIVIDER) + 1;

		return value.substring(offset);
	}

	/**
	 * @return <service>.<request-name>.
	 */
	public String getRequestName() {
		return this.requestName;
	}
	
	public String getMethod() {
		return this.method;
	}
	
	public List<Field> getHeaderParams() {
		return this.headersParams;
	}
	
	public List<Field> getUrlParams() {
		return this.urlParams;
	}
	
	public Class<?> getRequestClass() {
		return this.requestClass;
	}
	
	public Class<?> getResponseClass() {
		return this.responseClass;
	}
	
	private static List<Field> build(final Environment env, final String key) {
		// <param-name>:<info_id>:<regexp>:<min-length>:<max-length>:<required>:<default>[;<param-name>:<info_id>:<regexp>:<min-length>:<max-length>:<required>:<default>...]
		final String value = env.getProperty(key);

		if (value == null || value.isEmpty()) {
			return null;
		}

		final String[] strParams = value.split(DIVIDER);
		final List<Field> params;

		params = new ArrayList<>(strParams.length);
		for (final String param : strParams) {
			params.add(new Field(param.split(":")));
		}

		return params;
	}
	
	private static Class<?> buildClass(final String classPath) throws ClassNotFoundException {
		return (classPath == null || classPath.isEmpty()) ? null : Class.forName(classPath);
	}
	
} // end class Path
