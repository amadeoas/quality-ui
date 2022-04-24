package co.uk.bocaditos.ui.views;

import java.util.List;

import co.uk.bocaditos.ui.data.entity.JsonComparator.ErrorMsg;


/**
 * .
 * 
 * @author aasco
 */
public interface ValidationErrorListener {

	public void processErrors(final List<ErrorMsg> errors);

} // end class ValidationErrorListener
