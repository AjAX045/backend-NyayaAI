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
