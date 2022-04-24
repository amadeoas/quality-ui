package co.uk.bocaditos.ui.views;

import java.lang.reflect.Field;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;


/**
 * .
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class OwnDetails extends Details implements ViewField {

	private static final String OPEN = "_open";
	// Default
	private static final boolean DEFAULT_OPEN = true;
	
	private final int fieldOrder;


	/**
	 * .
	 * 
	 * @author aasco
	 */
	class ViewSpan extends Span implements ViewField {

		@Override
		public int getOrder() {
			return OwnDetails.this.getOrder();
		}

	} // end class ViewSpan

	
	public OwnDetails(final Field field, final StringBuilder path, 
			final List<Component> subComponents) {
		super(subComponents.get(0).getTranslation(path.append(LABLE).toString(), field.getName()), 
				buildContent(subComponents));
		
		final int length = path.length() - LABLE.length();

		path.setLength(length);
		path.append(ViewField.ORDER);
		this.fieldOrder = ViewField.getTranslation(DEFAULT_ORDER, subComponents.get(0), path);
		path.setLength(length);
		path.append(OPEN);
		setOpened(ViewField.getTranslation(DEFAULT_OPEN, subComponents.get(0), path));
	}

	@Override
	public int getOrder() {
		return this.fieldOrder;
	}
	
	public Component getEmpty() {
		return new ViewSpan();
	}
	
	private static Component buildContent(final List<Component> subComponents) {
		final FormLayout subView = new FormLayout();

		subComponents.sort((o1, o2) -> ((ViewField) o1).getOrder() - ((ViewField) o2).getOrder());
		subView.add(subComponents.toArray(new Component[subComponents.size()]));

		return subView;
	}

} // end class OwnDetails
