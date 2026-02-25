package com.ajay.nyayaAI.dto;
//new
import com.ajay.nyayaAI.model.Role;

public class CreateOfficerRequest {

    private String username;
    private String password;
    private String policeStation;
    private Role role;

    public CreateOfficerRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
