package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Span;


/**
 * View of the title of a content in a card.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class CardBasicBodyTitle  extends Span implements HasStyle, HasSize {

    public CardBasicBodyTitle(final String title) {
        super(title);
        init();
    }

    private void init() {
        getStyle()
                .set("font-size", "var(--lumo-font-size-l, var(--material-h6-font-size))")
                .set("text-overflow", "ellipsis")
                .set("overflow", "hidden")
                .set("font-weight", "500");
        setWidth("100%");
    }

} // end class CardBasicBodyTitle
