package com.ajay.nyayaAI.dto;

import java.time.LocalDateTime;

public class FirListDto {
    private Long id;
    private String complainantName;
    private String incidentType;
    private String status;
    private LocalDateTime createdAt;

    public FirListDto(Long id, String complainantName, String incidentType, String status, LocalDateTime createdAt) {
        this.id = id;
        this.complainantName = complainantName;
        this.incidentType = incidentType;
        this.status = status;
        this.createdAt = createdAt;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComplainantName() {
		return complainantName;
	}

	public void setComplainantName(String complainantName) {
		this.complainantName = complainantName;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

   
}
