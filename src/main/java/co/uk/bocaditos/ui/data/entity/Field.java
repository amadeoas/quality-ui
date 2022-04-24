package co.uk.bocaditos.ui.data.entity;

import java.util.Objects;


/**
 * .
 * 
 * @author aasco
 */
public class Field extends FieldDef<Field> {
	
	private static final int INDEX_DEFAULT_VALUE = FieldDef.INDEX_REQUIRED + 1;

	private String value;

	
	public Field() {
	}

	public Field(final String[] parts) {
		super(parts);

		if (parts.length >= INDEX_DEFAULT_VALUE) {
			this.value = parts[INDEX_DEFAULT_VALUE];
		}
	}
	
	/**
	 * 
	 * <name>:<>
	 * 
	 * @param fullValue
	 */
	public Field(final String fullValue) {
		super(fullValue);
	}

	public String getValue() {
		return this.value;
	}
	
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), this.value);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		super.toString(buf);

		return append(buf, "value", this.value);
	}

	@Override
	protected boolean equalsIt(final Field other) {
		return super.equalsIt(other)
				&& Objects.equals(this.value, other.value);
	}
	
	@Override
	protected void set(final String[] data) {
		this.value = data[INDEX_DEFAULT_VALUE];
	}

} // end class Field
