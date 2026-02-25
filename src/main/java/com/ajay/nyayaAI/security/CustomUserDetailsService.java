package com.ajay.nyayaAI.security;

import com.ajay.nyayaAI.model.PoliceOfficer;
import com.ajay.nyayaAI.repository.PoliceOfficerRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PoliceOfficerRepository repository;

    public CustomUserDetailsService(PoliceOfficerRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        PoliceOfficer officer = repository.findByUsername(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found")
                );

        return new CustomUserDetails(officer);
    }
}
