package co.uk.bocaditos.ui.data.service;

import org.springframework.stereotype.Service;

import co.uk.bocaditos.ui.data.entity.SamplePerson;


@Service
public class SamplePersonService implements ObjService<SamplePerson, SamplePerson> {
    
    @Override
    public SamplePerson update(final WrapperRestData<SamplePerson> value) {
    	return value.getRequest();
    }

} // end class SamplePersonService
