package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.Sede;

public interface SedeRepository extends JpaRepository<Sede, String>{

	List<Sede> findByAudiencia(String estado);
	
}
