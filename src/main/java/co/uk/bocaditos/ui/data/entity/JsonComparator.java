package co.uk.bocaditos.ui.data.entity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Support for JSON operations.
 * 
 * @author aasco
 */
public class JsonComparator {
	
	private static final JsonComparator comparator = new JsonComparator();


	/**
	 * Representation of a JSON comparison mismatch.
	 * 
	 * @author aasco
	 */
	public class ErrorMsg {
		
		private final String path;
		private final String error;


		ErrorMsg(final String path, final String errorTemplate, final Object... params) {
			this.path = path;
			if (params == null || params.length == 0) {
				this.error = errorTemplate;
			} else {
				this.error = MessageFormat.format(errorTemplate, params);
			}
		}

		ErrorMsg(final StringBuilder path, final String errorTemplate, final Object... params) {
			this(path.toString(), errorTemplate, params);
		}
		
		public String getPath() {
			return this.path;
		}
		
		public String getError() {
			return this.error;
		}
		
		@Override
		public String toString() {
			final StringBuilder buf = new StringBuilder();

			buf.append('{')
				.append("\"path\": \"").append(this.path).append("\", ")
				.append("\"error\": \"").append(this.error).append('"')
				.append('}');

			return buf.toString();
		}

	} // end class Error


	/**
	 * Compares the provided JSONs.
	 * 
	 * @param json1 first JSON, if starts by "file:" specifies the file to load the JSON from, if 
	 * 			starts by "classpath:" specified the file within the JAR containing the JSON, 
	 * 			otherwise is the string JSON.
	 * @param json2 second JSON, if starts by "file:" specifies the file to load the JSON from, if 
	 * 			starts by "classpath:" specified the file within the JAR containing the JSON, 
	 * 			otherwise is the string JSON.
	 * @return lest or found comparison errors.
	 * @throws IOException when failed to load binary.
	 */
	public static List<ErrorMsg> compare(final String json1, final String json2) 
			throws IOException {
		return compare(parse(json1), parse(json2));
	}

	/**
	 * Compares the provided object using JSON.
	 * 
	 * @param obj1 first object.
	 * @param obj2 second object.
	 * @return lest or found comparison errors.
	 * @throws IOException when failed to load binary.
	 */
	public static <T> List<ErrorMsg> compare(final T obj1, final T obj2) {
		return compare(buildJson(obj1), buildJson(obj2));
	}

	/**
	 * Compares the provided JSONs.
	 * 
	 * @param json1 first JSON node.
	 * @param json2 second JSON node.
	 * @return lest or found comparison errors.
	 * @throws IOException when failed to load binary.
	 */
	public static List<ErrorMsg> compare(final JsonNode json1, final JsonNode json2) {
		final List<ErrorMsg> errors = new ArrayList<>();

		comparator.compare(new StringBuilder(), errors, json1, json2);

		return Collections.unmodifiableList(errors);
	}

	/**
	 * Build JSOn node from passed JSON.
	 * 
	 * @param str a JSON, if starts by "file:" specifies the file to load the JSON from, if 
	 * 			starts by "classpath:" specified the file within the JAR containing the JSON, 
	 * 			otherwise is the string JSON.
	 * @return
	 * @throws IOException
	 */
	public static JsonNode parse(final String str) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode node;

		if (str.startsWith("file:")) {
			final File file = new File(str.substring("file:".length()));

			node = mapper.readTree(file);
		} else if (str.startsWith("classpath:")) {
			try (final InputStream in = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(str.substring("classpath:".length()))) {
				node = mapper.readTree(in);
			}
		} else {
			node = mapper.readTree(str);
		}

		return node;
	}

	private void compare(final StringBuilder path, final List<ErrorMsg> errors, 
			final JsonNode json1, final JsonNode json2) {
		if (json1 == json2) {
			return;
		}

		if (json1 == null || json1.isNull()) {
			if (json2 != null && !json2.isNull()) {
				errors.add(new ErrorMsg(path, "Missing field {0}: null != {1}", 
						path, json2.getNodeType()));
			}

			return;
		} else if (json2 == null || json2.isNull()) {
			errors.add(new ErrorMsg(path, "Missing field {0}: {1} != null", 
					path, json1.getNodeType()));

			return;
		}
		switch (json1.getNodeType()) {
			case NUMBER: 
				if (theSameType(path, errors, json1, json2, JsonNodeType.NUMBER)) {
					compare(path, errors, json1.numberValue(), json2.numberValue());
				}

				break;

			case STRING:
				if (theSameType(path, errors, json1, json2, JsonNodeType.STRING)) {
					compare(path, errors, json1.textValue(), json2.textValue());
				}

				break;

			case BOOLEAN:
				if (theSameType(path, errors, json1, json2, JsonNodeType.BOOLEAN)) {
					compare(path, errors, json1.booleanValue(), json2.booleanValue());
				}

				break;

			case ARRAY:
				if (theSameType(path, errors, json1, json2, JsonNodeType.ARRAY)) {
					compare(path, errors, (ArrayNode) json1, (ArrayNode) json2);
				}

				break;

			case OBJECT:
				if (theSameType(path, errors, json1, json2, JsonNodeType.OBJECT)) {
					compare(path, errors, (ObjectNode) json1, (ObjectNode) json2);
				}

				break;

			case BINARY:
				if (theSameType(path, errors, json1, json2, JsonNodeType.BINARY)) {
					try {
						compare(path, errors, json1.binaryValue(), json2.binaryValue());
					} catch (final IOException ioe) {
						errors.add(new ErrorMsg(path, "Failed to get binary value: {0}", 
								ioe.getMessage()));
					}
				}

				break;

			default:
				errors.add(new ErrorMsg(path, "Field {0} unsupported type: {1} != {2}", 
						path, json1.getNodeType(), json2.getNodeType()));
		}
	}
	
	private boolean theSameType(final StringBuilder path, final List<ErrorMsg> errors, 
			final JsonNode json1, final JsonNode json2, final JsonNodeType type) {
		if (json2.getNodeType() == type) {
			return true;
		}

		errors.add(new ErrorMsg(path, "Field {0} isn't a {1}: {2} != {3}", 
					path, type.name().toLowerCase(), type, json1.getNodeType(), 
					json2.getNodeType()));

		return false;
	}
	
	private void compare(final StringBuilder path, final List<ErrorMsg> errors, 
			final ArrayNode array1, final ArrayNode array2) {
		if (array1 == null) {
			if (array2 != null) {
				errors.add(new ErrorMsg(path, "No array field {0}: ARRAY != null", path));
			}

			return;
		} else if (array2 == null) {
			errors.add(new ErrorMsg(path, "No array field {0}: null != ARRAY", path));

			return;
		}

		final int length = path.length();
		final int l;

		path.append('[');
		l = path.length();
		for (int index = 0; index < array1.size(); ++index) {
			final JsonNode node1 = array1.get(index);
			final JsonNode node2 = array2.get(index);

			path.append(index).append(']');
			compare(path, errors, node1, node2);
			path.setLength(l);
		}
		path.setLength(length);
	}
	
	private void compare(final StringBuilder path, final List<ErrorMsg> errors, 
			final ObjectNode obj1, final ObjectNode obj2) {
		if (obj1 == null) {
			if (obj2 != null) {
				errors.add(new ErrorMsg(path, "No object field {0}: OBJECT != null", path));
			}

			return;
		} else if (obj2 == null) {
			errors.add(new ErrorMsg(path, "No object field {0}: null != OBJECT", path));

			return;
		}

		Iterator<Map.Entry<String, JsonNode>> fields = obj1.fields();
		final int length = path.length();
		final int l;

		if (length > 0) {
			path.append('.');
			l = path.length();
		} else {
			l = length;
		}

		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> field = fields.next();

			path.append(field.getKey());
			compare(path, errors, field.getValue(), obj2.get(field.getKey()));
			path.setLength(l);
		}
		
		fields = obj2.fields();
		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> field = fields.next();
			final JsonNode node = obj1.get(field.getKey());

			path.append(field.getKey());
			if ((node == null || node.isNull()) && (field.getValue() != null && !field.getValue().isNull())) {
				errors.add(new ErrorMsg(path, "Missing field {0} in sencond object", path));
			}
			path.setLength(l);
		}
		
		path.setLength(length);
	}

	private void compare(final StringBuilder path, final List<ErrorMsg> errors, final String str1, 
			final String str2) {
		if (str1 == null) {
			if (str2 != null) {
				errors.add(new ErrorMsg(path, "No string field {0}: STRING != null", path));
			}
		} else if (str2 == null) {
			errors.add(new ErrorMsg(path, "No string field {0}: null != STRING", path));
		} else if (!str1.equals(str2)) {
			errors.add(new ErrorMsg(path, "String field {0} values are different: {1} != {2}", 
					path, str1, str2));
		}
	}
	
	private void compare(final StringBuilder path, final List<ErrorMsg> errors, final Number num1, 
			final Number num2) {
		if (num1 == null) {
			if (num2 != null) {
				errors.add(new ErrorMsg(path, "No number field {0}: NUMBER != null", path));
			}
		} else if (num2 == null) {
			errors.add(new ErrorMsg(path, "No number field {0}: null != NUMBER", path));
		} else if (!num1.equals(num2)) {
			errors.add(new ErrorMsg(path, 
					"Number field {0} values are different: {1, number} != {2, number}", 
					path, num1, num2));
		}
	}
	
	private void compare(final StringBuilder path, final List<ErrorMsg> errors, final boolean bool1, 
			final boolean bool2) {
		if (bool1 != bool2) {
			errors.add(new ErrorMsg(path, "Different boolean field (0) values {1} != {2}", 
					path, bool1, bool2));
		}
	}
	
	private void compare(final StringBuilder path, final List<ErrorMsg> errors, final byte[] bytes1, 
			final byte[] bytes2) {
		if (bytes1 == null) {
			if (bytes2 != null) {
				errors.add(new ErrorMsg(path, "No array of bytes field {0}: BYTES != null", path));
			}

			return;
		} else if (bytes2 == null) {
			errors.add(new ErrorMsg(path, "No array of bytes field {0}: null != BYTES", path));

			return;
		} else if (bytes1.length != bytes2.length) {
			errors.add(new ErrorMsg(path, 
					"Different array of bytes length of field {0} : {0, number} != {1, number}", 
					bytes1.length, bytes2.length));

			return;
		}
		
		for (int index = 0; index < bytes1.length; ++index) {
			if (bytes1[index] != bytes2[index]) {
				errors.add(new ErrorMsg(path, "Different array of bytes field {0} at {1, number}", 
						path, index));

				return;
			}
		}
	}
	
	private static <T> JsonNode buildJson(final T obj) {
		final ObjectMapper mapper = new ObjectMapper();

		return mapper.valueToTree(obj);
	}

} // end class JsonComparator
