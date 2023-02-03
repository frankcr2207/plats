package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.UsuarioInterno;

public interface UsuarioInternoRepository extends JpaRepository<UsuarioInterno, String>{
	
	List<UsuarioInterno> findByUsuarioInAndPerfil(List<String> ids, Integer perfil);
	
	List<UsuarioInterno> findByUsuarioIn(List<String> ids);
	
}
