package com.ajay.nyayaAI.repository;


import com.ajay.nyayaAI.model.Fir;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepo extends JpaRepository<Fir, Long> {

//    long countByStatus(String status);
    
//    List<Fir> findTop5ByOrderByCreatedAtDesc();
    
    long countByPoliceStation(String policeStation);

    long countByPoliceStationAndStatus(String policeStation, String status);

    List<Fir> findTop5ByPoliceStationOrderByCreatedAtDesc(String policeStation);

    List<Fir> findByPoliceStation(String policeStation, org.springframework.data.domain.Sort sort);

}
