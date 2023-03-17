package com.cdm.repository2;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain2.SunarpSolicitudArchivo;

public interface SunarpSolicitudArchivoRepository extends JpaRepository<SunarpSolicitudArchivo, Integer>{

	List<SunarpSolicitudArchivo> findByIdSolicitud(Integer id);
	
}
