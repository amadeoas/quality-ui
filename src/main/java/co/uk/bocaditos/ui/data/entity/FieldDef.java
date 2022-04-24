package co.uk.bocaditos.ui.data.entity;

import java.util.Objects;

import co.uk.bocaditos.ui.data.AbstractEntity;


/**
 * .
 * 
 * @author aasco
 */
public class FieldDef<T extends FieldDef<?>> extends AbstractEntity<T> {
	
	static final int INDEX_NAME			= 0;
	static final int INDEX_INFO_ID		= 1;
	static final int INDEX_REGEXP		= 2;
	static final int INDEX_MIN_LENGTH	= 3;
	static final int INDEX_MAX_LENGTH	= 4;
	static final int INDEX_REQUIRED		= 5;

	private String name;
	private String infoId;
	private String regExp;
	private int maxLength;
	private int minLength;
	private boolean required;


	public FieldDef() {
	}

	public FieldDef(final String[] parts) {
		this.name = parts[INDEX_NAME];
		this.infoId = parts[INDEX_INFO_ID];
		this.regExp = parts[INDEX_REGEXP];
		if (parts[INDEX_MIN_LENGTH] == null || parts[INDEX_MIN_LENGTH].isEmpty()) {
			this.minLength = 0;
		} else {
			this.minLength = Integer.parseInt(parts[INDEX_MIN_LENGTH]);
		}
		this.maxLength = Integer.parseInt(parts[INDEX_MAX_LENGTH]);
		if (parts[INDEX_REQUIRED] == null || parts[INDEX_REQUIRED].isEmpty()) {
			this.required = true;
		} else {
			this.required = Boolean.parseBoolean(parts[INDEX_REQUIRED]);
		}

		if (this.minLength > this.maxLength) {
			throw new RuntimeException("Min. length may not be bigger than the max. length; " 
					+ this.minLength + " > " + this.maxLength);
		}
	}

	public FieldDef(final String data) {
		setData(data.split(":"));
	}

	public String getName() {
		return this.name;
	}

	public String getInfoId() {
		return infoId;
	}
	
	public int getMaxLength() {
		return this.maxLength;
	}
	
	public int getMinLength() {
		return this.minLength;
	}

	public String getRegExp() {
		return regExp;
	}

	public boolean isRequired() {
		return required;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setInfoId(final String infoId) {
		this.infoId = infoId;
	}

	public void setRegExp(final String regExp) {
		this.regExp = regExp;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public void setMaxLength(final int maxLength) {
		this.maxLength = maxLength;
	}
	
	public void setMinLength(final int minLength) {
		this.minLength = minLength;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.name, this.infoId, this.regExp, this.minLength, 
				this.maxLength, this.required);
	}
	
	@Override
	public StringBuilder toString(final StringBuilder buf) {
		super.toString(buf);
		append(buf, "name", this.name);
		append(buf, "infoId", this.infoId);
		append(buf, "regExp", this.regExp);
		append(buf, "minLength", this.minLength);
		append(buf, "maxLength", this.maxLength);
		append(buf, "required", this.required);

		return buf;
	}

	protected boolean equalsIt(final T other) {
		return super.equalsIt(other)
				&& Objects.equals(this.name , other.getName())
				&& Objects.equals(this.infoId, other.getInfoId())
				&& Objects.equals(this.regExp , other.getRegExp())
				&& Objects.equals(this.minLength , other.getMinLength())
				&& Objects.equals(this.maxLength , other.getMaxLength())
				&& (this.required == other.isRequired());
	}
	
	protected void set(final String[] data) {
		// Nothing to do
	}
	
	private void setData(final String[] data) {
		this.name = data[INDEX_NAME].trim();
		this.infoId = data[INDEX_INFO_ID].trim();;
		this.regExp = data[INDEX_REGEXP];
		this.minLength = get(data[INDEX_MIN_LENGTH], 0);
		this.maxLength = get(data[INDEX_MAX_LENGTH], Integer.MAX_VALUE);
		this.required = this.minLength > 0;
		set(data);
	}
	
	static boolean get(String value, final boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		value = value.trim();

		return value.isEmpty() ? defaultValue : Boolean.parseBoolean(value);
	}
	
	static int get(String value, final int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		value = value.trim();

		return value.isEmpty() ? defaultValue : Integer.parseInt(value);
	}

} // end class FieldDef
