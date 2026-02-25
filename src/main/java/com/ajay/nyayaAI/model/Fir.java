package com.ajay.nyayaAI.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "firs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fir {

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPoliceStation() {
		return policeStation;
	}

	public void setPoliceStation(String policeStation) {
		this.policeStation = policeStation;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getRelationToVictim() {
		return relationToVictim;
	}

	public void setRelationToVictim(String relationToVictim) {
		this.relationToVictim = relationToVictim;
	}

	public LocalDate getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(LocalDate incidentDate) {
		this.incidentDate = incidentDate;
	}

	public LocalTime getIncidentTime() {
		return incidentTime;
	}

	public void setIncidentTime(LocalTime incidentTime) {
		this.incidentTime = incidentTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPlaceOfOccurrence() {
		return placeOfOccurrence;
	}

	public void setPlaceOfOccurrence(String placeOfOccurrence) {
		this.placeOfOccurrence = placeOfOccurrence;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Accused> getAccusedList() {
		return accusedList;
	}

	public void setAccusedList(List<Accused> accusedList) {
		this.accusedList = accusedList;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // FIR Basic Details
    // ===============================

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String policeStation;

    private Integer year;

    // 🔹 Auto-derived from incidentDate
    private String day;

    // ===============================
    // Complainant Details
    // ===============================

    @Column(nullable = false)
    private String complainantName;

    @Column(nullable = false, length = 10)
    private String contactNumber;

    @Column(nullable = false)
    private String address;

    private String occupation;

    private String relationToVictim;

    // ===============================
    // Incident Details
    // ===============================

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false)
    private LocalTime incidentTime;

    @Column(nullable = false)
    private String location;

    private String placeOfOccurrence;

    @Column(nullable = false)
    private String incidentType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String complaintText;

    // ===============================
    // AI Prediction
    // ===============================

    @Column(columnDefinition = "TEXT")
    private String predictedSections;

    @Column(nullable = false)
    private String status;

    // ===============================
    // One FIR → Many Accused
    // ===============================

    @OneToMany(mappedBy = "fir", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accused> accusedList;

    // ===============================
    // Metadata
    // ===============================

    private LocalDateTime createdAt;

    // ===============================
    // Lifecycle Hooks
    // ===============================

    @PrePersist
    @PreUpdate
    public void onSave() {

        this.createdAt = (this.createdAt == null) ? LocalDateTime.now() : this.createdAt;
        this.year = (this.year == null) ? LocalDate.now().getYear() : this.year;

        if (this.status == null) {
            this.status = "PENDING";
        }

        // 🔥 Auto-calculate Day from incidentDate
        if (this.incidentDate != null) {
            this.day = this.incidentDate
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }
    }
}
