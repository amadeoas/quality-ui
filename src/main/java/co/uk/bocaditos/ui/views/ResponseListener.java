package co.uk.bocaditos.ui.views;


/**
 * .
 * 
 * @author aasco
 *
 * @param <P> the response type.
 */
public interface ResponseListener<P> {

	public void change(P response);
	
	public void clearForm();

} // end interface ResponseListener
