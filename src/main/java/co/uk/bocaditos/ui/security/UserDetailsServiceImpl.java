package co.uk.bocaditos.ui.security;

import co.uk.bocaditos.ui.data.entity.User;
import co.uk.bocaditos.ui.data.service.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = this.userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(), 
        		getAuthorities(user));
    }

    private static List<GrantedAuthority> getAuthorities(final User user) {
        return user.getRoles().stream()
        		.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());
    }

} // end class UserDetailsServiceImpl
