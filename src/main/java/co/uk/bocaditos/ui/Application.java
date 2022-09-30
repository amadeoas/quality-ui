package co.uk.bocaditos.ui;

import static java.lang.System.setProperty;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import co.uk.bocaditos.ui.views.Quality18NProvider;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop 
 * browsers.
 */
@SpringBootApplication
@Theme(value = "qualityui")
@PWA(name = "Quality UI", shortName = "Quality UI", offlineResources = {"images/logo.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8834006102602998633L;
	
	static {
	    SLF4JBridgeHandler.install();
	}


	public static void main(final String[] args) {
    	setProperty("vaadin.i18n.provider", Quality18NProvider.class.getName());
        SpringApplication.run(Application.class, args);
    }

} // end class Application
