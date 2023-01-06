package com.cdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain.Especialidad;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, String>{

}
