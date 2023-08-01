package com.dit.airbnb.security.user;

import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.repository.UserRegRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRegRepository userRegRepository;

    /*
     * When a user tries to authenticate, this method receives the username,
     * searches the database for a record containing it
     * and (if found) returns an instance of User
     * otherwise an exception is thrown.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserReg user = userRegRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: \"" + username + "\" was not found.")
                );

        return UserDetailsImpl.create(user);
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
        UserReg user = userRegRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: \"" + id + "\" was not found.")
                );

        return UserDetailsImpl.create(user);
    }

}
