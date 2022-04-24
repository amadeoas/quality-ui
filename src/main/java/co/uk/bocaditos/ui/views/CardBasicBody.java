package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;


/**
 * Content of a card.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class CardBasicBody extends VerticalLayout {

    public CardBasicBody(final String title, final String content) {
    	add(new CardBasicBodyTitle(title));
    	add(new CardBasicContent(content));
        setPadding(true);
        setMargin(false);
    }

} // end class CardBasicBody
