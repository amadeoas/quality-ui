package co.uk.bocaditos.ui.views;

import java.util.Map;

import com.vaadin.flow.data.binder.BinderValidationStatus;


public interface RestInfo<Q> {

	public String getMethod(); 
	public String getUri();
	public Map<String, String> getUrlParams();
	public Map<String, String> getHeaders();
	public Q getRequest();
	
	public void setNewBean();
	public BinderValidationStatus<EntryPoint> validate();

} // end interface RestInfo
