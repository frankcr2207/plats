package com.cdm.repository2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain2.RecPartida;

@Repository
public interface RecPartidaRepository extends JpaRepository<RecPartida, Integer>{
	public List<RecPartida> findByEstadoInAndSedeIn(List<String> estado, List<String> sede);
	
	public List<RecPartida> findByFechaSistemaBetweenAndSedeIn(LocalDateTime inicio, LocalDateTime fin, List<String> sede);
	
}
