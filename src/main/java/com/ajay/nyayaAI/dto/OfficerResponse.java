package com.ajay.nyayaAI.dto;
//new
import com.ajay.nyayaAI.model.Role;

public class OfficerResponse {

    private Long id;
    private String username;
    private String policeStation;
    private Role role;

    public OfficerResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
