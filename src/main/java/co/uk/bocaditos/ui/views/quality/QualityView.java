package co.uk.bocaditos.ui.views.quality;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import co.uk.bocaditos.ui.data.entity.SamplePerson;
import co.uk.bocaditos.ui.data.service.SamplePersonService;
import co.uk.bocaditos.ui.views.MainLayout;
import co.uk.bocaditos.ui.views.RestView;

import javax.annotation.security.PermitAll;

import org.springframework.core.env.Environment;


@PageTitle("Quality")
@Route(value = "quality", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class QualityView extends RestView<SamplePerson, SamplePerson> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9176810147113939103L;


    public QualityView(final SamplePersonService personService, final Environment env) 
    		throws SecurityException, NoSuchMethodException {
    	super(SamplePerson.class, personService, env);
    }

} // end class QualityView
