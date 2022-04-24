package co.uk.bocaditos.ui.data.service;

/**
 * Generic service for an object of type Q.
 * 
 * @author aasco
 *
 * @param <Q> The request bean type.
 * @param <P> The response bean type.
 */
public interface ObjService<Q, P> {

	public P update(WrapperRestData<Q> bean);

} // end interface ObjService
