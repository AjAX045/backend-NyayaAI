package com.ajay.nyayaAI.repository;

import com.ajay.nyayaAI.model.Fir;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirRepository extends JpaRepository<Fir, Long> {
	
	List<Fir> findByPoliceStation(String policeStation);
	
}

