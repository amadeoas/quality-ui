package co.uk.bocaditos.ui.views.home;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import co.uk.bocaditos.ui.views.CardBasic;
import co.uk.bocaditos.ui.views.MainLayout;
import co.uk.bocaditos.ui.views.ViewField;
import co.uk.bocaditos.ui.views.candidatesearch.CandidateSearchView;
import co.uk.bocaditos.ui.views.dataaccess.DataAccessView;
import co.uk.bocaditos.ui.views.quality.QualityView;


/**
 * Home view.
 * 
 * @author aasco
 */
@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6738049591257246449L;
	
	private static final String TITLE = "title";

	private final List<NativeButton> buttons;


	public HomeView() {
		this.buttons = new ArrayList<>(3);

		final Span overview 
				= new Span(MainLayout.getTranslation(this, this.getClass(), "overview"));
		final FlexLayout cardsView = new FlexLayout();
		final CardBasic[] cards = {
				buildBasicCard(CandidateSearchView.class),
				buildBasicCard(QualityView.class),
				buildBasicCard(DataAccessView.class)
			};

		overview.getElement().getStyle().set("font-size", "12px");
		setMargin(true);
		cardsView.add(cards);
		cardsView.setAlignContent(FlexLayout.ContentAlignment.STRETCH);
		cardsView.setFlexWrap(FlexLayout.FlexWrap.WRAP);

		add(overview, cardsView);
	}
	
	private CardBasic buildBasicCard(final Class<? extends Component> clazz) {
		final String title = MainLayout.getTranslation(this, clazz, TITLE);
		final String subTitle = MainLayout.getTranslation(this, clazz, ViewField.LABLE);
		final String description = MainLayout.getTranslation(this, clazz, "description");
		// https://vaadin.com/docs/v8/framework/advanced/advanced-navigator
		final NativeButton button = new NativeButton(MainLayout.buildNavButtonTxt(this, clazz));
		final String buttonTooltip = MainLayout.getTranslation(this, clazz, "button.tooltip", 
				MainLayout.getTitle(clazz));
		final CardBasic card;

		button.addClickListener(e ->
				button.getUI().ifPresent(ui -> ui.navigate(MainLayout.getRoute(clazz)))
			);
		if (buttonTooltip != null && !buttonTooltip.isEmpty()) {
			button.getElement().setProperty(TITLE, buttonTooltip);
		}
		card = new CardBasic(MainLayout.getIconClass(this, clazz, TITLE), title, 
				subTitle, description, 
				button);
		this.buttons.add(button);

		return card;
	}

} // end class HomeView
