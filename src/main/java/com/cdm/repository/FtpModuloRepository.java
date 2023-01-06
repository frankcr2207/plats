package com.cdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain.FtpModulo;

@Repository
public interface FtpModuloRepository extends JpaRepository<FtpModulo, Integer>{

}
