package co.uk.bocaditos.ui.views;


/**
 * .
 * 
 * @author aasco
 */
public class EntryPoint {

	private Route route;
	private Path path;


	public EntryPoint(final Route route, final Path path) {
		this.route = route;
		this.path = path;
	}
	
	public String getMethod() {
		return this.path.getMethod();
	}

	public Route getRoute() {
		return this.route;
	}

	public Path getPath() {
		return this.path;
	}
	
	public String getUri() {
		final StringBuilder buf = new StringBuilder();
		
		if (this.route != null) {
			buf.append(this.route.getValue());
		}
		if (this.path != null) {
			buf.append(this.path.getValue());
		}

		return buf.toString();
	}

	public void setRoute(final Route route) {
		this.route = route;
	}

	public void setPath(final Path path) {
		this.path = path;
	}

} // end class EntryPoint
