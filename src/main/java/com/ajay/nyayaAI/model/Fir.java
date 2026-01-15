package com.ajay.nyayaAI.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;


@Entity
@Table(name = "firs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Complainant details
    @Column(nullable = false)
    private String complainantName;

    @Column(nullable = false, length = 10)
    private String contactNumber;

    @Column(nullable = false)
    private String address;

    // Incident details
    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false)
    private String incidentTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String incidentType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String complaintText;

    // AI Prediction
    @Column(columnDefinition = "TEXT")
    private String predictedSections;
    
    @Column(nullable = false)
    private String status;


    // Metadata
    private LocalDateTime createdAt;
    
    @PrePersist
    public void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
          }
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(LocalDate incidentDate) {
		this.incidentDate = incidentDate;
	}

	public String getIncidentTime() {
		return incidentTime;
	}

	public void setIncidentTime(String incidentTime) {
		this.incidentTime = incidentTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getComplaintText() {
		return complaintText;
	}

	public void setComplaintText(String complaintText) {
		this.complaintText = complaintText;
	}

	public String getPredictedSections() {
		return predictedSections;
	}

	public void setPredictedSections(String predictedSections) {
		this.predictedSections = predictedSections;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    

}

