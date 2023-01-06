package com.cdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.RcSentenciado;

public interface RcSentenciadoRepository extends JpaRepository<RcSentenciado, Integer>{

	RcSentenciado findByDocumento(String documento);
	
}
