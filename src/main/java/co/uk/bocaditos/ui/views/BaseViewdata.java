package co.uk.bocaditos.ui.views;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;


/**
 * Base view data.
 * 
 * @author aasco
 * 
 * @param F the field bean.
 */
public class BaseViewdata {

	private final Field field;
	private final co.uk.bocaditos.ui.data.entity.Field strField;
	private final List<Annotation> annots;
	private final int order;
	private final String fieldName;


	BaseViewdata(final Component comp, final Field field, final StringBuilder path) {
		final int length = path.length();

		this.order = ViewField.getTranslation(0, comp, path.append(ViewField.ORDER));
		path.setLength(length);
		this.field = field;
		this.strField = null;
		this.annots = null;
		this.fieldName = path.substring(path.indexOf(".", "object.".length() + 1) + 1, 
				path.length()  -1);
	}

	@Size
	BaseViewdata(final Component comp, final co.uk.bocaditos.ui.data.entity.Field field, 
			final StringBuilder path) {
		final int length = path.length();

		this.order = ViewField.getTranslation(0, comp, path.append(ViewField.ORDER));
		path.setLength(length);
		this.field = null;
		this.strField = field;
		this.annots = new ArrayList<>();
		this.fieldName = path.substring(path.indexOf(".", "object.".length() + 1) + 1, 
				path.length()  -1);

		if (field.getRegExp() !=  null && !field.getRegExp().isEmpty()) {
			final Pattern annot = new Pattern() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return Pattern.class;
				}

				@Override
				public String regexp() {
					return field.getRegExp();
				}

				@Override
				public Flag[] flags() {
					return null;
				}

				@Override
				public String message() {
					return null;
				}

				@Override
				public Class<?>[] groups() {
					return null;
				}

				@Override
				public Class<? extends Payload>[] payload() {
					return null;
				}
				
			};

			this.annots.add(annot);
		}

		final Size annot = new Size() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Size.class;
			}

			@Override
			public String message() {
				return null;
			}

			@Override
			public Class<?>[] groups() {
				return null;
			}

			@Override
			public Class<? extends Payload>[] payload() {
				return null;
			}

			@Override
			public int min() {
				return field.getMinLength();
			}

			@Override
			public int max() {
				return field.getMaxLength();
			}
			
		};

		this.annots.add(annot);
	}
	
	public final Field getField() {
		return this.field;
	}
	
	public final co.uk.bocaditos.ui.data.entity.Field getStrField() {
		return this.strField;
	}
	
	public final int getOrder() {
		return this.order;
	}
	
	public final String getFieldName() {
		return this.fieldName;
	}
	
	public final Class<?> getFieldType() {
		return this.field.getType();
	}
	
	public final <T extends Annotation> T[] getAnnotationsByType(final Class<T> annotationClass) {
		return this.field != null ? this.field.getAnnotationsByType(annotationClass) 
				: getAnnotations(annotationClass);
	}

	@SuppressWarnings("unchecked")
	public <B, F> void build(final HasValue<?, F> view, final Binder<B> binder, 
			final List<Validator<F>> validators) {
		final BindingBuilder<B, F> builder = binder.forField(view);

	    for (final Validator<F> v: validators) {
	    	builder.withValidator(v);
	    }
	    if (this.field == null) { // It's a Map<String, String>
	    	builder.bind(// getter
	            list -> {return ((Map<String, F>) list).get(getFieldName());},
	            // setter
	            (list, fieldValue) -> {((Map<String, F>) list).put(getFieldName(), fieldValue);});
	    } else {
	    	builder.bind(getFieldName());
	    }
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Annotation> T[] getAnnotations(final Class<T> annotationClass) {
		if (annotationClass != null) {
			final List<T> result = new ArrayList<>(1);

			for (final Annotation annot : this.annots) {
				if (annotationClass == annot.getClass()) {
					result.add((T) annot);

					break;
				}
			}

			return result.toArray((T[]) Array.newInstance(annotationClass, result.size()));
		}

		return null;
	}

} // end class BaseViewdata
