package com.cdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdm.domain.SedeUsuario;

public interface SedeUsuarioRepository extends JpaRepository<SedeUsuario, Integer>{
	
	public List<SedeUsuario> findByUsuario(String usuario);
	
}
