package co.uk.bocaditos.ui.views.about;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import co.uk.bocaditos.ui.views.MainLayout;


@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4345790131075824990L;


	public AboutView() {
        setSpacing(false);

        final Image img = new Image("images/empty-plant.png", "placeholder plant");

        img.setWidth("200px");
        add(img);

        add(new H2(MainLayout.getTranslation(this, "title")));
        add(new Paragraph(MainLayout.getTranslation(this, "description")));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

} // end class AboutView
