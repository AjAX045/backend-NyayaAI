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

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Fir getFir() {
		return fir;
	}

	public void setFir(Fir fir) {
		this.fir = fir;
	}

	@ManyToOne
    @JoinColumn(name = "fir_id")
    @JsonIgnore
    private Fir fir;
}


