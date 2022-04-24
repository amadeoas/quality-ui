package co.uk.bocaditos.ui.data;

import com.vaadin.fusion.Nonnull;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class AbstractEntity<T extends AbstractEntity<?>> {

    @Id
    @GeneratedValue
    @Nonnull
    private Integer id;


	public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (this.id != null) {
            return this.id.hashCode();
        }

        return super.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
    	if (this == obj) {
    		return true;
    	}
    	if (obj == null) {
    		return false;
    	}

    	if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }

        @SuppressWarnings("unchecked")
		final T other = (T) obj;

        if (this.id != null) {
            return id.equals(other.getId());
        }

        return equalsIt(other);
    }

    public StringBuilder toString(final StringBuilder buf) {
    	buf.append("id: ").append(this.id);

    	return buf;
    }

	@Override
	public final String toString() {
		final StringBuilder buf = new StringBuilder();
		
		buf.append(getClass().getSimpleName()).append(" {");

		return toString(buf).append('}').toString();
	}

	protected boolean equalsIt(T other) {
		return ((int) id) == ((int) other.getId());
	}

	protected StringBuilder append(final StringBuilder buf, final String name, final String value) {
		int length= buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}

		return buf.append(name).append('=').append(value);
	}
	
	protected StringBuilder append(final StringBuilder buf, final String name, 
			final AbstractEntity<?> value) {
		int length= buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}
		buf.append(name).append("= {");
		value.toString(buf);

		return buf.append('}');
	}

	protected StringBuilder append(final StringBuilder buf, final String name, final Number value) {
		int length= buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}

		return buf.append(name).append("=").append(value);
	}

	protected StringBuilder append(final StringBuilder buf, final String name, final boolean value) {
		int length= buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}

		return buf.append(name).append("=").append(value);
	}

	protected <Q extends AbstractEntity<T>> StringBuilder append(final StringBuilder buf, 
			final Set<Q> entries) {
		int length= buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}

		buf.append('[');
		length = buf.length();
		for (final Q entry : entries) {
			if (length < buf.length()) {
				buf.append(", {");
			} else {
				buf.append('{');
			}
			entry.toString(buf)
				.append('}');
		}
		buf.append(']');

		return buf;
	}

	protected <Q extends Enum<Q>> StringBuilder appendEnum(final StringBuilder buf, 
			final Set<Q> entries) {
		int length = buf.length();

		if (length > 0 && buf.charAt(length - 1) != '{' && buf.charAt(length - 1) != '[') {
			buf.append(", ");
		}

		buf.append('[');
		length = buf.length();
		for (final Q entry : entries) {
			if (length < buf.length()) {
				buf.append(", ");
			}
			buf.append(entry.name());
		}
		buf.append(']');

		return buf;
	}

} // end class AbstractEntity
