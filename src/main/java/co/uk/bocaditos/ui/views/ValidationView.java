package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;


/**
 * Validation view.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
public class ValidationView extends VerticalLayout {

	private final TextArea textArea;
	private final TabsWithMain main;


	public ValidationView(final TabsWithMain main) {
		this.main = main;
        getStyle().set(EntryPointWrapper.WIDTH, "100%");
        getStyle().set(EntryPointWrapper.HEIGHT, "100%");
        this.textArea = new TextArea();
        this.textArea.setWidthFull();
        this.textArea.setHeightFull();
        this.textArea.setReadOnly(true);
		add(this.textArea);
        add(createButtonLayout());
    	setVisible(false);
	}
	
	public void clear() {
		setValue("");
	}

	public void setValue(final String value) {
		this.textArea.setValue(value);
		setVisible(value != null && !value.isEmpty());
		if (!isVisible()) {
			this.main.setMainTab(RestView.INDEX_ERRORS);
		}
	}

	private Component createButtonLayout() {
        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        final Button clear = new Button(getTranslation(ObjView.PROP_CLEAR));

        buttonsLayout.add(clear);
        clear.addClickListener(e -> clear());

        return buttonsLayout;
	}

} // end class ValidationView
