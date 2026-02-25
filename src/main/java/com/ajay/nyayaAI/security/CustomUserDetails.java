package com.ajay.nyayaAI.security;

import com.ajay.nyayaAI.model.PoliceOfficer;
import com.ajay.nyayaAI.model.Role;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String policeStation;
    private String role;
    private boolean active; //new
    
    public CustomUserDetails(PoliceOfficer officer) {
        this.username = officer.getUsername();
        this.password = officer.getPassword();
        this.policeStation = officer.getPoliceStation();
       // this.role = officer.getRole();
        this.role = officer.getRole().name();//new
        this.active = officer.isActive();//new

    }

    public String getPoliceStation() {
    	return policeStation;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; } //new
}
