package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;

import javax.validation.constraints.Pattern;

import org.springframework.util.ObjectUtils;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


/**
 * Phone number field view.
 * 
 * @author aasco
 *
 * @param <B> the bean type.
 */
public class ViewPhoneNumberField<B> extends CustomField<String> implements ViewField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6318988649181319389L;

	private ComboBox<String> countryCode = new ComboBox<>();
    private TextField number = new TextField();
    private final BaseViewdata data;
    private final String name;


    public ViewPhoneNumberField(final Binder<B> binder, final Field field, 
    		final StringBuilder path, final String lable) {
    	this.data = new BaseViewdata(this, field, path);
        setLabel(lable);
        this.name = this.data.getFieldName();

        this.countryCode.setWidth("120px");
        this.countryCode.setPlaceholder("Country");
        this.countryCode.setPattern("\\+\\d*");
        this.countryCode.setPreventInvalidInput(true);
        this.countryCode.setItems("+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225");
        this.countryCode.addCustomValueSetListener(e -> countryCode.setValue(e.getDetail()));

		// To say that a string field is only valid when it matches a certain regular expression
		final Pattern[] patters = this.data.getAnnotationsByType(Pattern.class);

        if (ObjectUtils.isEmpty(patters)) {
        	this.number.setPattern("\\d*");
        } else {
        	this.number.setPattern(patters[0].regexp());
        	if (!ObjectUtils.isEmpty(patters[0].message())) {
            	// TODO: also set 
        		
        	}
        }
        this.number.setPreventInvalidInput(true);

        final int length = path.length();
		final String placeholder = ViewField.getTranslation(this, path.append(ViewField.PLACEHOLDER));

		path.setLength(length);
		if (!ObjectUtils.isEmpty(placeholder)) {
			this.number.setPlaceholder(placeholder);
		}

        final HorizontalLayout layout = new HorizontalLayout(this.countryCode, this.number);

        layout.setFlexGrow(1.0, this.number);
        add(layout);
    }
    
    @Override
    public int getOrder() {
    	return this.data.getOrder();
    }
    
    @Override
    public String getValue() {
    	return generateModelValue();
    }

    @Override
    public void setValue(final String value) {
    	final String[] parts = value.split(" ");
    	int index = -1;

    	if (parts.length == 2) {
    		this.countryCode.setValue(parts[++index]);
    	}
    	this.number.setValue(parts[++index]);
    }
    
    public String getName() {
    	return this.name;
    }

    @Override
    protected String generateModelValue() {
        if (this.countryCode.getValue() != null) {
            return this.countryCode.getValue() + " " 
            		+ ((this.number.getValue() == null) ? "" : this.number.getValue());
        }

        return this.number.getValue();
    }

    @Override
    protected void setPresentationValue(final String phoneNumber) {
        final String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];

        if (parts.length == 1) {
        	this.countryCode.clear();
        	this.number.setValue(parts[0]);
        } else if (parts.length == 2) {
        	this.countryCode.setValue(parts[0]);
        	this.number.setValue(parts[1]);
        } else {
        	this.countryCode.clear();
        	this.number.clear();
        }
    }

} // end class ViewPhoneNumberField
