package com.cdm.repository2;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain2.SunarpSolicitudVer;

@Repository
public interface SunarpSolicitudVerRepository extends JpaRepository<SunarpSolicitudVer, Integer>{
	
	List<SunarpSolicitudVer> findByIdSolicitudInAndUltimo(List<Integer> ids, String ultimo);
	
	List<SunarpSolicitudVer> findByIdSolicitudAndEstadoIn(Integer id, List<String> estados);
	
}
