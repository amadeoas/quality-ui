package co.uk.bocaditos.ui.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;


/**
 * View supports for EntryPoint.
 * 
 * @author aasco
 */
public class EntryPointWrapper implements ValueChangeListener<ValueChangeEvent<Path>> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1676300349516962559L;
	
	static final String WIDTH  = "width";
	static final String HEIGHT = "height";

	private static final int INDEX_PATH = 1;

	private final TextField method;
    private final List<Select<?>> selects = new ArrayList<>(2); // top row
    private Values<Route> routeValues;
    private Values<Path> pathValues;
    private final Binder<EntryPoint> binder;
    private final SelectListener selectListener;


    EntryPointWrapper(final SelectListener selectListener) {
		this.method = new TextField();
		this.method.setLabel(this.method.getTranslation("view.general.method.lable"));
		this.method.setReadOnly(true);
		this.method.getStyle().set(WIDTH, "20%");
    	this.binder = new Binder<>(EntryPoint.class);
    	this.selectListener = selectListener;
    }

	@Override
	public void valueChanged(final ValueChangeEvent<Path> event) {
		update(event.getValue());
	}
    
    public TextField getMethodView() {
    	return this.method;
    }

    public final Binder<EntryPoint> getBinder() {
    	return this.binder;
    }
    
    public final void clear() {
    	this.binder.setBean(getNewBean());
    }
    
    public final String getMethod() {
    	return this.binder.getBean().getMethod();
    }
    
    public final EntryPoint getNewBean() {
        return new EntryPoint(this.routeValues.get(0), this.pathValues.get(0));
    }
    
    public final String getUri() {
    	return this.binder.getBean().getUri();
    }
    
    public final BinderValidationStatus<EntryPoint> validate() {
    	return this.binder.validate();
    }
    
    public final void setNewBean() {
    	this.binder.setBean(getNewBean());
    }
    
    public final void setEnabled(final boolean enabled) {
    	this.method.setEnabled(enabled);
    	for (final Select<?> select : this.selects) {
    		select.setEnabled(enabled);
    	}
    }
    
    @SuppressWarnings("unchecked")
	final void add(final Select<?> select) {
    	if (this.selects.size() == INDEX_PATH) {
    		// Assumes the path is the 2nd entry
    		((Select<Path>) select).addValueChangeListener(this);
    	}
    	this.selects.add(select);
    }
    
    @SuppressWarnings("unchecked")
	final void add(final Values<?> values) {
    	if (values == null || values.isEmpty()) {
    		return;
    	}
    	if (values.get(0) instanceof Route) {
    		this.routeValues = (Values<Route>) values;
    	} else {
    		this.pathValues = (Values<Path>) values;
    	}
    }
    
    final List<Select<?>> selects() {
    	return this.selects;
    }
	
	void update(final Path path) {
		this.method.setValue(path.getMethod());
		this.selectListener.update(path);
	}

} // end EntryPointWrapper
