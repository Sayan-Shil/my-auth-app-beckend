package com.sayan.auth.myauthappbeckend.security;

import com.sayan.auth.myauthappbeckend.entities.User;
import com.sayan.auth.myauthappbeckend.exceptions.UserNotFoundException;
import com.sayan.auth.myauthappbeckend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username).orElseThrow(()-> new UserNotFoundException("Invalid Email"));
    }
}
