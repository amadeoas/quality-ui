package co.uk.bocaditos.ui.views;

import java.text.MessageFormat;


/**
 * .
 * 
 * @author aasco
 */
public class ViewException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6308488328453219312L;


	public ViewException(final String msgFormat, final Object... prams) {
		this(null, msgFormat, prams);
	}

	public ViewException(final Throwable thrown, final String msgFormat, final Object... prams) {
		super(MessageFormat.format(msgFormat, prams), thrown);
	}

} // end class ViewException
