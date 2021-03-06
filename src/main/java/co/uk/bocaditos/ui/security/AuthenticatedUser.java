package co.uk.bocaditos.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

import co.uk.bocaditos.ui.data.entity.User;
import co.uk.bocaditos.ui.data.service.UserRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;


@Component
public class AuthenticatedUser {

    @Autowired
    private UserRepository userRepository;


    private Optional<Authentication> getAuthentication() {
        final SecurityContext context = SecurityContextHolder.getContext();

        return Optional.ofNullable(context.getAuthentication())
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }

    public Optional<User> get() {
        return getAuthentication()
        		.map(authentication -> userRepository.findByUsername(authentication.getName()));
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);

        final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

} // end class AuthenticatedUser
