package co.uk.bocaditos.ui.views.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import co.uk.bocaditos.ui.views.MainLayout;


@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6234002008901946031L;


	public LoginView() {
        setAction("login");

        final LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle(MainLayout.getTranslation(this, "title"));
        i18n.getHeader().setDescription(MainLayout.getTranslation(this, "description"));
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

} // end class LoginView
