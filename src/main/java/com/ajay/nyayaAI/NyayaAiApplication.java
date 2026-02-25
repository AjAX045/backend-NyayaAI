package com.ajay.nyayaAI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ajay.nyayaAI.model.PoliceOfficer;
import com.ajay.nyayaAI.model.Role;
import com.ajay.nyayaAI.repository.PoliceOfficerRepository;

@SpringBootApplication
public class NyayaAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyayaAiApplication.class, args);
	}
    
	@Bean
	CommandLineRunner runner(PoliceOfficerRepository repo,
	                         PasswordEncoder encoder) {
	    return args -> {
	        if (repo.findByUsername("admin").isEmpty()) {

	            PoliceOfficer officer = new PoliceOfficer();
	            officer.setUsername("admin");
	            officer.setPassword(encoder.encode("admin123"));
	            officer.setPoliceStation("Pune Station");
	            officer.setRole(Role.ROLE_ADMIN);

	            repo.save(officer);
	        }
	    };
	}



}
