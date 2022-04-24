package co.uk.bocaditos.ui.data.service;

import org.springframework.stereotype.Service;

import co.uk.bocaditos.ui.data.entity.SampleAddress;


@Service
public class SampleAddressService implements ObjService<SampleAddress, SampleAddress> {
    
    @Override
    public SampleAddress update(final WrapperRestData<SampleAddress> value) {
    	return value.getRequest();
    }

} // end class SampleAddressService
