package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Span;


/**
 * View of the content in a card.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class CardBasicContent extends Span implements HasStyle, HasSize {

    public CardBasicContent(final String content) {
        super(content);
        init();
    }

    private void init() {
        getStyle()
                .set("font-size", "var(--lumo-font-size-s, var(--material-small-font-size))")
                .set("text-overflow", "ellipsis")
                .set("overflow", "hidden")
                .set("overflow", "hidden")
                .set("color", "var(--lumo-secondary-text-color, var(--material-secondary-text-color))");
        setWidth("100%");
    }

} // end class CardBasicContent
