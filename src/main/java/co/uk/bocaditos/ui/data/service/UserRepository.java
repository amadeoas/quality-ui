package co.uk.bocaditos.ui.data.service;

import org.springframework.data.jpa.repository.JpaRepository;

import co.uk.bocaditos.ui.data.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

}