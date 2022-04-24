package co.uk.bocaditos.ui.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import co.uk.bocaditos.ui.data.entity.User;


@Service
public class UserService extends CrudService<User, Integer> {

    private UserRepository repository;


    public UserService(@Autowired final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    protected UserRepository getRepository() {
        return this.repository;
    }

} // end class UserService
