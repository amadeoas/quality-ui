package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;


/**
 * View composed of three areas; title, content, and actions (buttons).
 */
@SuppressWarnings("serial")
@Tag("content-card")
public class CardBasic extends Div {

	private static final String TEXT_L = "text-l";


	public CardBasic(final String iconClass, final String title, final String subTitle, 
			final String content, final HtmlComponent... buttons) {
		add(setTitle(iconClass, title));
		add(new Hr());
		add(new CardBasicBody(subTitle, content));
		add(new Hr());
		add(setActions(buttons));

		getStyle().set("margin-top", "15px");
		getStyle().set("margin-right", "10px");
		getStyle().set("border", "1px solid lightgrey");
		getStyle().set("padding", "10px");
		getStyle().set("box-shadow", "5px 10px 8px #888888");
		getStyle().set("border-radius", "20px");
		setMaxWidth("350px");
		setMinWidth("200px");
	}
	
	private HorizontalLayout setTitle(final String iconClass, final String title) {
		final HorizontalLayout titleView = new HorizontalLayout();
	    final Span icon = new Span();

	    icon.addClassNames("me-s", TEXT_L);
	    if (iconClass != null && !iconClass.isEmpty()) {
	        icon.addClassNames(iconClass);
	    }

		titleView.add(
				icon, 
				new CardTitle(title) // if you don't want the title to wrap you can set the whitespace = nowrap
			);

		return titleView;
	}
	
	private Div setActions(final HtmlComponent... buttons) {
		final Div actionsView = new Div();

		for (final HasStyle button : buttons) {
			button.getStyle().set("background-color", "white"); // aqua
			button.getStyle().set("border", "1px solid lightgrey");
			button.getStyle().set("box-shadow", "2px 5px 4px #888888");
			button.getStyle().set("border-radius", "5px");
		}
		actionsView.add(buttons);
		actionsView.setWidth("100%");
		actionsView.getStyle()
                .set("padding", "var(--lumo-space-xs, 0.5em) calc(var(--lumo-space-s, 0.5em) * 1.3)")
                .set("box-sizing", "border-box");

		return actionsView;
	}

} // end class CardBasic
