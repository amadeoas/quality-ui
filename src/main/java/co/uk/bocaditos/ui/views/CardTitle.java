package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Span;


/**
 * View of the title.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class CardTitle extends Span implements HasStyle, HasSize {

	public CardTitle(final String title) {
        super(title);
        init();
    }

    private void init() {
        getStyle().set("font-size", "var(--lumo-font-size-xl,var(--material-h5-font-size))")
                .set("text-overflow", "ellipsis")
                .set("overflow", "hidden")
                .set("font-weight", "500")
                .set("white-space", "nowrap");
        setWidth("100%");
	}

} // end class CardTitle
