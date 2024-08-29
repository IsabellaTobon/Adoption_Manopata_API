package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        // Search the user in the database
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + nickname));

        // Get user role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());

        // Return the user found in the database as a UserDetails object
        return new org.springframework.security.core.userdetails.User(user.getNickname(), user.getPassword(), Collections.singletonList(authority));
    }

}
