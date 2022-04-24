package co.uk.bocaditos.ui.views.dataaccess;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import co.uk.bocaditos.ui.data.entity.SampleAddress;
import co.uk.bocaditos.ui.data.service.SampleAddressService;
import co.uk.bocaditos.ui.views.MainLayout;
import co.uk.bocaditos.ui.views.RestView;

import javax.annotation.security.PermitAll;

import org.springframework.core.env.Environment;


@PageTitle("Data Access")
@Route(value = "dataaccess", layout = MainLayout.class)
@PermitAll
public class DataAccessView extends RestView<SampleAddress, SampleAddress> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -84010690039833362L;


    public DataAccessView(final SampleAddressService addressService, final Environment env) 
    		throws SecurityException, NoSuchMethodException {
    	super(SampleAddress.class, addressService, env);
    }

} // end class DataAccessView
