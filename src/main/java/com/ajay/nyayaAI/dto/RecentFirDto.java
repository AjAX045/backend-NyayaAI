package com.ajay.nyayaAI.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RecentFirDto {

    private Long id;
    private String complainantName;
    private String incidentType;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public RecentFirDto(Long id, String complainantName, String incidentType, String status, LocalDateTime createdAt) {
        this.id = id;
        this.complainantName = complainantName;
        this.incidentType = incidentType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
