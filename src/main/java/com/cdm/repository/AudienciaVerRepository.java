package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.AudienciaVer;

public interface AudienciaVerRepository extends JpaRepository<AudienciaVer, Integer>{

	List<AudienciaVer> findByIdAudienciaOrderByVersionDesc(Integer id);
	
}
