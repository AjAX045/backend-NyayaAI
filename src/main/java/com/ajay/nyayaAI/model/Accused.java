package com.ajay.nyayaAI.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "accused")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accused {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @ManyToOne
    @JoinColumn(name = "fir_id")
    @JsonIgnore
    private Fir fir;
}


