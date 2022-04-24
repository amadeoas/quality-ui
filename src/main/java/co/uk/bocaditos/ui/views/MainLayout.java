package co.uk.bocaditos.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;

import co.uk.bocaditos.ui.data.entity.User;
import co.uk.bocaditos.ui.security.AuthenticatedUser;
import co.uk.bocaditos.ui.views.about.AboutView;
import co.uk.bocaditos.ui.views.candidatesearch.CandidateSearchView;
import co.uk.bocaditos.ui.views.dataaccess.DataAccessView;
import co.uk.bocaditos.ui.views.home.HomeView;
import co.uk.bocaditos.ui.views.quality.QualityView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.util.ObjectUtils;


/**
 * The main view is a top-level placeholder for other views.
 */
@PageTitle("Main")
public class MainLayout extends AppLayout {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8698131686457297616L;

	private static final String ITEM_CENTER = "items-center";
	private static final String TEXT_L = "text-l";
	private static final String TEXT_SECONDARY = "text-secondary";
	
	private static final String PROP_BUTTON_LABLE = "button.lable";
	private static final String PROP_NAV_TITLE = "navigator.title";
	private static final String PROP_NAV_LOGIN = "navigator.login.lable";
	private static final String PROP_NAV_LOGOUT = "navigator.logout.lable";
	// Navigator Menu
	private static final String PROP_NAV_MENU_TOGGLE = "navigator.menu.toggle";
	private static final String PROP_MENU_HOME = "navigator.menu.home";
	private static final String PROP_MENU_VANDIDATE_SEARCH = "navigator.menu.candidate_search";
	private static final String PROP_MENU_CLEANSE_MATCH = "navigator.menu.cleanse_match";
	private static final String PROP_MENU_DATA_ACCESS = "navigator.menu.data_access";
	private static final String PROP_MENU_ABOUT = "navigator.menu.about";


	public class MenuItemInfo {

        private final String text;
        private String iconClass;
        private final Class<? extends Component> view;


        public MenuItemInfo(final String text, final Class<? extends Component> viewClass) {
            this.text = text;
            this.iconClass = MainLayout.getIconClass(MainLayout.this, viewClass, "title");
            if (this.iconClass == null) {
            	this.iconClass = "";
            }
            this.view = viewClass;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    } // end class MenuItemInfo


    private H1 viewTitle;

    private transient AuthenticatedUser authenticatedUser;


    public MainLayout(final AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }
	
	public static String getRoute(final Class<? extends Component> clazz) {
		final Route route = clazz.getAnnotation(Route.class);

		return (route == null) ? null : route.value();
	}
	
	public static String getTitle(final Class<? extends Component> clazz) {
		final PageTitle title = clazz.getAnnotation(PageTitle.class);

		return title.value();
	}
	
	public static String buildNavButtonTxt(final Component comp, 
			final Class<? extends Component> clazz) {
		return getTranslation(comp, clazz, PROP_BUTTON_LABLE, getTitle(clazz));
	}
	
	public static String getTranslation(final Component comp, 
			final Class<? extends Component> clazz, final String name, final Object... params) {
		return comp.getTranslation("view." + getRoute(clazz) + "." + name, params);
	}
	
	public static String getTranslation(final Component comp, final String name, 
			final Object... params) {
		return comp.getTranslation("view." + getRoute(comp.getClass()) + "." + name, params);
	}
	
	public static String getIconClass(final Component comp, final Class<? extends Component> clazz, 
			final String baseName) {
		return MainLayout.getTranslation(comp, clazz, baseName + ".iconClass");
	}

	public static Component getIcon(final Component comp, final Class<? extends Component> clazz, 
			final String baseName) {
        final Span icon = new Span();
        final String iconClass = MainLayout.getIconClass(comp, clazz, baseName);

        icon.addClassNames("me-s", TEXT_L);
        if (iconClass != null && !iconClass.isEmpty()) {
            icon.addClassNames(iconClass);
        }
        
        return icon;
	}

    private Component createHeaderContent() {
        final DrawerToggle toggle = new DrawerToggle();

        toggle.addClassName(TEXT_SECONDARY);
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", getTranslation(PROP_NAV_MENU_TOGGLE));

        this.viewTitle = new H1();
        this.viewTitle.addClassNames("m-0", TEXT_L);

        final Header header = new Header(toggle, this.viewTitle);

        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", 
        		"h-xl", ITEM_CENTER, "w-full");

        return header;
    }

    private Component createDrawerContent() {
        final H2 appName = new H2(getTranslation(PROP_NAV_TITLE));
        final com.vaadin.flow.component.html.Section section 
        		= new com.vaadin.flow.component.html.Section(appName, 
        			createNavigation(), createFooter());

        appName.addClassNames("flex", ITEM_CENTER, "h-xl", "m-0", "px-m", "text-m");
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");

        return section;
    }

    private Nav createNavigation() {
        final Nav nav = new Nav();

        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        final UnorderedList list = new UnorderedList();

        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (final RouterLink link : createLinks()) {
            final ListItem item = new ListItem(link);

            list.add(item);
        }

        return nav;
    }

    private List<RouterLink> createLinks() {
        final MenuItemInfo[] menuItems = new MenuItemInfo[] { //
                new MenuItemInfo(getTranslation(PROP_MENU_HOME), HomeView.class),
                new MenuItemInfo(getTranslation(PROP_MENU_VANDIDATE_SEARCH), CandidateSearchView.class),
                new MenuItemInfo(getTranslation(PROP_MENU_CLEANSE_MATCH), QualityView.class),
                new MenuItemInfo(getTranslation(PROP_MENU_DATA_ACCESS), DataAccessView.class),
                new MenuItemInfo(getTranslation(PROP_MENU_ABOUT), AboutView.class)
        	};
        final List<RouterLink> links = new ArrayList<>();

        for (final MenuItemInfo menuItemInfo : menuItems) {
            links.add(createLink(menuItemInfo));
        }

        return links;
    }

    private static RouterLink createLink(final MenuItemInfo menuItemInfo) {
        final RouterLink link = new RouterLink();
        final Span icon = new Span();
        final Span text = new Span(menuItemInfo.getText());

        link.addClassNames("flex", "mx-s", "p-s", "relative", TEXT_SECONDARY);
        link.setRoute(menuItemInfo.getView());
        icon.addClassNames("me-s", TEXT_L);
        if (!menuItemInfo.getIconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.getIconClass());
        }
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);

        return link;
    }

    private Footer createFooter() {
        final Footer layout = new Footer();
        final Optional<User> maybeUser = this.authenticatedUser.get();

        layout.addClassNames("flex", ITEM_CENTER, "my-s", "px-m", "py-xs");
        if (maybeUser.isPresent()) {
            final User user = maybeUser.get();
            final String imgName = user.getProfilePictureUrl();
            final Avatar avatar;
            
            if (ObjectUtils.isEmpty(imgName)) {
            	avatar = new Avatar(user.getName());
            } else if (!ObjectUtils.isEmpty(imgName) && imgName.startsWith("classpath:")) {
            	final String theImgName = imgName.substring("classpath:".length());
            	final StreamResource imageResource = new StreamResource(
            			theImgName,
            			() -> getClass().getResourceAsStream("images/" + theImgName));

            	avatar = new Avatar(user.getName());
            	avatar.setImageResource(imageResource);
            } else {
            	avatar = new Avatar(user.getName(), imgName);
            }
            avatar.addClassNames("me-xs");

            final ContextMenu userMenu = new ContextMenu(avatar);

            userMenu.setOpenOnClick(true);
            userMenu.addItem(getTranslation(PROP_NAV_LOGOUT), e -> this.authenticatedUser.logout());

            final Span name = new Span(user.getName());

            name.addClassNames("font-medium", "text-s", TEXT_SECONDARY);

            layout.add(avatar, name);
        } else {
            final Anchor loginLink = new Anchor("login", getTranslation(PROP_NAV_LOGIN));

            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        this.viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        final PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);

        return (title == null) ? "" : title.value();
    }

} // end class MainLayout
