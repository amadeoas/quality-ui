package co.uk.bocaditos.ui.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import co.uk.bocaditos.ui.data.Role;
import co.uk.bocaditos.ui.data.entity.User;
import co.uk.bocaditos.ui.data.service.UserRepository;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(final PasswordEncoder passwordEncoder, 
    		final UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");

                return;
            }
            logger.info("Generating demo data...\nGenerating 2 User entities...");

            final User user = new User();

            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
//            user.setProfilePictureUrl("classpath:user.png");
//            user.setProfilePictureUrl(
//                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);

            final User admin = new User();

            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
//            user.setProfilePictureUrl("classpath:admin.png");
//            admin.setProfilePictureUrl(
//                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
//            admin.setRoles(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()));
            userRepository.save(admin);

            logger.info("Generated demo data");
        };
    }

}